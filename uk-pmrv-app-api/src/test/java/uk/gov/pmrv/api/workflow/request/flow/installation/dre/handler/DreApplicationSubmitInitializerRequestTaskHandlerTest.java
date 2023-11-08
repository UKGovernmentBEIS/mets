package uk.gov.pmrv.api.workflow.request.flow.installation.dre.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;

@ExtendWith(MockitoExtension.class)
class DreApplicationSubmitInitializerRequestTaskHandlerTest {

	@InjectMocks
    private DreApplicationSubmitInitializerRequestTaskHandler cut;
	
	@Test
	void initializePayload_new() {
		DreRequestPayload requestPayload = DreRequestPayload.builder().build();
		Request request = Request.builder().payload(requestPayload).build();
		
		RequestTaskPayload result = cut.initializePayload(request);
		
		assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.DRE_APPLICATION_SUBMIT_PAYLOAD);
		assertThat(result).isInstanceOf(DreApplicationSubmitRequestTaskPayload.class);
		assertThat(result).isEqualTo(DreApplicationSubmitRequestTaskPayload.builder()
				.payloadType(RequestTaskPayloadType.DRE_APPLICATION_SUBMIT_PAYLOAD).build());
	}
	
	@Test
	void initializePayload_existing_Request_payload() {
		Dre dre = Dre.builder()
				.determinationReason(DreDeterminationReason.builder()
						.regulatorComments("regComments")
						.build())
				.build();
		DreRequestPayload requestPayload = DreRequestPayload.builder()
				.dre(dre)
				.build();
		Request request = Request.builder().payload(requestPayload).build();
		
		RequestTaskPayload result = cut.initializePayload(request);
		
		assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.DRE_APPLICATION_SUBMIT_PAYLOAD);
		assertThat(result).isInstanceOf(DreApplicationSubmitRequestTaskPayload.class);
		assertThat(result).isEqualTo(DreApplicationSubmitRequestTaskPayload.builder()
				.payloadType(RequestTaskPayloadType.DRE_APPLICATION_SUBMIT_PAYLOAD).dre(dre).build());
	}

    @Test
	void getRequestTaskTypes() {
		assertThat(cut.getRequestTaskTypes()).containsExactlyInAnyOrder(
				RequestTaskType.DRE_APPLICATION_SUBMIT);
	}
}
