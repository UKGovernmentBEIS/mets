package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service.InstallationAuditOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InstallationOnsiteInspectionOfficialNoticeServiceTest {


    @InjectMocks
    private InstallationOnsiteInspectionOfficialNoticeService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;



    @Test
    void generateInstallationOnsiteInspectionSubmittedOfficialNotice() throws InterruptedException, ExecutionException {
        String requestId = "1";
        String fileName = "INSTALLATION_ONSITE_INSPECTION_notice.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();
        InstallationInspectionRequestPayload requestPayload = InstallationInspectionRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .build();
        Request request = Request.builder().payload(requestPayload).build();
        UserInfoDTO accountPrimaryContactInfo = UserInfoDTO.builder().email("user@pmrv.uk").build();
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
                DocumentTemplateParamsSourceData.builder()
                        .contextActionType(DocumentTemplateGenerationContextActionType.INSTALLATION_ONSITE_INSPECTION_SUBMITTED)
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
                DocumentTemplateType.INSTALLATION_ONSITE_INSPECTION_SUBMITTED, templateParams, fileName))
                .thenReturn(officialDocFileInfoDTO);


        // Invoke
        service.generateInstallationOnsiteInspectionSubmittedOfficialNotice(requestId);

        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocument(
                DocumentTemplateType.INSTALLATION_ONSITE_INSPECTION_SUBMITTED, templateParams, fileName);
    }


}
