package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.notification;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderOfficialNoticeServiceTest {

    @InjectMocks
    private PermitSurrenderOfficialNoticeService service;
    
    @Mock
    private RequestService requestService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private OfficialNoticeSendService officialNoticeSendService;

    @Mock
    private OfficialNoticeGeneratorService officialNoticeGeneratorService;

    @Test
    void generateAndSaveGrantedOfficialNotice() {
        String requestId = "1";
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
            .reviewDecisionNotification(DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .signatory("signatory")
                .build())
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(officialNoticeGeneratorService.generate(request, DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_GRANTED,
            DocumentTemplateType.PERMIT_SURRENDERED_NOTICE, AccountType.INSTALLATION, requestPayload.getReviewDecisionNotification(),
            "permit_surrender_notice.pdf"))
            .thenReturn(officialDocFileInfoDTO);
        
        service.generateAndSaveGrantedOfficialNotice(requestId);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(officialNoticeGeneratorService, times(1))
            .generate(request, DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_GRANTED,
            DocumentTemplateType.PERMIT_SURRENDERED_NOTICE, AccountType.INSTALLATION, requestPayload.getReviewDecisionNotification(),
            "permit_surrender_notice.pdf");
        
        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
    }
    
    @Test
    void generateAndSaveRejectedOfficialNotice() {
        String requestId = "1";
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
            .reviewDecisionNotification(DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .signatory("signatory")
                .build())
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(officialNoticeGeneratorService.generate(request, DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_REJECTED,
            DocumentTemplateType.PERMIT_SURRENDER_REJECTED_NOTICE, AccountType.INSTALLATION, requestPayload.getReviewDecisionNotification(),
            "permit_surrender_refused_notice.pdf"))
            .thenReturn(officialDocFileInfoDTO);
        
        service.generateAndSaveRejectedOfficialNotice(requestId);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(officialNoticeGeneratorService, times(1))
            .generate(request, DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_REJECTED,
                DocumentTemplateType.PERMIT_SURRENDER_REJECTED_NOTICE, AccountType.INSTALLATION, requestPayload.getReviewDecisionNotification(),
                "permit_surrender_refused_notice.pdf");
        
        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
    }
    
    @Test
    void generateAndSaveDeemedWithdrawnOfficialNotice() {
        String requestId = "1";
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
            .reviewDecisionNotification(DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .signatory("signatory")
                .build())
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(officialNoticeGeneratorService.generate(request, DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_DEEMED_WITHDRAWN,
            DocumentTemplateType.PERMIT_SURRENDER_DEEMED_WITHDRAWN_NOTICE, AccountType.INSTALLATION, requestPayload.getReviewDecisionNotification(),
            "permit_surrender_deemed_withdrawn_notice.pdf"))
            .thenReturn(officialDocFileInfoDTO);

        service.generateAndSaveDeemedWithdrawnOfficialNotice(requestId);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(officialNoticeGeneratorService, times(1))
            .generate(request, DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_DEEMED_WITHDRAWN,
                DocumentTemplateType.PERMIT_SURRENDER_DEEMED_WITHDRAWN_NOTICE, AccountType.INSTALLATION, requestPayload.getReviewDecisionNotification(),
                "permit_surrender_deemed_withdrawn_notice.pdf");

        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
    }
    
    @Test
    void generateCessationOfficialNotice() {
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder().build();
        Request request = Request.builder().payload(requestPayload).build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();
        DecisionNotification cessationDecisionNotification = DecisionNotification.builder()
            .operators(Set.of("operatorUser"))
            .signatory("signatoryUser")
            .build();

        when(officialNoticeGeneratorService.generate(request, DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_CESSATION,
            DocumentTemplateType.PERMIT_SURRENDER_CESSATION, AccountType.INSTALLATION, cessationDecisionNotification,
            "permit_surrender_cessation_complete_notice.pdf"))
            .thenReturn(officialDocFileInfoDTO);
        
        FileInfoDTO result = service.generateCessationOfficialNotice(request, cessationDecisionNotification);

        verify(officialNoticeGeneratorService, times(1))
            .generate(request, DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_CESSATION,
                DocumentTemplateType.PERMIT_SURRENDER_CESSATION, AccountType.INSTALLATION, cessationDecisionNotification,
                "permit_surrender_cessation_complete_notice.pdf");

        assertThat(result).isEqualTo(officialDocFileInfoDTO);
    }
    
    @Test
    void sendReviewDeterminationOfficialNotice() {
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
            .reviewDecisionNotification(DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .signatory("signatory")
                .build())
            .build();
        Request request = Request.builder().payload(requestPayload).build();
        List<String> decisionNotificationRecipientsEmails = List.of("operator1@email");
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();
        requestPayload.setOfficialNotice(officialDocFileInfoDTO);
        
        when(decisionNotificationUsersService.findUserEmails(requestPayload.getReviewDecisionNotification()))
        .thenReturn(decisionNotificationRecipientsEmails);
        
        service.sendReviewDeterminationOfficialNotice(request);
        
        verify(decisionNotificationUsersService, times(1))
            .findUserEmails(requestPayload.getReviewDecisionNotification());
        verify(officialNoticeSendService, times(1))
            .sendOfficialNotice(List.of(officialDocFileInfoDTO), request, decisionNotificationRecipientsEmails);
    }
    
    @Test
    void sendOfficialNoticeForDecisionNotification() {
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder().build();
        Request request = Request.builder().payload(requestPayload).build();
        List<String> decisionNotificationRecipientsEmails = List.of("operator1@email");
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();
        requestPayload.setOfficialNotice(officialDocFileInfoDTO);
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator1"))
            .signatory("signatory")
            .build();

        when(decisionNotificationUsersService.findUserEmails(decisionNotification)).thenReturn(decisionNotificationRecipientsEmails);
        
        service.sendOfficialNoticeForDecisionNotification(request, officialDocFileInfoDTO, decisionNotification);
        
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(officialNoticeSendService, times(1))
            .sendOfficialNotice(List.of(officialDocFileInfoDTO), request, decisionNotificationRecipientsEmails);
    }

    private FileInfoDTO buildOfficialFileInfo() {
        return FileInfoDTO.builder()
                .name("offDoc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
    }
}
