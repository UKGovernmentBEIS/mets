package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.config.RegistryConfig;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceOfficialNoticeServiceTest {

    @InjectMocks
    private PermitIssuanceOfficialNoticeService officialNoticeService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private RequestService requestService;

    @Mock
    private DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private OfficialNoticeSendService officialNoticeSendService;
    
    @Mock
    private RegistryConfig registryConfig;

    @Test
    void generateGrantedOfficialNotice_GHGE() throws InterruptedException, ExecutionException {
        String requestId = "1";
        String fileName = "GHGE_permit_application_approved.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .permitType(PermitType.GHGE)
            .decisionNotification(decisionNotification)
            .build();
        Request request = Request.builder().payload(requestPayload).build();

        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();

        UserInfoDTO accountPrimaryContactInfo = UserInfoDTO.builder().email("user@pmrv.uk").build();
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
            DocumentTemplateParamsSourceData.builder()
                .contextActionType(DocumentTemplateGenerationContextActionType.PERMIT_ISSUANCE_GHGE_GRANTED)
                .request(request)
                .signatory(decisionNotification.getSignatory())
                .accountPrimaryContact(accountPrimaryContactInfo)
                .toRecipientEmail(accountPrimaryContactInfo.getEmail())
                .ccRecipientsEmails(decisionNotificationUserEmails)
                .build();

        when(requestService.findRequestById(requestId))
            .thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
            .thenReturn(decisionNotificationUserEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
            .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocumentAsync(
            DocumentTemplateType.PERMIT_ISSUANCE_GHGE_ACCEPTED, templateParams, fileName))
            .thenReturn(CompletableFuture.completedFuture(officialDocFileInfoDTO));

        // Invoke
        CompletableFuture<FileInfoDTO> result = officialNoticeService.generateGrantedOfficialNotice(requestId);
        
        assertThat(result.get()).isEqualTo(officialDocFileInfoDTO);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocumentAsync(
            DocumentTemplateType.PERMIT_ISSUANCE_GHGE_ACCEPTED, templateParams, fileName);
    }

    @Test
    void generateGrantedOfficialNotice_HSE() throws InterruptedException, ExecutionException {
        String requestId = "1";
        String fileName = "HSE_permit_application_approved.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .permitType(PermitType.HSE)
            .decisionNotification(decisionNotification)
            .build();
        Request request = Request.builder().payload(requestPayload).build();

        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();

        UserInfoDTO accountPrimaryContactInfo = UserInfoDTO.builder().email("user@pmrv.uk").build();
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
            DocumentTemplateParamsSourceData.builder()
                .contextActionType(DocumentTemplateGenerationContextActionType.PERMIT_ISSUANCE_HSE_GRANTED)
                .request(request)
                .signatory(decisionNotification.getSignatory())
                .accountPrimaryContact(accountPrimaryContactInfo)
                .toRecipientEmail(accountPrimaryContactInfo.getEmail())
                .ccRecipientsEmails(decisionNotificationUserEmails)
                .build();

        when(requestService.findRequestById(requestId))
            .thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
            .thenReturn(decisionNotificationUserEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
            .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocumentAsync(
            DocumentTemplateType.PERMIT_ISSUANCE_HSE_ACCEPTED, templateParams, fileName))
            .thenReturn(CompletableFuture.completedFuture(officialDocFileInfoDTO));

        // Invoke
        CompletableFuture<FileInfoDTO> result = officialNoticeService.generateGrantedOfficialNotice(requestId);

        assertThat(result.get()).isEqualTo(officialDocFileInfoDTO);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocumentAsync(
            DocumentTemplateType.PERMIT_ISSUANCE_HSE_ACCEPTED, templateParams, fileName);
    }

    @Test
    void generateGrantedOfficialNotice_WASTE() throws InterruptedException, ExecutionException {
        String requestId = "1";
        String fileName = "WASTE_permit_application_approved.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .permitType(PermitType.WASTE)
            .decisionNotification(decisionNotification)
            .build();
        Request request = Request.builder().payload(requestPayload).build();

        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();

        UserInfoDTO accountPrimaryContactInfo = UserInfoDTO.builder().email("user@pmrv.uk").build();
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
            DocumentTemplateParamsSourceData.builder()
                .contextActionType(DocumentTemplateGenerationContextActionType.PERMIT_ISSUANCE_WASTE_GRANTED)
                .request(request)
                .signatory(decisionNotification.getSignatory())
                .accountPrimaryContact(accountPrimaryContactInfo)
                .toRecipientEmail(accountPrimaryContactInfo.getEmail())
                .ccRecipientsEmails(decisionNotificationUserEmails)
                .build();

        when(requestService.findRequestById(requestId))
            .thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
            .thenReturn(decisionNotificationUserEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
            .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocumentAsync(
            DocumentTemplateType.PERMIT_ISSUANCE_WASTE_ACCEPTED, templateParams, fileName))
            .thenReturn(CompletableFuture.completedFuture(officialDocFileInfoDTO));

        // Invoke
        CompletableFuture<FileInfoDTO> result = officialNoticeService.generateGrantedOfficialNotice(requestId);

        assertThat(result.get()).isEqualTo(officialDocFileInfoDTO);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocumentAsync(
            DocumentTemplateType.PERMIT_ISSUANCE_WASTE_ACCEPTED, templateParams, fileName);
    }

    @Test
    void generateAndSaveRejectedOfficialNotice() {
        String requestId = "1";
        String fileName = "permit_application_rejection_notice.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .build();
        Request request = Request.builder().payload(requestPayload).build();

        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();

        UserInfoDTO accountPrimaryContactInfo = UserInfoDTO.builder().email("user@pmrv.uk").build();
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
                DocumentTemplateParamsSourceData.builder()
                        .contextActionType(DocumentTemplateGenerationContextActionType.PERMIT_ISSUANCE_REJECTED)
                        .request(request)
                        .signatory(decisionNotification.getSignatory())
                        .accountPrimaryContact(accountPrimaryContactInfo)
                        .toRecipientEmail(accountPrimaryContactInfo.getEmail())
                        .ccRecipientsEmails(decisionNotificationUserEmails)
                        .build();

        when(requestService.findRequestById(requestId))
                .thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
                .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
                .thenReturn(decisionNotificationUserEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
                .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocument(
                DocumentTemplateType.PERMIT_ISSUANCE_REJECTED, templateParams, fileName))
                .thenReturn(officialDocFileInfoDTO);

        // Invoke
        officialNoticeService.generateAndSaveRejectedOfficialNotice(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocument(
                DocumentTemplateType.PERMIT_ISSUANCE_REJECTED, templateParams, fileName);

        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
    }

    @Test
    void generateAndSaveDeemedWithdrawnOfficialNotice() {
        String requestId = "1";
        String fileName = "permit_application_deemed_withdrawn_notice.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();
        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .build();
        Request request = Request.builder().payload(requestPayload).build();

        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();

        UserInfoDTO accountPrimaryContactInfo = UserInfoDTO.builder().email("user@pmrv.uk").build();
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
                DocumentTemplateParamsSourceData.builder()
                        .contextActionType(DocumentTemplateGenerationContextActionType.PERMIT_ISSUANCE_DEEMED_WITHDRAWN)
                        .request(request)
                        .signatory(decisionNotification.getSignatory())
                        .accountPrimaryContact(accountPrimaryContactInfo)
                        .toRecipientEmail(accountPrimaryContactInfo.getEmail())
                        .ccRecipientsEmails(decisionNotificationUserEmails)
                        .build();

        when(requestService.findRequestById(requestId))
                .thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
                .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
                .thenReturn(decisionNotificationUserEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
                .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocument(
                DocumentTemplateType.PERMIT_ISSUANCE_DEEMED_WITHDRAWN, templateParams, fileName))
                .thenReturn(officialDocFileInfoDTO);

        // Invoke
        officialNoticeService.generateAndSaveDeemedWithdrawnOfficialNotice(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocument(
                DocumentTemplateType.PERMIT_ISSUANCE_DEEMED_WITHDRAWN, templateParams, fileName);

        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
    }

    @Test
    void sendOfficialNotice() {
        String requestId = "1";

        DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUser"))
                .signatory("signatoryUser")
                .build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();

        Request request = Request.builder()
                .id(requestId)
                .payload(PermitIssuanceRequestPayload.builder()
                        .decisionNotification(decisionNotification)
                        .officialNotice(officialDocFileInfoDTO)
                        .build())
                .build();

        List<String> decisionRecipientsEmails = List.of("operator1@email");

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(decisionNotificationUsersService.findUserEmails(decisionNotification)).thenReturn(decisionRecipientsEmails);
        when(registryConfig.getEmail()).thenReturn("registry@email");

        officialNoticeService.sendOfficialNotice(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(officialNoticeSendService, times(1)).sendOfficialNotice(List.of(officialDocFileInfoDTO), request, decisionRecipientsEmails, List.of("registry@email"));
    }

    private FileInfoDTO buildOfficialFileInfo() {
        return FileInfoDTO.builder()
                .name("offDoc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
    }
}
