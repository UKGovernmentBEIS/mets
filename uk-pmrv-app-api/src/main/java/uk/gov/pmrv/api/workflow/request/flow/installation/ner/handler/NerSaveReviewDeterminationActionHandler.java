package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerReviewGroupDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplyReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerReviewValidator;

@Component
@RequiredArgsConstructor
public class NerSaveReviewDeterminationActionHandler
    implements RequestTaskActionHandler<NerSaveReviewDeterminationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final NerReviewValidator reviewValidator;
    private final NerApplyReviewService applyReviewService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final NerSaveReviewDeterminationRequestTaskActionPayload payload) {

        final NerDetermination determination = payload.getDetermination();

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final NerApplicationReviewRequestTaskPayload taskPayload =
            (NerApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final Map<NerReviewGroup, NerReviewGroupDecision> reviewGroupDecisions = taskPayload.getReviewGroupDecisions();
        final boolean reviewDeterminationValid = determination.getType() != null &&
            reviewValidator.isReviewDeterminationValid(determination, reviewGroupDecisions);
        if (!reviewDeterminationValid) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        applyReviewService.saveDetermination(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NER_SAVE_REVIEW_DETERMINATION);
    }
}
