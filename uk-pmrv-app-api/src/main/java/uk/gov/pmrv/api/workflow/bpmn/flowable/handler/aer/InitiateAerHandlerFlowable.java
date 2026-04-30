package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aer;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCreationService;

import java.time.Year;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class InitiateAerHandlerFlowable implements JavaDelegate {

    private final RequestService requestService;
    private final AerCreationService aerCreationService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Request request = requestService.findRequestById(requestId);
        final Date expirationDate = (Date) execution.getVariable(BpmnProcessConstants.AER_EXPIRATION_DATE);

        // Invoke AER
		aerCreationService.createRequestAerForYear(request.getAccountId(), Year.now(), expirationDate,
				AerInitiatorRequest.builder()
						.type(request.getType())
						.submissionDateTime(request.getSubmissionDate())
						.build());
    }
}
