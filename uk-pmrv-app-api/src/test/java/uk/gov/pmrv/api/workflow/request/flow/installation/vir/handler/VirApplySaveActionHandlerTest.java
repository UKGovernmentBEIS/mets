package uk.gov.pmrv.api.workflow.request.flow.installation.vir.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.service.RequestVirApplyService;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VirApplySaveActionHandlerTest {

    @InjectMocks
    private VirApplySaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestVirApplyService requestVirApplyService;

    @Test
    void doProcess() {
        VirSaveApplicationRequestTaskActionPayload virApplySavePayload =
                VirSaveApplicationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AER_SAVE_APPLICATION_PAYLOAD)
                        .operatorImprovementResponses(Map.of("A1",
                                OperatorImprovementResponse.builder().isAddressed(false).addressedDescription("description").build()
                        ))
                        .build();

        RequestTask requestTask = RequestTask.builder().id(1L).build();
        AppUser appUser = AppUser.builder().build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(), RequestTaskActionType.VIR_SAVE_APPLICATION, appUser, virApplySavePayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(1L);
        verify(requestVirApplyService, times(1)).applySaveAction(virApplySavePayload, requestTask);
    }

    @Test
    void getTypes() {
        List<RequestTaskActionType> actual = handler.getTypes();

        assertThat(actual).isEqualTo(List.of(RequestTaskActionType.VIR_SAVE_APPLICATION));
    }
}
