package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionType;

import java.util.Objects;

import static uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationType.CESSATION;

@Validated
@Service
@RequiredArgsConstructor
public class PermitNotificationReviewDecisionValidatorService {

    public void validatePermitNotificationDecision(PermitNotificationApplicationReviewRequestTaskPayload taskPayload,
                                                   PermitNotificationReviewDecision reviewDecision) {

        if (Objects.nonNull(taskPayload.getPermitNotification()) && CESSATION.equals(taskPayload.getPermitNotification().getType())){

            boolean isCessationWithErrorType = (reviewDecision.getType().equals(PermitNotificationReviewDecisionType.ACCEPTED)
                            || reviewDecision.getType().equals(PermitNotificationReviewDecisionType.REJECTED));

            if (isCessationWithErrorType) {
                throw new BusinessException(MetsErrorCode.INVALID_PERMIT_NOTIFICATION_CESSATION_DECISION);
            }

        } else if (Objects.nonNull(taskPayload.getPermitNotification()) && !CESSATION.equals(taskPayload.getPermitNotification().getType())){
            boolean hasLegalType = (reviewDecision.getType().equals(PermitNotificationReviewDecisionType.ACCEPTED)
                    || reviewDecision.getType().equals(PermitNotificationReviewDecisionType.REJECTED));

            if (!hasLegalType) {
                throw new BusinessException(MetsErrorCode.INVALID_PERMIT_NOTIFICATION_CESSATION_DECISION);
            }
        }
    }
}
