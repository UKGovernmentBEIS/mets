package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveReviewGroupDecisionRequestTaskActionPayload;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AerReviewService {

    @Transactional
    public void saveReviewGroupDecision(final AerSaveReviewGroupDecisionRequestTaskActionPayload payload,
                                        final RequestTask requestTask) {
        final AerReviewGroup group = payload.getGroup();
        final AerReviewDecision decision = payload.getDecision();

        final AerApplicationReviewRequestTaskPayload taskPayload =
            (AerApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final Map<AerReviewGroup, AerReviewDecision> reviewGroupDecisions = taskPayload.getReviewGroupDecisions();

        reviewGroupDecisions.put(group, decision);

        final Map<String, Boolean> reviewSectionsCompleted = payload.getReviewSectionsCompleted();
        taskPayload.setReviewSectionsCompleted(reviewSectionsCompleted);
    }

    @Transactional
    public void updateRequestPayload(final RequestTask requestTask,
                                     final PmrvUser pmrvUser) {
        AerApplicationReviewRequestTaskPayload taskPayload =
            (AerApplicationReviewRequestTaskPayload) requestTask.getPayload();
        Request request = requestTask.getRequest();
        AerRequestPayload requestPayload = (AerRequestPayload) request.getPayload();

        requestPayload.setRegulatorReviewer(pmrvUser.getUserId());
        requestPayload.setReviewGroupDecisions(taskPayload.getReviewGroupDecisions());
        requestPayload.setReviewAttachments(taskPayload.getReviewAttachments());
        requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
    }

    @Transactional
    public void saveRequestReturnForAmends(RequestTask requestTask, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        AerRequestPayload aerRequestPayload =
            (AerRequestPayload) request.getPayload();

        AerApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            (AerApplicationReviewRequestTaskPayload) requestTask.getPayload();

        aerRequestPayload.setRegulatorReviewer(pmrvUser.getUserId());
        aerRequestPayload.setReviewGroupDecisions(reviewRequestTaskPayload.getReviewGroupDecisions());
        aerRequestPayload.setReviewAttachments(reviewRequestTaskPayload.getReviewAttachments());
        aerRequestPayload.setReviewSectionsCompleted(reviewRequestTaskPayload.getReviewSectionsCompleted());
    }

    @Transactional
    public void saveAmendOfAer(AerSaveApplicationAmendRequestTaskActionPayload aerSaveApplicationAmendRequestTaskActionPayload,
                               RequestTask requestTask) {
        AerApplicationAmendsSubmitRequestTaskPayload
            aerApplicationAmendsSubmitRequestTaskPayload =
            (AerApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        updateApplicationAmendsSubmitRequestTaskPayload(aerSaveApplicationAmendRequestTaskActionPayload,
            aerApplicationAmendsSubmitRequestTaskPayload);
    }

    private static void updateApplicationAmendsSubmitRequestTaskPayload(
        AerSaveApplicationAmendRequestTaskActionPayload aerSaveApplicationAmendRequestTaskActionPayload,
        AerApplicationAmendsSubmitRequestTaskPayload aerApplicationAmendsSubmitRequestTaskPayload) {

        aerApplicationAmendsSubmitRequestTaskPayload.setAer(aerSaveApplicationAmendRequestTaskActionPayload.getAer());
        aerApplicationAmendsSubmitRequestTaskPayload
            .setAerSectionsCompleted(aerSaveApplicationAmendRequestTaskActionPayload.getAerSectionsCompleted());
        aerApplicationAmendsSubmitRequestTaskPayload
            .setReviewSectionsCompleted(aerSaveApplicationAmendRequestTaskActionPayload.getReviewSectionsCompleted());

        //Reset verification performed flag
        aerApplicationAmendsSubmitRequestTaskPayload.setVerificationPerformed(false);
    }
}
