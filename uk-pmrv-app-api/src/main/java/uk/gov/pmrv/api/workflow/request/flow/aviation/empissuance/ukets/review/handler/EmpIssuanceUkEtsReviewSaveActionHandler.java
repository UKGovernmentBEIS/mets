package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.RequestEmpUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmpIssuanceUkEtsReviewSaveActionHandler implements
    RequestTaskActionHandler<EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestEmpUkEtsReviewService requestEmpUkEtsReviewService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestEmpUkEtsReviewService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_APPLICATION_REVIEW);
    }
}
