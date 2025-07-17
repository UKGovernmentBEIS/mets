package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain.AviationDreUkEtsApplicationSubmitRequestTaskPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AviationDreApplicationSubmitInitializerRequestTaskHandlerTest {

    @InjectMocks
    private AviationDreApplicationSubmitInitializerRequestTaskHandler handler;

    @Test
    void initializePayload_new() {
        AviationDreUkEtsRequestPayload requestPayload = AviationDreUkEtsRequestPayload.builder().build();
        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload result = handler.initializePayload(request);

        assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD);
        assertThat(result).isInstanceOf(AviationDreUkEtsApplicationSubmitRequestTaskPayload.class);
        assertThat(result).isEqualTo(AviationDreUkEtsApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD).build());
    }

    @Test
    void initializePayload_existing_Request_payload() {
        AviationDre dre = AviationDre.builder()
            .determinationReason(AviationDreDeterminationReason.builder()
                .furtherDetails("furtherDetails")
                .type(AviationDreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
                .build())
            .build();
        AviationDreUkEtsRequestPayload requestPayload = AviationDreUkEtsRequestPayload.builder()
            .dre(dre)
            .build();
        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload result = handler.initializePayload(request);

        assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD);
        assertThat(result).isInstanceOf(AviationDreUkEtsApplicationSubmitRequestTaskPayload.class);
        assertThat(result).isEqualTo(AviationDreUkEtsApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD).dre(dre).build());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getRequestTaskTypes()).containsExactlyInAnyOrder(
                RequestTaskType.AVIATION_DRE_UKETS_APPLICATION_SUBMIT);
    }
}
