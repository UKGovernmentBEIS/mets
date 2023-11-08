package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalCloseApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalCloseJustification;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalSaveApplicationRequestTaskActionPayload;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesWithdrawalServiceTest {

    @Mock
    private WithholdingOfAllowancesWithdrawalSaveApplicationRequestTaskActionPayload saveActionPayload;

    @Mock
    private RequestTask requestTask;

    @Mock
    private WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload submitTaskPayload;

    @Mock
    private NotifyOperatorForDecisionRequestTaskActionPayload decisionActionPayload;

    @Mock
    private WithholdingOfAllowancesWithdrawalCloseApplicationRequestTaskActionPayload closeTaskActionPayload;

    @Mock
    private Request request;

    @Mock
    private WithholdingOfAllowancesRequestPayload requestPayload;

    @InjectMocks
    private WithholdingOfAllowancesWithdrawalService withdrawalService;

    @Test
    void applySavePayload() {
        when(requestTask.getPayload()).thenReturn(submitTaskPayload);
        withdrawalService.applySavePayload(saveActionPayload, requestTask);
        verify(submitTaskPayload).setSectionsCompleted(saveActionPayload.getSectionsCompleted());
        verify(submitTaskPayload).setWithholdingWithdrawal(saveActionPayload.getWithholdingWithdrawal());
    }

    @Test
    void saveWithholdingOfAllowancesWithdrawalDecisionNotification() {
        when(requestTask.getRequest()).thenReturn(request);
        when(requestTask.getPayload()).thenReturn(submitTaskPayload);
        when(request.getPayload()).thenReturn(requestPayload);

        withdrawalService.saveWithholdingOfAllowancesWithdrawalDecisionNotification(decisionActionPayload, requestTask);

        verify(requestPayload).setWithdrawDecisionNotification(decisionActionPayload.getDecisionNotification());
        verify(requestPayload).setWithholdingOfAllowancesWithdrawalSectionsCompleted(submitTaskPayload.getSectionsCompleted());
        verify(requestPayload).setWithholdingWithdrawal(submitTaskPayload.getWithholdingWithdrawal());
    }

    @Test
    void applyCloseAction() {
        when(requestTask.getRequest()).thenReturn(request);
        when(requestTask.getPayload()).thenReturn(submitTaskPayload);
        when(request.getPayload()).thenReturn(requestPayload);
        when(closeTaskActionPayload.getCloseJustification())
            .thenReturn(WithholdingOfAllowancesWithdrawalCloseJustification.builder().build());
        when(submitTaskPayload.getWithholdingWithdrawalAttachments()).thenReturn(Map.of(UUID.randomUUID(), "test"));

        withdrawalService.applyCloseAction(requestTask, closeTaskActionPayload);

        verify(requestPayload).setCloseJustification(closeTaskActionPayload.getCloseJustification());
        verify(requestPayload).setWithholdingOfAllowancesWithdrawalAttachments(submitTaskPayload.getWithholdingWithdrawalAttachments());
        verify(submitTaskPayload).setCloseJustification(closeTaskActionPayload.getCloseJustification());
    }
}
