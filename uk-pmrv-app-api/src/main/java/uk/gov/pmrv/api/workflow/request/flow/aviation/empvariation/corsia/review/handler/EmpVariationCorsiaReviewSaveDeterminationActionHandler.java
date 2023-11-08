package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service.EmpVariationCorsiaReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation.EmpVariationCorsiaReviewDeterminationValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@Component
@RequiredArgsConstructor
public class EmpVariationCorsiaReviewSaveDeterminationActionHandler 
	implements RequestTaskActionHandler<EmpVariationCorsiaSaveReviewDeterminationRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
    private final EmpVariationCorsiaReviewService empVariationCorsiaReviewService;
    private final EmpVariationCorsiaReviewDeterminationValidatorService determinationValidatorService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        EmpVariationCorsiaSaveReviewDeterminationRequestTaskActionPayload taskActionPayload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        checkDeterminationValidity(taskActionPayload.getDetermination(), requestTask);
        empVariationCorsiaReviewService.saveDetermination(taskActionPayload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_REVIEW_DETERMINATION);
    }
    
    private void checkDeterminationValidity(EmpVariationDetermination determination, RequestTask requestTask) {
        EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload =
            (EmpVariationCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        if (!determinationValidatorService.isValid(requestTaskPayload, determination.getType())) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}
