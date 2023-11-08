package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.config.RegistryConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;

@Service
@RequiredArgsConstructor
public class DreOfficialNoticeSendService {
	
	private final RequestService requestService;
	private final DecisionNotificationUsersService decisionNotificationUsersService;
	private final OfficialNoticeSendService officialNoticeSendService;
	private final RegistryConfig registryConfig;

	public void sendOfficialNotice(String requestId) {
		final Request request = requestService.findRequestById(requestId);
	    final DreRequestPayload requestPayload = (DreRequestPayload) request.getPayload();
	        
        final List<String> additionalRecipients = new ArrayList<>();
        additionalRecipients.add(registryConfig.getEmail());

        additionalRecipients.addAll(decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification()));
        officialNoticeSendService.sendOfficialNotice(List.of(requestPayload.getOfficialNotice()), request, additionalRecipients);
    }
	
}
