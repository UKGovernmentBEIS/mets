package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload;

import java.util.Objects;

import static uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationType.CESSATION;

@Service
@RequiredArgsConstructor
public class RequestPermitNotificationReviewService {

    private final PermitNotificationReviewDecisionValidatorService decisionValidatorService;

    @Transactional
    public void saveReviewDecision(PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload,
                                   RequestTask requestTask) {
        final PermitNotificationReviewDecision reviewDecision = taskActionPayload.getReviewDecision();

        final PermitNotificationApplicationReviewRequestTaskPayload taskPayload =
                (PermitNotificationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        decisionValidatorService.validatePermitNotificationDecision(taskPayload, reviewDecision);
        taskPayload.setReviewDecision(reviewDecision);
    }

    @Transactional
    public void saveRequestPeerReviewAction(RequestTask requestTask, String selectedPeerReview, AppUser appUser) {
        final Request request = requestTask.getRequest();
        final PermitNotificationApplicationReviewRequestTaskPayload taskPayload =
                (PermitNotificationApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();

        requestPayload.setReviewDecision(taskPayload.getReviewDecision());
        requestPayload.setRegulatorReviewer(appUser.getUserId());
        requestPayload.setRegulatorPeerReviewer(selectedPeerReview);
    }
}
