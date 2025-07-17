package uk.gov.pmrv.api.workflow.request.flow.installation.common.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationContainer;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class PermitCessationNotifyOperatorValidatorTest {

    @InjectMocks
    private PermitCessationNotifyOperatorValidator cessationNotifyOperatorValidator;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Mock
    private PermitCessationValidator permitCessationValidator;

    @Test
    void validate() {
        AppUser appUser = AppUser.builder().build();
        NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
            .decisionNotification(DecisionNotification.builder().build())
            .build();
        PermitCessationSubmitRequestTaskPayload requestTaskPayload =
            PermitCessationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD)
                .cessationContainer(PermitCessationContainer.builder().build())
            .build();

        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .build();

        doNothing().when(permitCessationValidator).validate(requestTaskPayload.getCessationContainer());
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, taskActionPayload.getDecisionNotification(), appUser))
            .thenReturn(true);

        cessationNotifyOperatorValidator.validate(requestTask, appUser, taskActionPayload);
    }

    @Test
    void validate_throws_form_validation_exception() {
        AppUser appUser = AppUser.builder().build();
        NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
            .decisionNotification(DecisionNotification.builder().build())
            .build();
        PermitCessationSubmitRequestTaskPayload requestTaskPayload =
            PermitCessationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD)
                .cessationContainer(PermitCessationContainer.builder().build())
                .build();

        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .build();

        doNothing().when(permitCessationValidator).validate(requestTaskPayload.getCessationContainer());
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, taskActionPayload.getDecisionNotification(), appUser))
            .thenReturn(false);

        BusinessException be = assertThrows(BusinessException.class,
            () -> cessationNotifyOperatorValidator.validate(requestTask, appUser, taskActionPayload));

        assertEquals(ErrorCode.FORM_VALIDATION, be.getErrorCode());
    }
}