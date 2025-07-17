package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.validation;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaReviewNotifyOperatorValidatorServiceTest {

    @InjectMocks
    private EmpIssuanceCorsiaReviewNotifyOperatorValidatorService reviewNotifyOperatorValidatorService;

    @Mock
    private EmpIssuanceCorsiaReviewDeterminationValidatorService reviewDeterminationValidatorService;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Test
    void validate_is_valid() {
        AppUser appUser = AppUser.builder().build();
        EmpIssuanceDetermination determination = EmpIssuanceDetermination.builder()
            .type(EmpIssuanceDeterminationType.APPROVED)
            .build();
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload =
            EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder()
                .determination(determination)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .build();

        DecisionNotification decisionNotification = DecisionNotification.builder().build();
        EmpIssuanceCorsiaNotifyOperatorForDecisionRequestTaskActionPayload notifyOperatorRequestTaskActionPayload =
            EmpIssuanceCorsiaNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(decisionNotification)
                .build();

        when(reviewDeterminationValidatorService.isValid(requestTaskPayload, determination.getType())).thenReturn(true);
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)).thenReturn(true);

        reviewNotifyOperatorValidatorService.validate(requestTask, notifyOperatorRequestTaskActionPayload, appUser);

        verify(reviewDeterminationValidatorService,times(1)).validateDeterminationObject(determination);
        verify(reviewDeterminationValidatorService, times(1)).isValid(requestTaskPayload, determination.getType());
        verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask, decisionNotification, appUser);

    }

    @Test
    void validate_invalid() {
        AppUser appUser = AppUser.builder().build();
        EmpIssuanceDetermination determination = EmpIssuanceDetermination.builder()
            .type(EmpIssuanceDeterminationType.APPROVED)
            .build();
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload =
            EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder()
                .determination(determination)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .build();

        DecisionNotification decisionNotification = DecisionNotification.builder().build();
        EmpIssuanceCorsiaNotifyOperatorForDecisionRequestTaskActionPayload notifyOperatorRequestTaskActionPayload =
            EmpIssuanceCorsiaNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(decisionNotification)
                .build();

        when(reviewDeterminationValidatorService.isValid(requestTaskPayload, determination.getType())).thenReturn(false);

        BusinessException be = assertThrows(BusinessException.class,
            () -> reviewNotifyOperatorValidatorService.validate(requestTask, notifyOperatorRequestTaskActionPayload, appUser));

        Assertions.assertEquals(ErrorCode.FORM_VALIDATION, be.getErrorCode());

        verify(reviewDeterminationValidatorService,times(1)).validateDeterminationObject(determination);
        verify(reviewDeterminationValidatorService, times(1)).isValid(requestTaskPayload, determination.getType());
        verifyNoInteractions(decisionNotificationUsersValidator);
    }
}