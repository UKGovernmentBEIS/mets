package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service.ReturnOfAllowancesReturnedService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnOfAllowancesReturnedApplySaveActionHandlerTest {

    @Mock
    private ReturnOfAllowancesReturnedService returnOfAllowancesReturnedService;

    @Mock
    private RequestTaskService requestTaskService;

    @InjectMocks
    private ReturnOfAllowancesReturnedApplySaveActionHandler actionHandler;

    @Test
    void process() {
        Long requestTaskId = 123L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.RETURN_OF_ALLOWANCES_RETURNED_SAVE_APPLICATION;
        PmrvUser pmrvUser = mock(PmrvUser.class);
        ReturnOfAllowancesReturnedSaveApplicationRequestTaskActionPayload actionPayload =
            mock(ReturnOfAllowancesReturnedSaveApplicationRequestTaskActionPayload.class);
        RequestTask requestTask = mock(RequestTask.class);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        actionHandler.process(requestTaskId, requestTaskActionType, pmrvUser, actionPayload);

        verify(returnOfAllowancesReturnedService).applySavePayload(actionPayload, requestTask);
    }

    @Test
    void getTypes() {
        List<RequestTaskActionType> types = actionHandler.getTypes();
        assertEquals(List.of(RequestTaskActionType.RETURN_OF_ALLOWANCES_RETURNED_SAVE_APPLICATION), types);
    }

}