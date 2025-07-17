package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;

import java.util.List;

@RequiredArgsConstructor
@Service
public class WithholdingOfAllowancesOfficialNoticeService {

    private final RequestService requestService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final OfficialNoticeSendService officialNoticeSendService;
    private final OfficialNoticeGeneratorService officialNoticeGeneratorService;

    @Transactional
    public FileInfoDTO generateWithholdingOfAllowancesOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final WithholdingOfAllowancesRequestPayload requestPayload =
            (WithholdingOfAllowancesRequestPayload) request.getPayload();
        final DecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        return officialNoticeGeneratorService.generate(request,
            DocumentTemplateGenerationContextActionType.WITHHOLDING_OF_ALLOWANCES,
            DocumentTemplateType.WITHHOLDING_OF_ALLOWANCES,
            AccountType.INSTALLATION, decisionNotification, "Withholding_of_allowances_notice.pdf");
    }

    @Transactional
    public FileInfoDTO generateWithholdingOfAllowancesWithdrawnOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final WithholdingOfAllowancesRequestPayload requestPayload =
            (WithholdingOfAllowancesRequestPayload) request.getPayload();
        final DecisionNotification decisionNotification = requestPayload.getWithdrawDecisionNotification();

        return officialNoticeGeneratorService.generate(request,
            DocumentTemplateGenerationContextActionType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWN,
            DocumentTemplateType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWN,
            AccountType.INSTALLATION, decisionNotification, "Withdrawal_of_withholding_of_allowances_notice.pdf");
    }

    public void sendOfficialNotice(Request request, FileInfoDTO officialNotice,
                                   DecisionNotification decisionNotification) {
		officialNoticeSendService.sendOfficialNotice(List.of(officialNotice), request,
				decisionNotificationUsersService.findUserEmails(decisionNotification));
    }

}
