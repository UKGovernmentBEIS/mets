package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

public class PermitNotificationReviewDecisionValidatorServiceTest {

    private PermitNotificationReviewDecisionValidatorService validatorService;

    @BeforeEach
    void setUp() {
        validatorService = new PermitNotificationReviewDecisionValidatorService();
    }

    @Test
    void validate_whenPermitNotificationIsNull_shouldNotThrowException() {
        PermitNotificationApplicationReviewRequestTaskPayload payload = new PermitNotificationApplicationReviewRequestTaskPayload();
        payload.setPermitNotification(null);

        PermitNotificationReviewDecision decision = new PermitNotificationReviewDecision();
        decision.setType(PermitNotificationReviewDecisionType.ACCEPTED);

        assertThatCode(() -> validatorService.validatePermitNotificationDecision(payload, decision))
                .doesNotThrowAnyException();
    }

    @Test
    void validate_whenCessationAndAccepted_shouldThrowException() {
        PermitNotificationApplicationReviewRequestTaskPayload payload = new PermitNotificationApplicationReviewRequestTaskPayload();
        PermitNotification permitNotification = new CessationNotification();
        permitNotification.setType(PermitNotificationType.CESSATION);
        payload.setPermitNotification(permitNotification);

        PermitNotificationReviewDecision decision = new PermitNotificationReviewDecision();
        decision.setType(PermitNotificationReviewDecisionType.ACCEPTED);

        assertThatThrownBy(() -> validatorService.validatePermitNotificationDecision(payload, decision))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(MetsErrorCode.INVALID_PERMIT_NOTIFICATION_CESSATION_DECISION.getMessage());
    }

    @Test
    void validate_whenCessationAndRejected_shouldThrowException() {
        PermitNotificationApplicationReviewRequestTaskPayload payload = new PermitNotificationApplicationReviewRequestTaskPayload();
        PermitNotification permitNotification = new CessationNotification();
        permitNotification.setType(PermitNotificationType.CESSATION);
        payload.setPermitNotification(permitNotification);

        PermitNotificationReviewDecision decision = new PermitNotificationReviewDecision();
        decision.setType(PermitNotificationReviewDecisionType.REJECTED);

        assertThatThrownBy(() -> validatorService.validatePermitNotificationDecision(payload, decision))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(MetsErrorCode.INVALID_PERMIT_NOTIFICATION_CESSATION_DECISION.getMessage());
    }

    @Test
    void validate_whenNonCessationAndAccepted_shouldNotThrowException() {
        PermitNotificationApplicationReviewRequestTaskPayload payload = new PermitNotificationApplicationReviewRequestTaskPayload();
        PermitNotification permitNotification = new TemporaryChange();
        permitNotification.setType(PermitNotificationType.CESSATION);
        payload.setPermitNotification(permitNotification);

        PermitNotificationReviewDecision decision = new PermitNotificationReviewDecision();
        decision.setType(PermitNotificationReviewDecisionType.CESSATION_TREATED_AS_PERMANENT);

        assertThatCode(() -> validatorService.validatePermitNotificationDecision(payload, decision))
                .doesNotThrowAnyException();
    }

    @Test
    void validate_whenNonCessationWithInvalidDecision_shouldThrowException() {
        PermitNotificationApplicationReviewRequestTaskPayload payload = new PermitNotificationApplicationReviewRequestTaskPayload();
        PermitNotification permitNotification = new TemporaryChange();
        permitNotification.setType(PermitNotificationType.TEMPORARY_CHANGE);
        payload.setPermitNotification(permitNotification);

        PermitNotificationReviewDecision decision = new PermitNotificationReviewDecision();
        decision.setType(PermitNotificationReviewDecisionType.PERMANENT_CESSATION); // invalid for non-cessation

        assertThatThrownBy(() -> validatorService.validatePermitNotificationDecision(payload, decision))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(MetsErrorCode.INVALID_PERMIT_NOTIFICATION_CESSATION_DECISION.getMessage());
    }
}
