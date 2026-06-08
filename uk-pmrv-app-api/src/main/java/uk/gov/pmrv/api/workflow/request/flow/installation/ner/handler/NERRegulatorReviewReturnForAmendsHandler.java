package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRegulatorReviewReturnForAmendsTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NERRegulatorReviewSubmitService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NERRegulatorReviewReturnForAmendsHandler implements RequestTaskActionHandler<NERRegulatorReviewReturnForAmendsTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final NERRegulatorReviewSubmitService submitService;
    private final WorkflowService workflowService;

    @Override
    @Transactional
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, NERRegulatorReviewReturnForAmendsTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        NERApplicationRequestTaskPayload taskPayload = (NERApplicationRequestTaskPayload) requestTask.getPayload();
        taskPayload.setNerSectionsCompleted(payload.getNerSectionsCompleted());

        submitService.returnForAmends(requestTask, appUser);

        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NER_REGULATOR_REVIEW_RETURN_FOR_AMENDS);
    }
}
