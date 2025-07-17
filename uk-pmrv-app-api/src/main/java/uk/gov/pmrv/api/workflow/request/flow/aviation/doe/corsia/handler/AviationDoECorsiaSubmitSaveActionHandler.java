package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaSubmitSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service.AviationDoECorsiaSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AviationDoECorsiaSubmitSaveActionHandler
    implements RequestTaskActionHandler<AviationDoECorsiaSubmitSaveRequestTaskActionPayload> {

    private final AviationDoECorsiaSubmitService submitService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType,
                        AppUser appUser, AviationDoECorsiaSubmitSaveRequestTaskActionPayload payload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        submitService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_DOE_CORSIA_SUBMIT_SAVE);
    }


}
