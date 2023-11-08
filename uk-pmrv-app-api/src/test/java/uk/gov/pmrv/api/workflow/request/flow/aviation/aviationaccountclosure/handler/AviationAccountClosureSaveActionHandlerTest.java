package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosure;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.service.RequestAviationAccountClosureService;

@ExtendWith(MockitoExtension.class)
class AviationAccountClosureSaveActionHandlerTest {

	@InjectMocks
    private AviationAccountClosureSaveActionHandler handler;

    @Mock
    private RequestAviationAccountClosureService requestAviationAccountClosureService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {

        final AviationAccountClosureSaveRequestTaskActionPayload actionPayload =
        		AviationAccountClosureSaveRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_ACCOUNT_CLOSURE_SAVE_APPLICATION_PAYLOAD)
                .aviationAccountClosure(AviationAccountClosure.builder().reason("test reason").build())
                .build();

        final RequestTask requestTask = RequestTask.builder().id(1L).build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTask.getId(),
            RequestTaskActionType.AVIATION_ACCOUNT_CLOSURE_SAVE_APPLICATION,
            pmrvUser,
            actionPayload);

        verify(requestAviationAccountClosureService, times(1)).applySavePayload(actionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).isEqualTo(
        		List.of(RequestTaskActionType.AVIATION_ACCOUNT_CLOSURE_SAVE_APPLICATION));
    }
}
