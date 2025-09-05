package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HSETIOfficialNoticeServiceTest {

    @InjectMocks
    private HSETIOfficialNoticeService officialNoticeService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private OfficialNoticeSendService officialNoticeSendService;

    @Test
    void sendOfficialNotice(){
        String requestId = "1";
        FileInfoDTO officialDocFileInfoDTO = FileInfoDTO.builder()
                .name("offDoc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();

        DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of("operator"))
                .signatory("signatory").build();
        HSETIRequestPayload requestPayload = HSETIRequestPayload.builder().decisionNotification(decisionNotification)
                .officialNotice(officialDocFileInfoDTO)
                .build();
        Request request = Request.builder().id(requestId).payload(requestPayload).build();
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
                .thenReturn(decisionNotificationUserEmails);

        officialNoticeService.sendOfficialNotice(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(officialNoticeSendService, times(1))
                .sendOfficialNotice(List.of(requestPayload.getOfficialNotice()), request, decisionNotificationUserEmails);
    }

    @Test
    void generateOfficialNoticeApproved() {
        String requestId = "1";
        String fileName = "HSE_target_increase_application_approved.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();
        HSETIRequestPayload requestPayload = HSETIRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .build();
        Request request = Request.builder().payload(requestPayload).build();
        UserInfoDTO accountPrimaryContactInfo = UserInfoDTO.builder().email("user@pmrv.uk").build();
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
                DocumentTemplateParamsSourceData.builder()
                        .contextActionType(DocumentTemplateGenerationContextActionType.HSE_TI_COMPLETED)
                        .request(request)
                        .signatory(decisionNotification.getSignatory())
                        .accountPrimaryContact(accountPrimaryContactInfo)
                        .toRecipientEmail(accountPrimaryContactInfo.getEmail())
                        .ccRecipientsEmails(decisionNotificationUserEmails)
                        .build();
        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = FileInfoDTO.builder()
                .name("offDoc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
                .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
                .thenReturn(decisionNotificationUserEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
                .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocument(
                DocumentTemplateType.HSE_TI_COMPLETED, templateParams, fileName))
                .thenReturn(officialDocFileInfoDTO);


        // Invoke
        officialNoticeService.generateOfficialNoticeApproved(requestId);

        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocument(
                DocumentTemplateType.HSE_TI_COMPLETED, templateParams, fileName);
    }

    @Test
    void generateOfficialNoticeRejected() {
        String requestId = "1";
        String fileName = "HSE_target_increase_application_rejected.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();
        HSETIRequestPayload requestPayload = HSETIRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .build();
        Request request = Request.builder().payload(requestPayload).build();
        UserInfoDTO accountPrimaryContactInfo = UserInfoDTO.builder().email("user@pmrv.uk").build();
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
                DocumentTemplateParamsSourceData.builder()
                        .contextActionType(DocumentTemplateGenerationContextActionType.HSE_TI_COMPLETED)
                        .request(request)
                        .signatory(decisionNotification.getSignatory())
                        .accountPrimaryContact(accountPrimaryContactInfo)
                        .toRecipientEmail(accountPrimaryContactInfo.getEmail())
                        .ccRecipientsEmails(decisionNotificationUserEmails)
                        .build();
        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = FileInfoDTO.builder()
                .name("offDoc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
                .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
                .thenReturn(decisionNotificationUserEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
                .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocument(
                DocumentTemplateType.HSE_TI_COMPLETED, templateParams, fileName))
                .thenReturn(officialDocFileInfoDTO);


        // Invoke
        officialNoticeService.generateOfficialNoticeRejected(requestId);

        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocument(
                DocumentTemplateType.HSE_TI_COMPLETED, templateParams, fileName);
    }

    @Test
    void generateOfficialNoticeWithdrawn() {
        String requestId = "1";
        String fileName = "HSE_target_increase_application_withdrawn.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();
        HSETIRequestPayload requestPayload = HSETIRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .build();
        Request request = Request.builder().payload(requestPayload).build();
        UserInfoDTO accountPrimaryContactInfo = UserInfoDTO.builder().email("user@pmrv.uk").build();
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
                DocumentTemplateParamsSourceData.builder()
                        .contextActionType(DocumentTemplateGenerationContextActionType.HSE_TI_COMPLETED)
                        .request(request)
                        .signatory(decisionNotification.getSignatory())
                        .accountPrimaryContact(accountPrimaryContactInfo)
                        .toRecipientEmail(accountPrimaryContactInfo.getEmail())
                        .ccRecipientsEmails(decisionNotificationUserEmails)
                        .build();
        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = FileInfoDTO.builder()
                .name("offDoc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
                .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
                .thenReturn(decisionNotificationUserEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
                .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocument(
                DocumentTemplateType.HSE_TI_COMPLETED, templateParams, fileName))
                .thenReturn(officialDocFileInfoDTO);


        // Invoke
        officialNoticeService.generateOfficialNoticeWithdrawn(requestId);

        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocument(
                DocumentTemplateType.HSE_TI_COMPLETED, templateParams, fileName);
    }

    @Test
    void generateOfficialNoticeDeemedWithdrawn() {
        String requestId = "1";
        String fileName = "HSE_target_increase_application_deemed_withdrawn.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();
        HSETIRequestPayload requestPayload = HSETIRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .build();
        Request request = Request.builder().payload(requestPayload).build();
        UserInfoDTO accountPrimaryContactInfo = UserInfoDTO.builder().email("user@pmrv.uk").build();
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
                DocumentTemplateParamsSourceData.builder()
                        .contextActionType(DocumentTemplateGenerationContextActionType.HSE_TI_COMPLETED)
                        .request(request)
                        .signatory(decisionNotification.getSignatory())
                        .accountPrimaryContact(accountPrimaryContactInfo)
                        .toRecipientEmail(accountPrimaryContactInfo.getEmail())
                        .ccRecipientsEmails(decisionNotificationUserEmails)
                        .build();
        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = FileInfoDTO.builder()
                .name("offDoc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
                .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
                .thenReturn(decisionNotificationUserEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
                .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocument(
                DocumentTemplateType.HSE_TI_COMPLETED, templateParams, fileName))
                .thenReturn(officialDocFileInfoDTO);


        // Invoke
        officialNoticeService.generateOfficialNoticeDeemedWithdrawn(requestId);

        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocument(
                DocumentTemplateType.HSE_TI_COMPLETED, templateParams, fileName);
    }
}
