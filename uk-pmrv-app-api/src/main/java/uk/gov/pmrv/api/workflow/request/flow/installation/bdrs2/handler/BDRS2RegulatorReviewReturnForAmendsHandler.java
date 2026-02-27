package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RegulatorReviewReturnForAmendsTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2RegulatorReviewSubmitService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BDRS2RegulatorReviewReturnForAmendsHandler implements RequestTaskActionHandler<BDRS2RegulatorReviewReturnForAmendsTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final BDRS2RegulatorReviewSubmitService submitService;
    private final WorkflowService workflowService;

    @Override
    @Transactional
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, BDRS2RegulatorReviewReturnForAmendsTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        BDRS2ApplicationRequestTaskPayload taskPayload = (BDRS2ApplicationRequestTaskPayload) requestTask.getPayload();
        taskPayload.setBdrs2SectionsCompleted(payload.getBdrs2SectionsCompleted());

        submitService.returnForAmends(requestTask, appUser);

        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.BDRS2_REGULATOR_REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.BDRS2_REGULATOR_REVIEW_RETURN_FOR_AMENDS);
    }
}
