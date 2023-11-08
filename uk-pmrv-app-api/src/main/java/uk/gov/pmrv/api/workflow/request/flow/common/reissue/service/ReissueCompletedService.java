package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueReport;

@Service
@RequiredArgsConstructor
public class ReissueCompletedService {

	private final RequestService requestService;
	private final WorkflowService workflowService;
	
	@Transactional
	public void reissueCompleted(String requestId, Long accountId, String reissueRequestId, boolean succeeded) {
		final Request request = requestService.findRequestById(requestId);
		
		// update report metadata
		BatchReissueRequestMetadata metadata = (BatchReissueRequestMetadata) request.getMetadata();
		ReissueReport report = metadata.getAccountsReports().get(accountId);
		report.setSucceeded(succeeded);
		
		if(succeeded) {
			report.setIssueDate(LocalDate.now());
		} else {
			//delete request
			final Request reissueRequest = requestService.findRequestById(reissueRequestId);
			workflowService.deleteProcessInstance(reissueRequest.getProcessInstanceId(), "Reissue failed");
		}
	}
}
