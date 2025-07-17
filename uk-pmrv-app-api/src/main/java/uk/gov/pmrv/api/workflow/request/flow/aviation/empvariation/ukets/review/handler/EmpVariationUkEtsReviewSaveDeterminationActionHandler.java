package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.EmpVariationUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.validation.EmpVariationUkEtsReviewDeterminationValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;


@Component
@RequiredArgsConstructor
public class EmpVariationUkEtsReviewSaveDeterminationActionHandler 
	implements RequestTaskActionHandler<EmpVariationUkEtsSaveReviewDeterminationRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
    private final EmpVariationUkEtsReviewService empVariationUkEtsReviewService;
    private final EmpVariationUkEtsReviewDeterminationValidatorService determinationValidatorService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        EmpVariationUkEtsSaveReviewDeterminationRequestTaskActionPayload taskActionPayload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        checkDeterminationValidity(taskActionPayload.getDetermination(), requestTask);
        empVariationUkEtsReviewService.saveDetermination(taskActionPayload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_REVIEW_DETERMINATION);
    }
    
    private void checkDeterminationValidity(EmpVariationDetermination determination, RequestTask requestTask) {
        EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
            (EmpVariationUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        if (!determinationValidatorService.isValid(requestTaskPayload, determination.getType())) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

    }
}
