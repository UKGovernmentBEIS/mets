package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveDetailsReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.EmpVariationUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmpVariationUkEtsSaveDetailsReviewGroupDecisionActionHandler 
	implements RequestTaskActionHandler<EmpVariationUkEtsSaveDetailsReviewGroupDecisionRequestTaskActionPayload>{

	private final RequestTaskService requestTaskService;
    private final EmpVariationUkEtsReviewService empVariationUkEtsReviewService;

    @Override
    public void process(final Long requestTaskId,
        final RequestTaskActionType requestTaskActionType,
        final PmrvUser pmrvUser,
        final EmpVariationUkEtsSaveDetailsReviewGroupDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        empVariationUkEtsReviewService.saveDetailsReviewGroupDecision(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_DETAILS_REVIEW_GROUP_DECISION);
    }
}
