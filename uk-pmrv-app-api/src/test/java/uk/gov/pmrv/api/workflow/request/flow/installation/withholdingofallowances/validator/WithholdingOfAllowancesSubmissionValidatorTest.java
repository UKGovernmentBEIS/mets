package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowances;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationSubmitRequestTaskPayload;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesSubmissionValidatorTest {

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    @Mock
    private WithholdingOfAllowancesValidator withholdingOfAllowancesValidator;

    @InjectMocks
    private WithholdingOfAllowancesSubmissionValidator validator;

    @Test
    void validate_InvalidUsers() {
        RequestTask requestTask = new RequestTask();
        NotifyOperatorForDecisionRequestTaskActionPayload payload = new NotifyOperatorForDecisionRequestTaskActionPayload();
        PmrvUser pmrvUser = new PmrvUser();
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, payload.getDecisionNotification(), pmrvUser))
            .thenReturn(false);

        assertThrows(BusinessException.class,
            () -> validator.validate(requestTask, payload, pmrvUser));
    }

    @Test
    void validate() {
        NotifyOperatorForDecisionRequestTaskActionPayload payload = new NotifyOperatorForDecisionRequestTaskActionPayload();
        PmrvUser pmrvUser = new PmrvUser();
        WithholdingOfAllowancesApplicationSubmitRequestTaskPayload taskPayload =
            new WithholdingOfAllowancesApplicationSubmitRequestTaskPayload();
        RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .build();
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, payload.getDecisionNotification(), pmrvUser))
            .thenReturn(true);
        WithholdingOfAllowances withholdingOfAllowances = new WithholdingOfAllowances();
        taskPayload.setWithholdingOfAllowances(withholdingOfAllowances);

        validator.validate(requestTask, payload, pmrvUser);

        verify(decisionNotificationUsersValidator).areUsersValid(requestTask, payload.getDecisionNotification(),
            pmrvUser);
        verify(withholdingOfAllowancesValidator).validate(withholdingOfAllowances);
    }
}
