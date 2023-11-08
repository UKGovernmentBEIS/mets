package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.handler;

import java.util.List;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestRecallActionHandler;

@Component
public class EmpVariationCorsiaReviewRequestRecallActionHandler extends RequestRecallActionHandler {

	public EmpVariationCorsiaReviewRequestRecallActionHandler(RequestTaskService requestTaskService,
                                                              RequestService requestService,
                                                              WorkflowService workflowService) {
		super(requestTaskService, requestService, workflowService);
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.EMP_VARIATION_CORSIA_RECALL_FROM_AMENDS);
	}

	@Override
	public RequestActionType getRequestActionType() {
		return RequestActionType.EMP_VARIATION_CORSIA_RECALLED_FROM_AMENDS;
	}
}
