package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service.notification.PermitRevocationOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class PermitRevocationEffectiveDateReminderService {

	private final RequestService requestService;
	private final PermitRevocationOfficialNoticeService permitRevocationOfficialNoticeService;
	
	public void sendEffectiveDateReminder(String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) request.getPayload();
		final FileInfoDTO officialNotice = requestPayload.getOfficialNotice();
		final DecisionNotification decisionNotification = requestPayload.getDecisionNotification();
		
		permitRevocationOfficialNoticeService.sendOfficialNotice(request, officialNotice, decisionNotification);
	}
}
