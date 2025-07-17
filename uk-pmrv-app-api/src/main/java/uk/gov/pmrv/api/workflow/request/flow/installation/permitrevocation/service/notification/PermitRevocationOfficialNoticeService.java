package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service.notification;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PermitRevocationOfficialNoticeService {
	
	private final RequestService requestService;
	private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final OfficialNoticeSendService officialNoticeSendService;
    private final OfficialNoticeGeneratorService officialNoticeGeneratorService;

	@Transactional
    public FileInfoDTO generateRevocationOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) request.getPayload();
        final DecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        return officialNoticeGeneratorService.generate(request,
            DocumentTemplateGenerationContextActionType.PERMIT_REVOCATION,
            DocumentTemplateType.PERMIT_REVOCATION,
            AccountType.INSTALLATION,
            decisionNotification,
            "permit_revocation_notice.pdf");
    }
	
	@Transactional
    public FileInfoDTO generateRevocationWithdrawnOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) request.getPayload();
        final DecisionNotification withdrawDecisionNotification = requestPayload.getWithdrawDecisionNotification();

        return officialNoticeGeneratorService.generate(request,
            DocumentTemplateGenerationContextActionType.PERMIT_REVOCATION_WITHDRAWN,
            DocumentTemplateType.PERMIT_REVOCATION_WITHDRAWN,
            AccountType.INSTALLATION,
            withdrawDecisionNotification,
            "permit_revocation_withdrawn_notice.pdf");
    }
	
	@Transactional
    public FileInfoDTO generateRevocationCessationOfficialNotice(String requestId, DecisionNotification cessationDecisionNotification) {
        final Request request = requestService.findRequestById(requestId);

        return officialNoticeGeneratorService.generate(request,
            DocumentTemplateGenerationContextActionType.PERMIT_REVOCATION_CESSATION,
            DocumentTemplateType.PERMIT_REVOCATION_CESSATION,
            AccountType.INSTALLATION,
            cessationDecisionNotification,
            "cessation_notice_after_permit_revocation.pdf");
    }

	public void sendOfficialNotice(Request request, FileInfoDTO officialNotice,
			DecisionNotification decisionNotification) {
		officialNoticeSendService.sendOfficialNotice(List.of(officialNotice), request,
				decisionNotificationUsersService.findUserEmails(decisionNotification));
	}

}
