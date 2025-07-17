package uk.gov.pmrv.api.workflow.request.flow.installation.dre.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreApplyService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class DreApplySaveActionHandler implements RequestTaskActionHandler<DreSaveApplicationRequestTaskActionPayload> {

	private final DreApplyService dreApplyService;
    private final RequestTaskService requestTaskService;
	
	@Override
	public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
			DreSaveApplicationRequestTaskActionPayload payload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		dreApplyService.applySaveAction(payload, requestTask);
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.DRE_SAVE_APPLICATION);
	}

}
