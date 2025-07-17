package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RequestPermitSurrenderReviewService {
    
    private final PermitSurrenderReviewDeterminationHandlerService determinationHandlerService;

    @Transactional
    public void saveReviewDecision(PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload,
            RequestTask requestTask) {
        final PermitSurrenderReviewDecision reviewDecision = taskActionPayload.getReviewDecision();

        final PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = 
            (PermitSurrenderApplicationReviewRequestTaskPayload) requestTask.getPayload();
        
        taskPayload.setReviewDecision(reviewDecision);
        taskPayload.setReviewDeterminationCompleted(taskActionPayload.getReviewDeterminationCompleted());
        
        PermitSurrenderReviewDetermination reviewDetermination = taskPayload.getReviewDetermination();
        if(reviewDetermination != null) {
            determinationHandlerService.handleDeterminationUponDecision(reviewDetermination.getType(), taskPayload, reviewDecision);    
        }
    }
    
    @Transactional
    public void saveReviewDetermination(PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload taskActionPayload, 
            RequestTask requestTask) {
        final PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = 
                (PermitSurrenderApplicationReviewRequestTaskPayload) requestTask.getPayload();

        determinationHandlerService.validateDecisionUponDetermination(taskPayload, taskActionPayload.getReviewDetermination());
        
        taskPayload.setReviewDetermination(taskActionPayload.getReviewDetermination());
        taskPayload.setReviewDeterminationCompleted(taskActionPayload.getReviewDeterminationCompleted());
    }

    @Transactional
    public void saveRequestPeerReviewAction(RequestTask requestTask, String selectedPeerReviewer, String regulatorReviewer) {
        Request request = requestTask.getRequest();
        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload =
            (PermitSurrenderApplicationReviewRequestTaskPayload) requestTask.getPayload();

        PermitSurrenderRequestPayload permitSurrenderRequestPayload =
            (PermitSurrenderRequestPayload) request.getPayload();

        permitSurrenderRequestPayload.setRegulatorReviewer(regulatorReviewer);
        permitSurrenderRequestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
        permitSurrenderRequestPayload.setReviewDecision(taskPayload.getReviewDecision());
        permitSurrenderRequestPayload.setReviewDetermination(taskPayload.getReviewDetermination());
        permitSurrenderRequestPayload.setReviewDeterminationCompleted(taskPayload.getReviewDeterminationCompleted());
    }
    
    @Transactional
    public void saveReviewDecisionNotification(final RequestTask requestTask, final DecisionNotification decisionNotification,
            final AppUser reviewer) {
        final Request request = requestTask.getRequest();
        final PermitSurrenderRequestPayload requestPayload = (PermitSurrenderRequestPayload) request.getPayload();
        final PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = 
                (PermitSurrenderApplicationReviewRequestTaskPayload) requestTask.getPayload();
        
        requestPayload.setReviewDecision(taskPayload.getReviewDecision());
        requestPayload.setReviewDetermination(taskPayload.getReviewDetermination());
        requestPayload.setReviewDeterminationCompletedDate(LocalDate.now());
        requestPayload.setReviewDecisionNotification(decisionNotification);
        requestPayload.setRegulatorReviewer(reviewer.getUserId());
    }

}
