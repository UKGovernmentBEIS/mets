package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.service.EmpVariationCorsiaAmendService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaSaveApplicationAmendActionHandler implements
		RequestTaskActionHandler<EmpVariationCorsiaSaveApplicationAmendRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
    private final EmpVariationCorsiaAmendService empVariationCorsiaAmendService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        EmpVariationCorsiaSaveApplicationAmendRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        empVariationCorsiaAmendService.saveAmend(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_APPLICATION_AMEND);
    }
}
