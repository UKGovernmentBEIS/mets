package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.service.EmpVariationUkEtsAmendService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsSaveApplicationAmendActionHandler implements
		RequestTaskActionHandler<EmpVariationUkEtsSaveApplicationAmendRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
    private final EmpVariationUkEtsAmendService empVariationUkEtsAmendService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser, EmpVariationUkEtsSaveApplicationAmendRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        empVariationUkEtsAmendService.saveAmend(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_APPLICATION_AMEND);
    }
}
