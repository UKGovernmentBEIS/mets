package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.notification.PermitSurrenderOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderNoticeDateReminderService {

	private final RequestService requestService;
	private final PermitSurrenderOfficialNoticeService permitSurrenderOfficialNoticeService;
	
	public void sendNoticeDateReminder(String requestId) {
		final Request request = requestService.findRequestById(requestId);
		permitSurrenderOfficialNoticeService.sendReviewDeterminationOfficialNotice(request);
	}
}
