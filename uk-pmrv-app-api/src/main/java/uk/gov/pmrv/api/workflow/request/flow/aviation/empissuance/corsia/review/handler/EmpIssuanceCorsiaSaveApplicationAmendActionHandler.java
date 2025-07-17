package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service.RequestEmpCorsiaReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@Service
@RequiredArgsConstructor
public class EmpIssuanceCorsiaSaveApplicationAmendActionHandler implements
        RequestTaskActionHandler<EmpIssuanceCorsiaSaveApplicationAmendRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestEmpCorsiaReviewService requestEmpCorsiaReviewService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, EmpIssuanceCorsiaSaveApplicationAmendRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestEmpCorsiaReviewService.saveAmend(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_AMEND);
    }
}
