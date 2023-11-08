package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.validation;

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

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsReviewNotifyOperatorValidatorServiceTest {

	@InjectMocks
    private EmpVariationUkEtsReviewNotifyOperatorValidatorService reviewNotifyOperatorValidatorService;

    @Mock
    private EmpVariationUkEtsReviewDeterminationValidatorService reviewDeterminationValidatorService;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Test
    void validate_is_valid() {
        PmrvUser pmrvUser = PmrvUser.builder().build();
        EmpVariationDetermination determination = EmpVariationDetermination.builder()
            .type(EmpVariationDeterminationType.APPROVED)
            .build();
        EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
        		EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
                .determination(determination)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .build();

        DecisionNotification decisionNotification = DecisionNotification.builder().build();
        NotifyOperatorForDecisionRequestTaskActionPayload notifyOperatorRequestTaskActionPayload =
        		NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(decisionNotification)
                .build();

        when(reviewDeterminationValidatorService.isValid(requestTaskPayload, determination.getType())).thenReturn(true);
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser)).thenReturn(true);

        reviewNotifyOperatorValidatorService.validate(requestTask, notifyOperatorRequestTaskActionPayload, pmrvUser);

        verify(reviewDeterminationValidatorService,times(1)).validateDeterminationObject(determination);
        verify(reviewDeterminationValidatorService, times(1)).isValid(requestTaskPayload, determination.getType());
        verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask, decisionNotification, pmrvUser);

    }

    @Test
    void validate_invalid() {
        PmrvUser pmrvUser = PmrvUser.builder().build();
        EmpVariationDetermination determination = EmpVariationDetermination.builder()
            .type(EmpVariationDeterminationType.APPROVED)
            .build();
        EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
        		EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
                .determination(determination)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .build();

        DecisionNotification decisionNotification = DecisionNotification.builder().build();
        NotifyOperatorForDecisionRequestTaskActionPayload notifyOperatorRequestTaskActionPayload =
        		NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(decisionNotification)
                .build();

        when(reviewDeterminationValidatorService.isValid(requestTaskPayload, determination.getType())).thenReturn(false);

        BusinessException be = assertThrows(BusinessException.class,
            () -> reviewNotifyOperatorValidatorService.validate(requestTask, notifyOperatorRequestTaskActionPayload, pmrvUser));

        Assertions.assertEquals(ErrorCode.FORM_VALIDATION, be.getErrorCode());

        verify(reviewDeterminationValidatorService,times(1)).validateDeterminationObject(determination);
        verify(reviewDeterminationValidatorService, times(1)).isValid(requestTaskPayload, determination.getType());
        verifyNoInteractions(decisionNotificationUsersValidator);
    }
}
