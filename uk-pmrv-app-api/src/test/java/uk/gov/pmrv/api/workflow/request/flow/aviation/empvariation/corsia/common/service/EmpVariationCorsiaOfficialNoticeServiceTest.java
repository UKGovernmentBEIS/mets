package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaOfficialNoticeServiceTest {

	@InjectMocks
    private EmpVariationCorsiaOfficialNoticeService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;

    @Mock
    private OfficialNoticeSendService officialNoticeSendService;

    @Test
    void generateApprovedOfficialNotice() throws InterruptedException, ExecutionException {
        String requestId = "1";
        Long accountId = 1L ;
        String fileName = "corsia_emp_variation_approved.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();
        EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
            .decisionNotification(decisionNotification)
            .build();
        Request request = Request.builder().payload(requestPayload).accountId(accountId).build();

        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();

        UserInfoDTO accountPrimaryContactInfo = buildUserInfo("primaryContact@pmrv.uk");
        UserInfoDTO serviceContactInfo = buildUserInfo("serviceContact@pmrv.uk");
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
        EmissionsMonitoringPlanCorsiaDTO empDto = EmissionsMonitoringPlanCorsiaDTO
            .builder()
            .consolidationNumber(5)
            .build();
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
            DocumentTemplateParamsSourceData.builder()
                .contextActionType(DocumentTemplateGenerationContextActionType.EMP_VARIATION_CORSIA_ACCEPTED)
                .request(request)
                .signatory(decisionNotification.getSignatory())
                .accountPrimaryContact(accountPrimaryContactInfo)
                .toRecipientEmail(serviceContactInfo.getEmail())
                .ccRecipientsEmails(decisionNotificationUserEmails)
                .build();

        when(requestService.findRequestById(requestId))
            .thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(requestAccountContactQueryService.getRequestAccountServiceContact(request))
            .thenReturn(Optional.of(serviceContactInfo));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
            .thenReturn(decisionNotificationUserEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
            .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocumentAsync(
            DocumentTemplateType.EMP_VARIATION_CORSIA_ACCEPTED, templateParams, fileName))
            .thenReturn(CompletableFuture.completedFuture(officialDocFileInfoDTO));

        // Invoke
        CompletableFuture<FileInfoDTO> result = service.generateApprovedOfficialNotice(requestId);

        // Verify
        assertThat(result.get()).isEqualTo(officialDocFileInfoDTO);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountServiceContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocumentAsync(
            DocumentTemplateType.EMP_VARIATION_CORSIA_ACCEPTED, templateParams, fileName);
    }
    
    @Test
    void generateApprovedOfficialNoticeRegulatorLed() throws InterruptedException, ExecutionException {
    	String requestId = "1";
        Long accountId = 1L ;
        String fileName = "corsia_emp_ca_variation_approved.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();
        EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .build();
        Request request = Request.builder().payload(requestPayload).accountId(accountId).build();

        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();

        UserInfoDTO accountPrimaryContactInfo = buildUserInfo("primaryContact@pmrv.uk");
        UserInfoDTO serviceContactInfo = buildUserInfo("serviceContact@pmrv.uk");
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
        EmissionsMonitoringPlanCorsiaDTO empDto = EmissionsMonitoringPlanCorsiaDTO
        		.builder()
        		.consolidationNumber(5)
        		.build();
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
                DocumentTemplateParamsSourceData.builder()
                        .contextActionType(DocumentTemplateGenerationContextActionType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPROVED)
                        .request(request)
                        .signatory(decisionNotification.getSignatory())
                        .accountPrimaryContact(accountPrimaryContactInfo)
                        .toRecipientEmail(serviceContactInfo.getEmail())
                        .ccRecipientsEmails(decisionNotificationUserEmails)
                        .build();

        when(requestService.findRequestById(requestId))
                .thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
                .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(requestAccountContactQueryService.getRequestAccountServiceContact(request))
        		.thenReturn(Optional.of(serviceContactInfo));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
                .thenReturn(decisionNotificationUserEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
                .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocumentAsync(
                DocumentTemplateType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPROVED, templateParams, fileName))
                .thenReturn(CompletableFuture.completedFuture(officialDocFileInfoDTO));

        // Invoke
        CompletableFuture<FileInfoDTO> result = service.generateApprovedOfficialNoticeRegulatorLed(requestId);  

        // Verify
        assertThat(result.get()).isEqualTo(officialDocFileInfoDTO);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountServiceContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocumentAsync(
                DocumentTemplateType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPROVED, templateParams, fileName);
    }

    @Test
    void generateAndSaveRejectedOfficialNotice() {
        String requestId = "1";
        String fileName = "corsia_emp_variation_withdrawn.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();
        EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
            .decisionNotification(decisionNotification)
            .build();
        Request request = Request.builder().payload(requestPayload).build();

        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();

        UserInfoDTO accountPrimaryContactInfo = buildUserInfo("primaryContact@pmrv.uk");
        UserInfoDTO serviceContactInfo = buildUserInfo("serviceContact@pmrv.uk");
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
            DocumentTemplateParamsSourceData.builder()
                .contextActionType(null)
                .request(request)
                .signatory(decisionNotification.getSignatory())
                .accountPrimaryContact(accountPrimaryContactInfo)
                .toRecipientEmail(serviceContactInfo.getEmail())
                .ccRecipientsEmails(decisionNotificationUserEmails)
                .build();

        when(requestService.findRequestById(requestId))
            .thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(requestAccountContactQueryService.getRequestAccountServiceContact(request))
            .thenReturn(Optional.of(serviceContactInfo));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
            .thenReturn(decisionNotificationUserEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
            .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocument(
            DocumentTemplateType.EMP_VARIATION_CORSIA_DEEMED_WITHDRAWN, templateParams, fileName))
            .thenReturn(officialDocFileInfoDTO);

        // Invoke
        service.generateAndSaveDeemedWithdrawnOfficialNotice(requestId);

        // Verify
        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocument(
            DocumentTemplateType.EMP_VARIATION_CORSIA_DEEMED_WITHDRAWN, templateParams, fileName);
    }

    @Test
    void generateAndSaveDeemedWithdrawnOfficialNotice() {
        String requestId = "1";
        String fileName = "corsia_emp_variation_rejected.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();
        EmpVariationCorsiaRequestPayload requestPayload = EmpVariationCorsiaRequestPayload.builder()
            .decisionNotification(decisionNotification)
            .build();
        Request request = Request.builder().payload(requestPayload).build();

        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();

        UserInfoDTO accountPrimaryContactInfo = buildUserInfo("primaryContact@pmrv.uk");
        UserInfoDTO serviceContactInfo = buildUserInfo("serviceContact@pmrv.uk");
        List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
            DocumentTemplateParamsSourceData.builder()
                .contextActionType(DocumentTemplateGenerationContextActionType.EMP_VARIATION_CORSIA_REJECTED)
                .request(request)
                .signatory(decisionNotification.getSignatory())
                .accountPrimaryContact(accountPrimaryContactInfo)
                .toRecipientEmail(serviceContactInfo.getEmail())
                .ccRecipientsEmails(decisionNotificationUserEmails)
                .build();

        when(requestService.findRequestById(requestId))
            .thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(requestAccountContactQueryService.getRequestAccountServiceContact(request))
            .thenReturn(Optional.of(serviceContactInfo));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
            .thenReturn(decisionNotificationUserEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
            .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocument(
            DocumentTemplateType.EMP_VARIATION_CORSIA_REJECTED, templateParams, fileName))
            .thenReturn(officialDocFileInfoDTO);

        // Invoke
        service.generateAndSaveRejectedOfficialNotice(requestId);

        // Verify
        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocument(
            DocumentTemplateType.EMP_VARIATION_CORSIA_REJECTED, templateParams, fileName);
    }
    
    @Test
    void sendOfficialNotice() {
        String requestId = "1";
        String decisionNotificationUserEmail = "operator1@email";

        DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUser"))
                .signatory("signatoryUser")
                .build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();

        Request request = Request.builder()
                .id(requestId)
                .payload(EmpVariationCorsiaRequestPayload.builder()
                        .decisionNotification(decisionNotification)
                        .officialNotice(officialDocFileInfoDTO)
                        .build())
                .build();

        List<String> ccRecipientsEmails = List.of(decisionNotificationUserEmail);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(decisionNotificationUsersService.findUserEmails(decisionNotification)).thenReturn(List.of(decisionNotificationUserEmail));

        service.sendOfficialNotice(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(officialNoticeSendService, times(1)).sendOfficialNotice(List.of(officialDocFileInfoDTO), request, ccRecipientsEmails);
    }
    
    private FileInfoDTO buildOfficialFileInfo() {
        return FileInfoDTO.builder()
                .name("offDoc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
    }
    
	private UserInfoDTO buildUserInfo(String email) {
		return UserInfoDTO.builder().email(email).build();
	}
}
