package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

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

import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;

@ExtendWith(MockitoExtension.class)
public class PermitVariationOfficialNoticeServiceTest {

	@InjectMocks
    private PermitVariationOfficialNoticeService cut;
	
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

    @Test
    void generateGrantedOfficialNotice() throws InterruptedException, ExecutionException {
        String requestId = "1";
        String fileName = "permit_variation_approved.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();
        PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
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
                .contextActionType(DocumentTemplateGenerationContextActionType.PERMIT_VARIATION_GRANTED)
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
        when(documentFileGeneratorService.generateFileDocumentAsync(
            DocumentTemplateType.PERMIT_VARIATION_ACCEPTED, templateParams, fileName))
            .thenReturn(CompletableFuture.completedFuture(officialDocFileInfoDTO));

        // Invoke
        CompletableFuture<FileInfoDTO> result = cut.generateGrantedOfficialNotice(requestId);
        
        assertThat(result.get()).isEqualTo(officialDocFileInfoDTO);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateFileDocumentAsync(
            DocumentTemplateType.PERMIT_VARIATION_ACCEPTED, templateParams, fileName);
    }
    
    @Test
    void generateRegulatorLedApprovedOfficialNotice() throws InterruptedException, ExecutionException {
        String requestId = "1";
        String fileName = "permit_ca_variation_approved.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();
        PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
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
                .contextActionType(DocumentTemplateGenerationContextActionType.PERMIT_VARIATION_REGULATOR_LED_APPROVED)
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
        when(documentFileGeneratorService.generateFileDocumentAsync(
            DocumentTemplateType.PERMIT_VARIATION_REGULATOR_LED_APPROVED, templateParams, fileName))
            .thenReturn(CompletableFuture.completedFuture(officialDocFileInfoDTO));

        // Invoke
        CompletableFuture<FileInfoDTO> result = cut.generateRegulatorLedApprovedOfficialNotice(requestId);
        
        assertThat(result.get()).isEqualTo(officialDocFileInfoDTO);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateFileDocumentAsync(
            DocumentTemplateType.PERMIT_VARIATION_REGULATOR_LED_APPROVED, templateParams, fileName);
    }
    
    @Test
    void generateAndSaveRejectedOfficialNotice() {
        String requestId = "1";
        String fileName = "permit_variation_rejected.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();
        PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
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
                .contextActionType(DocumentTemplateGenerationContextActionType.PERMIT_VARIATION_REJECTED)
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
        when(documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.PERMIT_VARIATION_REJECTED, templateParams, fileName))
            .thenReturn(officialDocFileInfoDTO);

        // Invoke
        cut.generateAndSaveRejectedOfficialNotice(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
            DocumentTemplateType.PERMIT_VARIATION_REJECTED, templateParams, fileName);

        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
    }
    
    @Test
    void generateAndSaveDeemedWithdrawnOfficialNotice() {
        String requestId = "1";
        String fileName = "permit_variation_deemed_withdrawn.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();
        PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
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
        when(documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.PERMIT_VARIATION_DEEMED_WITHDRAWN, templateParams, fileName))
            .thenReturn(officialDocFileInfoDTO);

        // Invoke
        cut.generateAndSaveDeemedWithdrawnOfficialNotice(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
            DocumentTemplateType.PERMIT_VARIATION_DEEMED_WITHDRAWN, templateParams, fileName);

        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
    }
    
    @Test
    void sendOfficialNotice() {
    	String requestId = "requestId";
    	
    	DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();
        PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
            .permitType(PermitType.GHGE)
            .decisionNotification(decisionNotification)
            .permitDocument(FileInfoDTO.builder()
                .name("permitDoc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build())
            .officialNotice(buildOfficialFileInfo())
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        List<String> ccRecipientsEmails = List.of("operator@pmrv.uk");
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
        .thenReturn(ccRecipientsEmails);
        
        cut.sendOfficialNotice(requestId);
        
        verify(requestService, times(1)).findRequestById(requestId);
		verify(officialNoticeSendService, times(1)).sendOfficialNotice(
				List.of(requestPayload.getOfficialNotice(), requestPayload.getPermitDocument()), request,
				ccRecipientsEmails);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
    }
    
    @Test
    void sendOfficialNotice_no_permit_doc() {
    	String requestId = "requestId";
    	
    	DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();
        PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
            .permitType(PermitType.GHGE)
            .decisionNotification(decisionNotification)
            .officialNotice(buildOfficialFileInfo())
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        List<String> ccRecipientsEmails = List.of("operator@pmrv.uk");
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
        .thenReturn(ccRecipientsEmails);
        
        cut.sendOfficialNotice(requestId);
        
        verify(requestService, times(1)).findRequestById(requestId);
		verify(officialNoticeSendService, times(1)).sendOfficialNotice(
				List.of(requestPayload.getOfficialNotice()), request,
				ccRecipientsEmails);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
    }
    
    private FileInfoDTO buildOfficialFileInfo() {
        return FileInfoDTO.builder()
                .name("offDoc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
    }
	
	
}
