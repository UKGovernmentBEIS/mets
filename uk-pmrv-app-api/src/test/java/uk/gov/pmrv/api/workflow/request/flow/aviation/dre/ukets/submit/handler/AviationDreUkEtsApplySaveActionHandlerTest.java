package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain.AviationDreUkEtsSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.service.RequestAviationDreUkEtsApplyService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationDreUkEtsApplySaveActionHandlerTest {

    @InjectMocks
    private AviationDreUkEtsApplySaveActionHandler applySaveActionHandler;

    @Mock
    private RequestAviationDreUkEtsApplyService requestAviationDreUkEtsApplyService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        AppUser appUser = AppUser.builder().build();
        AviationDreUkEtsSaveApplicationRequestTaskActionPayload taskActionPayload =
            AviationDreUkEtsSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_DRE_UKETS_SAVE_APPLICATION_PAYLOAD)
                .dre(AviationDre.builder().build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        applySaveActionHandler.process(requestTask.getId(), RequestTaskActionType.AVIATION_DRE_UKETS_SAVE_APPLICATION,
            appUser, taskActionPayload);

        // Verify
        verify(requestAviationDreUkEtsApplyService, times(1)).applySaveAction(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(applySaveActionHandler.getTypes())
            .containsOnly(RequestTaskActionType.AVIATION_DRE_UKETS_SAVE_APPLICATION);
    }
}