package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingWithdrawal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesWithdrawnValidatorTest {

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    @Mock
    private WithholdingOfAllowancesWithdrawalValidator withholdingOfAllowancesWithdrawalValidator;

    @InjectMocks
    private WithholdingOfAllowancesWithdrawnValidator validator;

    @Test
    void validate_should_throw_exception_for_invalid_users() {
        RequestTask requestTask = new RequestTask();
        NotifyOperatorForDecisionRequestTaskActionPayload payload = new NotifyOperatorForDecisionRequestTaskActionPayload();
        AppUser appUser = new AppUser();
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, payload.getDecisionNotification(), appUser))
            .thenReturn(false);

        assertThrows(BusinessException.class,
            () -> validator.validate(requestTask, payload, appUser));
    }

    @Test
    void validate() {
        NotifyOperatorForDecisionRequestTaskActionPayload payload = new NotifyOperatorForDecisionRequestTaskActionPayload();
        AppUser appUser = new AppUser();
        WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload taskPayload =
            new WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload();
        RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .build();
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, payload.getDecisionNotification(), appUser))
            .thenReturn(true);
        WithholdingWithdrawal withholdingWithdrawal = WithholdingWithdrawal.builder().build();
        taskPayload.setWithholdingWithdrawal(withholdingWithdrawal);

        validator.validate(requestTask, payload, appUser);

        verify(decisionNotificationUsersValidator).areUsersValid(requestTask, payload.getDecisionNotification(),
            appUser);
        verify(withholdingOfAllowancesWithdrawalValidator).validate(withholdingWithdrawal);
    }

}