package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler.PermitTransferAApplicationSubmitInitializer;

@ExtendWith(MockitoExtension.class)
class PermitTransferAApplicationSubmitInitializerTest {

    @InjectMocks
    private PermitTransferAApplicationSubmitInitializer initializer;

    @Test
    void initializePayload() {

        final Long accountId = 1L;
        final Request request = Request.builder().accountId(accountId).build();

        final RequestTaskPayload result = initializer.initializePayload(request);

        assertEquals(RequestTaskPayloadType.PERMIT_TRANSFER_A_APPLICATION_SUBMIT_PAYLOAD, result.getPayloadType());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.PERMIT_TRANSFER_A_APPLICATION_SUBMIT);
    }
}
