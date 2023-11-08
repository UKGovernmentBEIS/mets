package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerReviewGroupDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerReviewValidator;

@Service
@RequiredArgsConstructor
public class NerApplyReviewService {

    private final NerReviewValidator reviewValidator;
    
    @Transactional
    public void applySaveAction(final RequestTask requestTask,
                                final NerSaveApplicationReviewRequestTaskActionPayload taskActionPayload) {

        final NerApplicationReviewRequestTaskPayload
            taskPayload = (NerApplicationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.getNerOperatorDocuments().getNewEntrantDataReport().setDocument(taskActionPayload.getNewEntrantDataReport());
        taskPayload.getNerOperatorDocuments().getVerifierOpinionStatement().setDocument(taskActionPayload.getVerifierOpinionStatement());
        taskPayload.getNerOperatorDocuments().getMonitoringMethodologyPlan().setDocument(taskActionPayload.getMonitoringMethodologyPlan());
        taskPayload.setConfidentialityStatement(taskActionPayload.getConfidentialityStatement());
        taskPayload.setAdditionalDocuments(taskActionPayload.getAdditionalDocuments());
        taskPayload.setNerSectionsCompleted(taskActionPayload.getNerSectionsCompleted());
        taskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());

        this.resetDeterminationIfInvalid(taskPayload);
    }

    @Transactional
    public void saveReviewGroupDecision(final NerSaveReviewGroupDecisionRequestTaskActionPayload payload,
                                        final RequestTask requestTask) {

        final NerReviewGroup group = payload.getGroup();
        final NerReviewGroupDecision decision = payload.getDecision();
        final NerApplicationReviewRequestTaskPayload taskPayload =
            (NerApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final Map<NerReviewGroup, NerReviewGroupDecision> reviewGroupDecisions = taskPayload.getReviewGroupDecisions();
        final Map<String, Boolean> reviewSectionsCompleted = payload.getReviewSectionsCompleted();

        reviewGroupDecisions.put(group, decision);
        taskPayload.setReviewSectionsCompleted(reviewSectionsCompleted);
        
        this.resetDeterminationIfInvalid(taskPayload);
    }

    @Transactional
    public void saveDetermination(final NerSaveReviewDeterminationRequestTaskActionPayload taskActionPayload,
                                  final RequestTask requestTask) {

        final NerApplicationReviewRequestTaskPayload taskPayload =
            (NerApplicationReviewRequestTaskPayload) requestTask.getPayload();

        final NerDetermination determination = taskActionPayload.getDetermination();
        taskPayload.setDetermination(determination);

        final Map<String, Boolean> reviewSectionsCompleted = taskActionPayload.getReviewSectionsCompleted();
        taskPayload.setReviewSectionsCompleted(reviewSectionsCompleted);
    }
    
    @Transactional
    public void saveRequestPeerReviewAction(final RequestTask requestTask, 
                                            final String selectedPeerReview, 
                                            final PmrvUser pmrvUser) {

        final Request request = requestTask.getRequest();
        final NerApplicationReviewRequestTaskPayload taskPayload =
            (NerApplicationReviewRequestTaskPayload) requestTask.getPayload();

        final NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();
        this.updateRequestPayload(pmrvUser, taskPayload, requestPayload);
        requestPayload.setRegulatorPeerReviewer(selectedPeerReview);
    }
    
    @Transactional
    public void updateRequestPayload(final RequestTask requestTask, final PmrvUser pmrvUser) {
        
        final Request request = requestTask.getRequest();
        final NerApplicationReviewRequestTaskPayload taskPayload =
            (NerApplicationReviewRequestTaskPayload) requestTask.getPayload();

        final NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();

        this.updateRequestPayload(pmrvUser, taskPayload, requestPayload);
    }

    @Transactional
    public void amend(final NerSaveApplicationAmendRequestTaskActionPayload taskActionPayload,
                      final RequestTask requestTask) {

        final NerApplicationAmendsSubmitRequestTaskPayload
            taskPayload = (NerApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setNerOperatorDocuments(taskActionPayload.getNerOperatorDocuments());
        taskPayload.setConfidentialityStatement(taskActionPayload.getConfidentialityStatement());
        taskPayload.setAdditionalDocuments(taskActionPayload.getAdditionalDocuments());
        taskPayload.setNerSectionsCompleted(taskActionPayload.getNerSectionsCompleted());
        taskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
    }

    @Transactional
    public void submitAmendedNer(final NerSubmitApplicationAmendRequestTaskActionPayload taskActionPayload,
                                 final RequestTask requestTask) {

        final Request request = requestTask.getRequest();
        final NerApplicationAmendsSubmitRequestTaskPayload taskPayload =
            (NerApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        final NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();
        requestPayload.setNerOperatorDocuments(taskPayload.getNerOperatorDocuments());
        requestPayload.setConfidentialityStatement(taskPayload.getConfidentialityStatement());
        requestPayload.setAdditionalDocuments(taskPayload.getAdditionalDocuments());
        requestPayload.setNerAttachments(taskPayload.getNerAttachments());
        requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
        requestPayload.setNerSectionsCompleted(taskActionPayload.getNerSectionsCompleted());
    }

    @Transactional
    public void saveDecisionNotification(final RequestTask requestTask,
                                         final DecisionNotification decisionNotification,
                                         final PmrvUser pmrvUser) {

        final Request request = requestTask.getRequest();
        final NerApplicationReviewRequestTaskPayload taskPayload =
            (NerApplicationReviewRequestTaskPayload) requestTask.getPayload();

        final NerRequestPayload requestPayload =
            (NerRequestPayload) request.getPayload();

        this.updateRequestPayload(pmrvUser, taskPayload, requestPayload);
        requestPayload.setDecisionNotification(decisionNotification);
    }
    
    private void updateRequestPayload(final PmrvUser pmrvUser,
                                      final NerApplicationReviewRequestTaskPayload taskPayload,
                                      final NerRequestPayload requestPayload) {
        
        requestPayload.setNerOperatorDocuments(taskPayload.getNerOperatorDocuments());
        requestPayload.setConfidentialityStatement(taskPayload.getConfidentialityStatement());
        requestPayload.setAdditionalDocuments(taskPayload.getAdditionalDocuments());
        requestPayload.setDetermination(taskPayload.getDetermination());
        requestPayload.setReviewGroupDecisions(taskPayload.getReviewGroupDecisions());
        requestPayload.setNerAttachments(taskPayload.getNerAttachments());
        requestPayload.setReviewAttachments(taskPayload.getReviewAttachments());
        requestPayload.setNerSectionsCompleted(taskPayload.getNerSectionsCompleted());
        requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
        requestPayload.setRegulatorReviewer(pmrvUser.getUserId());
    }

    private void resetDeterminationIfInvalid(final NerApplicationReviewRequestTaskPayload requestTaskPayload) {

        final boolean reviewDeterminationValid = requestTaskPayload.getDetermination() != null &&
            reviewValidator.isReviewDeterminationValid(requestTaskPayload.getDetermination(), requestTaskPayload.getReviewGroupDecisions());

        if (!reviewDeterminationValid) {
            requestTaskPayload.setDetermination(null);
        }
    }
}
