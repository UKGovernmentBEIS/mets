package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DreOfficialNoticeSendService {
	
	private final RequestService requestService;
	private final DecisionNotificationUsersService decisionNotificationUsersService;
	private final OfficialNoticeSendService officialNoticeSendService;

	public void sendOfficialNotice(String requestId) {
		final Request request = requestService.findRequestById(requestId);
	    final DreRequestPayload requestPayload = (DreRequestPayload) request.getPayload();
		final List<String> decisionRecipients = decisionNotificationUsersService
				.findUserEmails(requestPayload.getDecisionNotification());
        
		officialNoticeSendService.sendOfficialNotice(List.of(requestPayload.getOfficialNotice()), request,
				decisionRecipients);
    }
	
}
