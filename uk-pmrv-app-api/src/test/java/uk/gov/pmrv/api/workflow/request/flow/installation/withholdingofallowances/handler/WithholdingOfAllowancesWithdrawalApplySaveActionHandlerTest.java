package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesWithdrawalService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesWithdrawalApplySaveActionHandlerTest {

    @Mock
    private WithholdingOfAllowancesWithdrawalService withholdingOfAllowancesWithdrawalService;

    @Mock
    private RequestTaskService requestTaskService;

    @InjectMocks
    private WithholdingOfAllowancesWithdrawalApplySaveActionHandler actionHandler;

    @Test
    void process() {
        Long requestTaskId = 1L;
        PmrvUser pmrvUser = new PmrvUser();
        WithholdingOfAllowancesWithdrawalSaveApplicationRequestTaskActionPayload actionPayload =
            new WithholdingOfAllowancesWithdrawalSaveApplicationRequestTaskActionPayload();
        RequestTask requestTask = new RequestTask();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        actionHandler.process(
            requestTaskId,
            RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION,
            pmrvUser,
            actionPayload
        );

        verify(requestTaskService).findTaskById(requestTaskId);
        verify(withholdingOfAllowancesWithdrawalService).applySavePayload(actionPayload, requestTask);
    }

    @Test
    void getTypes() {
        List<RequestTaskActionType> actionTypes = actionHandler.getTypes();
        Assertions.assertEquals(
            List.of(RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION), actionTypes
        );
    }
}
