package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationRequestPayload;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PermanentCessationOfficialNoticeService {

    private static final String FILENAME = "Permanent_cessation_notice.pdf";

    private final RequestService requestService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final OfficialNoticeSendService officialNoticeSendService;
    private final OfficialNoticeGeneratorService officialNoticeGeneratorService;

    @Transactional
    public FileInfoDTO generatePermanentCessationOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final PermanentCessationRequestPayload requestPayload =
            (PermanentCessationRequestPayload) request.getPayload();
        final DecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        return officialNoticeGeneratorService.generate(request,
            DocumentTemplateGenerationContextActionType.PERMANENT_CESSATION_SUBMITTED,
            DocumentTemplateType.PERMANENT_CESSATION,
            AccountType.INSTALLATION, decisionNotification, FILENAME);
    }

    public void sendOfficialNotice(Request request, FileInfoDTO officialNotice,
                                   DecisionNotification decisionNotification) {

        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(decisionNotification);
        
        officialNoticeSendService.sendOfficialNotice(List.of(officialNotice), request, ccRecipientsEmails);
    }
}
