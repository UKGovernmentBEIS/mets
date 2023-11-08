package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesApplySaveActionHandlerTest {

    @Mock
    private WithholdingOfAllowancesService withHoldingOfAllowancesService;

    @Mock
    private RequestTaskService requestTaskService;

    @InjectMocks
    private WithholdingOfAllowancesApplySaveActionHandler actionHandler;

    @Test
    void process_shouldCallApplySavePayload() {
        Long requestTaskId = 123L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_SAVE_APPLICATION;
        PmrvUser pmrvUser = mock(PmrvUser.class);
        WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload actionPayload =
            mock(WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload.class);
        RequestTask requestTask = mock(RequestTask.class);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        actionHandler.process(requestTaskId, requestTaskActionType, pmrvUser, actionPayload);

        verify(withHoldingOfAllowancesService).applySavePayload(actionPayload, requestTask);
    }

    @Test
    void getTypes_shouldReturnListOfWithholdingOfAllowancesSaveApplicationType() {
        List<RequestTaskActionType> types = actionHandler.getTypes();
        assertEquals(List.of(RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_SAVE_APPLICATION), types);
    }
}
