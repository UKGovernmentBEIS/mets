package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.RequestEmpUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation.EmpIssuanceUkEtsReviewDeterminationValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmpIssuanceUkEtsReviewSaveDeterminationActionHandler
    implements RequestTaskActionHandler<EmpIssuanceUkEtsSaveReviewDeterminationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestEmpUkEtsReviewService requestEmpUkEtsReviewService;
    private final EmpIssuanceUkEtsReviewDeterminationValidatorService determinationValidatorService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        EmpIssuanceUkEtsSaveReviewDeterminationRequestTaskActionPayload taskActionPayload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        checkDeterminationValidity(taskActionPayload.getDetermination(), requestTask);
        requestEmpUkEtsReviewService.saveDetermination(taskActionPayload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_REVIEW_DETERMINATION);
    }
    private void checkDeterminationValidity(EmpIssuanceDetermination determination, RequestTask requestTask) {
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
            (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        if (!determinationValidatorService.isValid(requestTaskPayload, determination.getType())) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

    }
}
