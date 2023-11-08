package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureSubmitRequestTaskPayload;


@ExtendWith(MockitoExtension.class)
class AviationAccountClosureSubmitInitializerTest {

	@InjectMocks
    private AviationAccountClosureSubmitInitializer initializer;
	

    @Test
    void initializePayload() {        
    	AviationAccountClosureRequestPayload requestPayload = AviationAccountClosureRequestPayload.builder().build();
        Request request = Request.builder().payload(requestPayload).accountId(1L).build();

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);
        
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.AVIATION_ACCOUNT_CLOSURE_SUBMIT_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(AviationAccountClosureSubmitRequestTaskPayload.class);
    }

    @Test
    void getRequestTaskTypes() {
        assertEquals(initializer.getRequestTaskTypes(), Set.of(RequestTaskType.AVIATION_ACCOUNT_CLOSURE_SUBMIT));
    }
}
