package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReturnOfAllowancesOfficialNoticeService {

    private final RequestService requestService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final OfficialNoticeSendService officialNoticeSendService;
    private final OfficialNoticeGeneratorService officialNoticeGeneratorService;

    @Transactional
    public FileInfoDTO generateReturnOfAllowancesOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final ReturnOfAllowancesRequestPayload requestPayload =
            (ReturnOfAllowancesRequestPayload) request.getPayload();
        final DecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        return officialNoticeGeneratorService.generate(request,
            DocumentTemplateGenerationContextActionType.RETURN_OF_ALLOWANCES,
            DocumentTemplateType.RETURN_OF_ALLOWANCES,
            AccountType.INSTALLATION, decisionNotification, "Return_allowances_notice.pdf");
    }

    public void sendOfficialNotice(Request request, FileInfoDTO officialNotice,
                                   DecisionNotification decisionNotification) {

        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(decisionNotification);
        
        officialNoticeSendService.sendOfficialNotice(List.of(officialNotice), request, ccRecipientsEmails);
    }
}
