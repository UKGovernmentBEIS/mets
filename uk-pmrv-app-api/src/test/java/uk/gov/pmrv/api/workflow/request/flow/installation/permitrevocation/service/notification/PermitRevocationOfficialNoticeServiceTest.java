package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeGeneratorService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitRevocationOfficialNoticeServiceTest {

    @InjectMocks
    private PermitRevocationOfficialNoticeService service;
    
    @Mock
    private RequestService requestService;
    
    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;
    
    @Mock
    private OfficialNoticeSendService officialNoticeSendService;

    @Mock
    private OfficialNoticeGeneratorService officialNoticeGeneratorService;

    @Test
    void generateRevocationOfficialNotice() {
        String requestId = "1";
        PermitRevocationRequestPayload requestPayload = PermitRevocationRequestPayload.builder()
            .decisionNotification(DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .signatory("signatory")
                .build())
            .build();
        Request request = Request.builder().id(requestId).payload(requestPayload).build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(officialNoticeGeneratorService.generate(request, DocumentTemplateGenerationContextActionType.PERMIT_REVOCATION,
            DocumentTemplateType.PERMIT_REVOCATION, AccountType.INSTALLATION, requestPayload.getDecisionNotification(),
            "permit_revocation_notice.pdf"))
            .thenReturn(officialDocFileInfoDTO);
        
        FileInfoDTO result = service.generateRevocationOfficialNotice(requestId);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(officialNoticeGeneratorService, times(1))
            .generate(request, DocumentTemplateGenerationContextActionType.PERMIT_REVOCATION,
                DocumentTemplateType.PERMIT_REVOCATION, AccountType.INSTALLATION, requestPayload.getDecisionNotification(),
                "permit_revocation_notice.pdf");

        assertThat(result).isEqualTo(officialDocFileInfoDTO);
    }
    
    @Test
    void generateRevocationWithdrawnOfficialNotice() {
        String requestId = "1";
        PermitRevocationRequestPayload requestPayload = PermitRevocationRequestPayload.builder()
            .withdrawDecisionNotification(DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .signatory("signatory")
                .build())
            .build();
        Request request = Request.builder().id(requestId).payload(requestPayload).build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(officialNoticeGeneratorService.generate(request, DocumentTemplateGenerationContextActionType.PERMIT_REVOCATION_WITHDRAWN,
            DocumentTemplateType.PERMIT_REVOCATION_WITHDRAWN, AccountType.INSTALLATION, requestPayload.getWithdrawDecisionNotification(),
            "permit_revocation_withdrawn_notice.pdf"))
            .thenReturn(officialDocFileInfoDTO);

        FileInfoDTO result = service.generateRevocationWithdrawnOfficialNotice(requestId);
        
        verify(requestService, times(1)).findRequestById(requestId);

        verify(officialNoticeGeneratorService, times(1))
            .generate(request, DocumentTemplateGenerationContextActionType.PERMIT_REVOCATION_WITHDRAWN,
                DocumentTemplateType.PERMIT_REVOCATION_WITHDRAWN, AccountType.INSTALLATION, requestPayload.getWithdrawDecisionNotification(),
                "permit_revocation_withdrawn_notice.pdf");
        
        assertThat(result).isEqualTo(officialDocFileInfoDTO);
    }
    
    @Test
    void generateRevocationCessationOfficialNotice() {
        String requestId = "1";
        PermitRevocationRequestPayload requestPayload = PermitRevocationRequestPayload.builder().build();
        Request request = Request.builder().id(requestId).payload(requestPayload).build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();
        DecisionNotification cessationDecisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator1"))
            .signatory("signatory")
            .build();
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(officialNoticeGeneratorService.generate(request, DocumentTemplateGenerationContextActionType.PERMIT_REVOCATION_CESSATION,
            DocumentTemplateType.PERMIT_REVOCATION_CESSATION, AccountType.INSTALLATION, cessationDecisionNotification,
            "cessation_notice_after_permit_revocation.pdf"))
            .thenReturn(officialDocFileInfoDTO);
        
        FileInfoDTO result = service.generateRevocationCessationOfficialNotice(requestId, cessationDecisionNotification);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(officialNoticeGeneratorService, times(1))
            .generate(request, DocumentTemplateGenerationContextActionType.PERMIT_REVOCATION_CESSATION,
                DocumentTemplateType.PERMIT_REVOCATION_CESSATION, AccountType.INSTALLATION, cessationDecisionNotification,
                "cessation_notice_after_permit_revocation.pdf");
        
        assertThat(result).isEqualTo(officialDocFileInfoDTO);
    }

    @Test
    void sendOfficialNotice() {
        String requestId = "1";
        Request request = Request.builder().id(requestId).build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();
        List<String> decisionNotificationRecipientsEmails = List.of("operator1@email");
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operatorUser"))
            .signatory("signatoryUser")
            .build();
        
        when(decisionNotificationUsersService.findUserEmails(decisionNotification))
        .thenReturn(decisionNotificationRecipientsEmails);
        
        service.sendOfficialNotice(request, officialDocFileInfoDTO, decisionNotification);

        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        
        verify(officialNoticeSendService, times(1)).sendOfficialNotice(List.of(officialDocFileInfoDTO), request, decisionNotificationRecipientsEmails);
    }
    
    private FileInfoDTO buildOfficialFileInfo() {
        return FileInfoDTO.builder()
                .name("offDoc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
    }
}
