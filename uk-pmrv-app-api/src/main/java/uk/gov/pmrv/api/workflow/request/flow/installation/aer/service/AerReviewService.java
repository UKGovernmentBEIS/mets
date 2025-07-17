package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSkipReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveReviewGroupDecisionRequestTaskActionPayload;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

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
                                     final AppUser appUser) {
        AerApplicationReviewRequestTaskPayload taskPayload =
            (AerApplicationReviewRequestTaskPayload) requestTask.getPayload();
        Request request = requestTask.getRequest();
        AerRequestPayload requestPayload = (AerRequestPayload) request.getPayload();

        requestPayload.setRegulatorReviewer(appUser.getUserId());
        requestPayload.setReviewGroupDecisions(taskPayload.getReviewGroupDecisions());
        requestPayload.setReviewAttachments(taskPayload.getReviewAttachments());
        requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
    }

    @Transactional
    public void saveRequestReturnForAmends(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        AerRequestPayload aerRequestPayload =
            (AerRequestPayload) request.getPayload();

        AerApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            (AerApplicationReviewRequestTaskPayload) requestTask.getPayload();

        aerRequestPayload.setRegulatorReviewer(appUser.getUserId());
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

    @Transactional
    public void updateRequestPayloadWithSkipReviewOutcome(
            final RequestTask requestTask,
            final AerApplicationSkipReviewRequestTaskActionPayload payload,
            final AppUser appUser) {

        final Request request = requestTask.getRequest();
        final AerRequestPayload requestPayload = (AerRequestPayload) request.getPayload();

        final Aer aer = requestPayload.getAer();
        final AerVerificationReport aerVerificationReport = requestPayload.getVerificationReport();
        final boolean isVerificationPerformed = requestPayload.isVerificationPerformed();

        final Set<AerReviewGroup> aerDataReviewGroups = AerReviewGroup.getAerDataReviewGroups(aer);
        final Set<AerReviewGroup> verificationDataReviewGroups = isVerificationPerformed ?
                AerReviewGroup.getVerificationDataReviewGroups(aerVerificationReport) :
                Collections.emptySet() ;

        final Map<AerReviewGroup, AerReviewDecision> reviewGroupDecisions = requestPayload.getReviewGroupDecisions();

        for (final AerReviewGroup group : aerDataReviewGroups) {
            final AerDataReviewDecision decision = AerDataReviewDecision.builder()
                    .reviewDataType(AerReviewDataType.AER_DATA)
                    .type(AerDataReviewDecisionType.ACCEPTED)
                    .details(ReviewDecisionDetails.builder().build())
                    .build();
            reviewGroupDecisions.put(group, decision);
        }

        for (final AerReviewGroup group : verificationDataReviewGroups) {
            final AerVerificationReportDataReviewDecision decision = AerVerificationReportDataReviewDecision.builder()
                    .reviewDataType(AerReviewDataType.VERIFICATION_REPORT_DATA)
                    .type(AerVerificationReportDataReviewDecisionType.ACCEPTED)
                    .details(ReviewDecisionDetails.builder().build())
                    .build();
            reviewGroupDecisions.put(group, decision);
        }

        requestPayload.setSkipReviewDecision(payload.getAerSkipReviewDecision());
        requestPayload.setRegulatorReviewer(appUser.getUserId());
    }
}
