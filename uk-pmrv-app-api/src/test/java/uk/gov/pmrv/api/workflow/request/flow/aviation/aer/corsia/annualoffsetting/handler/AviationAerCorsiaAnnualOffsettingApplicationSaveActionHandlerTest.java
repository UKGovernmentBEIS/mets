package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.handler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service.AviationAerCorsiaAnnualOffsettingSubmitService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AviationAerCorsiaAnnualOffsettingApplicationSaveActionHandlerTest {

    @InjectMocks
    private AviationAerCorsiaAnnualOffsettingApplicationSaveActionHandler handler;

    @Mock
    private AviationAerCorsiaAnnualOffsettingSubmitService aviationAerCorsiaAnnualOffsettingSubmitService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestTask requestTask;

    @Mock
    private AppUser appUser;

    @Mock
    private AviationAerCorsiaAnnualOffsettingSaveRequestTaskActionPayload payload;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcess_success() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SAVE_APPLICATION;

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        handler.process(requestTaskId, requestTaskActionType, appUser, payload);

        verify(requestTaskService).findTaskById(requestTaskId);
        verify(aviationAerCorsiaAnnualOffsettingSubmitService).applySaveAction(requestTask, payload);
    }

    @Test
    void testGetTypes() {

        List<RequestTaskActionType> result = handler.getTypes();
        assertEquals(1, result.size());
        assertTrue(result.contains(RequestTaskActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SAVE_APPLICATION));
    }
}
