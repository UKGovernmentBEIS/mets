package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRAmendsSubmitService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WasteQDRApplicationAmendsSaveActionHandler implements
        RequestTaskActionHandler<WasteQDRApplicationAmendsSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WasteQDRAmendsSubmitService submitService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, WasteQDRApplicationAmendsSaveRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        submitService.saveAmends(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.WASTE_QDR_APPLICATION_AMENDS_SAVE);
    }
}
