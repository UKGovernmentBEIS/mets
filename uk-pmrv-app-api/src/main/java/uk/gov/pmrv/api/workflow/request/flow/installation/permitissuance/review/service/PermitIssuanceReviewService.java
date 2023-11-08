package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.validation.PermitValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.mapper.PermitMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PermitIssuanceReviewService {
    private final PermitReviewService permitReviewService;
    private final PermitValidatorService permitValidatorService;
    private static final PermitMapper permitMapper = Mappers.getMapper(PermitMapper.class);

    @Transactional
    public void savePermit(PermitIssuanceSaveApplicationReviewRequestTaskActionPayload permitReviewRequestTaskActionPayload,
        RequestTask requestTask) {
        PermitIssuanceApplicationReviewRequestTaskPayload
            permitIssuanceApplicationReviewRequestTaskPayload = (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();

		permitReviewService.cleanUpDeprecatedReviewGroupDecisions(permitIssuanceApplicationReviewRequestTaskPayload,
				permitReviewRequestTaskActionPayload.getPermit().getMonitoringApproaches().getMonitoringApproaches().keySet());
		
		permitReviewService.resetDeterminationIfNotDeemedWithdrawn(permitIssuanceApplicationReviewRequestTaskPayload);
        
        updatePermitIssuanceReviewRequestTaskPayload(permitIssuanceApplicationReviewRequestTaskPayload, permitReviewRequestTaskActionPayload);
    }

    @Transactional
    public void saveReviewGroupDecision(final PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload payload,
        final RequestTask requestTask) {

        final PermitReviewGroup group = payload.getGroup();
        final PermitIssuanceReviewDecision decision = payload.getDecision();

        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final Map<PermitReviewGroup, PermitIssuanceReviewDecision> reviewGroupDecisions = taskPayload.getReviewGroupDecisions();

        reviewGroupDecisions.put(group, decision);

        final Map<String, Boolean> reviewSectionsCompleted = payload.getReviewSectionsCompleted();
        taskPayload.setReviewSectionsCompleted(reviewSectionsCompleted);
        
        permitReviewService.resetDeterminationIfNotValidWithDecisions(taskPayload, requestTask.getRequest().getType());
    }

    @Transactional
    public void saveDetermination(final PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload payload,
        final RequestTask requestTask) {

        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();

        final PermitIssuanceDeterminateable determination = payload.getDetermination();
        taskPayload.setDetermination(determination);

        final Map<String, Boolean> reviewSectionsCompleted = payload.getReviewSectionsCompleted();
        taskPayload.setReviewSectionsCompleted(reviewSectionsCompleted);
    }

    @Transactional
    public void savePermitDecisionNotification(final RequestTask requestTask,
        final DecisionNotification permitDecisionNotification,
        final PmrvUser pmrvUser) {

        final Request request = requestTask.getRequest();
        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();

        final PermitIssuanceRequestPayload permitIssuanceRequestPayload =
            (PermitIssuanceRequestPayload) request.getPayload();

        updatePermitIssuanceRequestPayload(permitIssuanceRequestPayload, taskPayload, pmrvUser);
        permitIssuanceRequestPayload.setDecisionNotification(permitDecisionNotification);
    }

    @Transactional
    public void saveRequestReturnForAmends(RequestTask requestTask, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();

        PermitIssuanceRequestPayload permitIssuanceRequestPayload =
            (PermitIssuanceRequestPayload) request.getPayload();

        updatePermitIssuanceRequestPayload(permitIssuanceRequestPayload, taskPayload, pmrvUser);
    }

    @Transactional
    public void saveRequestPeerReviewAction(RequestTask requestTask, String selectedPeerReview, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();

        PermitIssuanceRequestPayload permitIssuanceRequestPayload =
            (PermitIssuanceRequestPayload) request.getPayload();

        updatePermitIssuanceRequestPayload(permitIssuanceRequestPayload, taskPayload, pmrvUser);
        permitIssuanceRequestPayload.setRegulatorPeerReviewer(selectedPeerReview);
    }

    @Transactional
    public void amendPermit(PermitIssuanceSaveApplicationAmendRequestTaskActionPayload permitSaveApplicationAmendRequestTaskActionPayload,
        RequestTask requestTask) {
        PermitIssuanceApplicationAmendsSubmitRequestTaskPayload
            permitIssuanceApplicationAmendsSubmitRequestTaskPayload = (PermitIssuanceApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        updatePermitIssuanceReviewRequestTaskPayload(permitIssuanceApplicationAmendsSubmitRequestTaskPayload,
            permitSaveApplicationAmendRequestTaskActionPayload);
    }

    @Transactional
    public void submitAmendedPermit(PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload submitApplicationAmendRequestTaskActionPayload,
        RequestTask requestTask) {
        Request request = requestTask.getRequest();
        PermitIssuanceApplicationAmendsSubmitRequestTaskPayload permitIssuanceApplicationAmendsSubmitRequestTaskPayload =
            (PermitIssuanceApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        PermitContainer permitContainer = permitMapper.toPermitContainer(permitIssuanceApplicationAmendsSubmitRequestTaskPayload);
        permitValidatorService.validatePermit(permitContainer);

        PermitIssuanceRequestPayload permitIssuanceRequestPayload = (PermitIssuanceRequestPayload) request.getPayload();

        //PMRV-7723 : review decisions for dynamic sections are never removed from request payload when operator amends the permit
        //this should be done before setting the permit object of the requestPayload with the corresponding from the task payload
        permitReviewService.cleanUpDeprecatedReviewGroupDecisions(
            permitIssuanceRequestPayload,
            permitIssuanceApplicationAmendsSubmitRequestTaskPayload.getPermit().getMonitoringApproaches()
                .getMonitoringApproaches().keySet());

        permitIssuanceRequestPayload.setPermitType(permitIssuanceApplicationAmendsSubmitRequestTaskPayload.getPermitType());
        permitIssuanceRequestPayload.setPermit(permitIssuanceApplicationAmendsSubmitRequestTaskPayload.getPermit());
        permitIssuanceRequestPayload.setPermitAttachments(permitIssuanceApplicationAmendsSubmitRequestTaskPayload.getPermitAttachments());
        permitIssuanceRequestPayload.setReviewSectionsCompleted(permitIssuanceApplicationAmendsSubmitRequestTaskPayload.getReviewSectionsCompleted());
        permitIssuanceRequestPayload.setPermitSectionsCompleted(submitApplicationAmendRequestTaskActionPayload.getPermitSectionsCompleted());
    }

    private void updatePermitIssuanceRequestPayload(PermitIssuanceRequestPayload permitIssuanceRequestPayload,
        PermitIssuanceApplicationReviewRequestTaskPayload reviewRequestTaskPayload, PmrvUser pmrvUser) {
        permitIssuanceRequestPayload.setRegulatorReviewer(pmrvUser.getUserId());
        permitIssuanceRequestPayload.setPermitType(reviewRequestTaskPayload.getPermitType());
        permitIssuanceRequestPayload.setPermit(reviewRequestTaskPayload.getPermit());
        permitIssuanceRequestPayload.setPermitSectionsCompleted(reviewRequestTaskPayload.getPermitSectionsCompleted());
        permitIssuanceRequestPayload.setPermitAttachments(reviewRequestTaskPayload.getPermitAttachments());
        permitIssuanceRequestPayload.setReviewSectionsCompleted(reviewRequestTaskPayload.getReviewSectionsCompleted());
        permitIssuanceRequestPayload.setReviewGroupDecisions(reviewRequestTaskPayload.getReviewGroupDecisions());
        permitIssuanceRequestPayload.setReviewAttachments(reviewRequestTaskPayload.getReviewAttachments());
        permitIssuanceRequestPayload.setDetermination(reviewRequestTaskPayload.getDetermination());
    }

    private void updatePermitIssuanceReviewRequestTaskPayload(PermitIssuanceReviewRequestTaskPayload permitIssuanceReviewRequestTaskPayload,
        PermitIssuanceReviewRequestTaskActionPayload permitIssuanceReviewRequestTaskActionPayload) {
        permitIssuanceReviewRequestTaskPayload.setPermitType(permitIssuanceReviewRequestTaskActionPayload.getPermitType());
        permitIssuanceReviewRequestTaskPayload.setPermit(permitIssuanceReviewRequestTaskActionPayload.getPermit());
        permitIssuanceReviewRequestTaskPayload.setPermitSectionsCompleted(permitIssuanceReviewRequestTaskActionPayload.getPermitSectionsCompleted());
        permitIssuanceReviewRequestTaskPayload.setReviewSectionsCompleted(permitIssuanceReviewRequestTaskActionPayload.getReviewSectionsCompleted());
    }
}
