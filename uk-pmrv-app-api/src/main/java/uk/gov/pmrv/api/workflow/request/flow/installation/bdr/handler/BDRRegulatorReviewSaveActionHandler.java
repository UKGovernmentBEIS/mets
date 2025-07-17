package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSaveTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRRegulatorReviewSubmitService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BDRRegulatorReviewSaveActionHandler
        implements RequestTaskActionHandler<BDRApplicationRegulatorReviewSaveTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final BDRRegulatorReviewSubmitService submitService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        BDRApplicationRegulatorReviewSaveTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        submitService.save(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.BDR_REGULATOR_REVIEW_SAVE);
    }
}
