package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class PermanentCessationSubmissionValidatorTest {

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Mock
    private PermanentCessationValidator permanentCessationValidator;

    @InjectMocks
    private PermanentCessationSubmissionValidator permanentCessationSubmissionValidator;

    @Mock
    private RequestTask requestTask;

    @Mock
    private NotifyOperatorForDecisionRequestTaskActionPayload payload;

    @Mock
    private AppUser appUser;

    @Mock
    private PermanentCessationApplicationSubmitRequestTaskPayload taskPayload;

    @BeforeEach
    void setUp() { }

    @Test
    void validate_shouldPassWhenUsersAreValidAndDataIsValid() {

        when(requestTask.getPayload()).thenReturn(taskPayload);
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, payload.getDecisionNotification(), appUser))
                .thenReturn(true);

        permanentCessationSubmissionValidator.validate(requestTask, payload, appUser);
        verify(permanentCessationValidator, times(1)).validate(taskPayload.getPermanentCessation());
    }

    @Test
    void validate_shouldThrowExceptionWhenUsersAreInvalid() {

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, payload.getDecisionNotification(), appUser))
                .thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                permanentCessationSubmissionValidator.validate(requestTask, payload, appUser));

        verify(permanentCessationValidator, never()).validate(any());
        assert(exception.getErrorCode().equals(ErrorCode.FORM_VALIDATION));
    }
}