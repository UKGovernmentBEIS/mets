package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirApplyService;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;

@ExtendWith(MockitoExtension.class)
class AviationVirApplySaveActionHandlerTest {

    @InjectMocks
    private AviationVirApplySaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationVirApplyService applyService;

    @Test
    void doProcess() {

        final AviationVirSaveApplicationRequestTaskActionPayload virApplySavePayload =
            AviationVirSaveApplicationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AVIATION_VIR_SAVE_APPLICATION_PAYLOAD)
                        .operatorImprovementResponses(Map.of("A1",
                                OperatorImprovementResponse.builder().isAddressed(false).addressedDescription("description").build()
                        ))
                        .build();

        final RequestTask requestTask = RequestTask.builder().id(1L).build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(), RequestTaskActionType.AVIATION_VIR_SAVE_APPLICATION, pmrvUser, virApplySavePayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(1L);
        verify(applyService, times(1)).applySaveAction(virApplySavePayload, requestTask);
    }

    @Test
    void getTypes() {
        List<RequestTaskActionType> actual = handler.getTypes();

        assertThat(actual).isEqualTo(List.of(RequestTaskActionType.AVIATION_VIR_SAVE_APPLICATION));
    }
}
