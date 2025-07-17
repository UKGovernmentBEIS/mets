package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecisionType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationValidatorServiceTest {

    @InjectMocks
    private PermitNotificationValidatorService validator;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Test
    void validateNotifyUsers() {
        final RequestTask requestTask = RequestTask.builder().build();
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final AppUser appUser = AppUser.builder().build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)).thenReturn(true);

        // Invoke
        validator.validateNotifyUsers(requestTask, decisionNotification, appUser);

        // Verify
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification, appUser);
    }

    @Test
    void validateNotifyUsers_not_valid() {
        final RequestTask requestTask = RequestTask.builder().build();
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final AppUser appUser = AppUser.builder().build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)).thenReturn(false);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> validator.validateNotifyUsers(requestTask, decisionNotification, appUser));

        // Verify
        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification, appUser);
    }

    @Test
    void validatePeerReviewer() {
        String peerReviewer = "selectedPeerReviewer";
        AppUser appUser = AppUser.builder().userId("userId").build();

        // Invoke
        validator.validatePeerReviewer(peerReviewer, appUser);

        // Verify
        verify(peerReviewerTaskAssignmentValidator, times(1))
                .validate(RequestTaskType.PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW, peerReviewer, appUser);
    }

    @Test
    void validateNotificationFollowUpReviewDecision() {

        final PermitNotificationFollowUpReviewDecision reviewDecision = 
            PermitNotificationFollowUpReviewDecision.builder()
                .type(PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED)
                .build();

        final BusinessException businessException = assertThrows(BusinessException.class,
            () -> validator.validateNotificationFollowUpReviewDecision(reviewDecision));

        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
    }
}
