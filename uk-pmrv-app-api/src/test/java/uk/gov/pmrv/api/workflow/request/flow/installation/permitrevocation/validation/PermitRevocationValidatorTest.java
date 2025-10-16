package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.validation;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRCreationValidationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class PermitRevocationValidatorTest {

    @InjectMocks
    private PermitRevocationValidator permitRevocationValidator;

    @Mock
    private Validator validator;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Mock
    private ALRCreationValidationService alrCreationValidationService;
    

    @Test
    void validateUsers() {

        final String userId = "userId";
        final AppUser appUser = AppUser.builder().userId(userId).build();
        final PermitRevocation permitRevocation = PermitRevocation.builder()
            .reason("reason")
            .activitiesStopped(false)
            .feeCharged(true)
            .build();
        final PermitRevocationApplicationSubmitRequestTaskPayload requestTaskPayload =
            PermitRevocationApplicationSubmitRequestTaskPayload.builder()
                .permitRevocation(permitRevocation)
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_REVOCATION_APPLICATION_SUBMIT)
            .payload(requestTaskPayload)
            .build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("user1", "user2"))
            .signatory("signatory")
            .build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)).thenReturn(
            true);

        permitRevocationValidator.validateNotifyUsers(requestTask, decisionNotification, appUser);

        verify(decisionNotificationUsersValidator, times(1))
            .areUsersValid(requestTask, decisionNotification, appUser);
    }

    @Test
    void validatePeerReviewer() {

        final String peerReviewer = "peerReviewer";
        final String userId = "userId";
        final AppUser appUser = AppUser.builder().userId(userId).build();

        permitRevocationValidator.validatePeerReviewer(peerReviewer, appUser);

        verify(peerReviewerTaskAssignmentValidator, times(1))
            .validate(RequestTaskType.PERMIT_REVOCATION_APPLICATION_PEER_REVIEW, peerReviewer, appUser);
    }

    @Test
    void whenNoALR_andNoALRProperties_thenValid() {
        PermitRevocationApplicationSubmitRequestTaskPayload payload = new PermitRevocationApplicationSubmitRequestTaskPayload();
        PermitRevocation permitRevocation = new PermitRevocation();
        permitRevocation.setAccountId(1L);
        payload.setPermitRevocation(permitRevocation);

        when(alrCreationValidationService.validateAccountEmitterTypeAndFreeAllocations(1L))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        permitRevocationValidator.validateSubmitRequestTaskPayload(payload);
        // no exception expected
    }

    @Test
    void whenHasALR_butNoALRProperties_thenThrows() {
        PermitRevocationApplicationSubmitRequestTaskPayload payload = new PermitRevocationApplicationSubmitRequestTaskPayload();
        PermitRevocation permitRevocation = new PermitRevocation();
        permitRevocation.setAccountId(1L);
        payload.setPermitRevocation(permitRevocation);

        when(alrCreationValidationService.validateAccountEmitterTypeAndFreeAllocations(1L))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        assertThrows(BusinessException.class,
                () -> permitRevocationValidator.validateSubmitRequestTaskPayload(payload));
    }
}