package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaEmissions;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaApplicationSubmitInitializerTest {

    @InjectMocks
    private AviationDoECorsiaApplicationSubmitInitializer handler;

    @Test
    void initializePayload_doe_does_not_exist_initialize_empty_request_task_payload() {
        AviationDoECorsiaRequestPayload requestPayload = AviationDoECorsiaRequestPayload.builder().build();
        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload result = handler.initializePayload(request);

        assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD);
        assertThat(result).isInstanceOf(AviationDoECorsiaApplicationSubmitRequestTaskPayload.class);
        assertThat(result).isEqualTo(AviationDoECorsiaApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD).build());
        assertThat(((AviationDoECorsiaApplicationSubmitRequestTaskPayload) result).getDoe()).isNull();
    }

    @Test
    void initializePayload_doe_exists_initialize_request_task_payload_from_request_payload() {

        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .build())
                .emissions(AviationDoECorsiaEmissions.builder().build())
                .build();


        AviationDoECorsiaRequestPayload requestPayload = AviationDoECorsiaRequestPayload
                .builder()
                .doe(doe)
                .sectionCompleted(true)
                .build();
        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload result = handler.initializePayload(request);

        assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD);
        assertThat(result).isInstanceOf(AviationDoECorsiaApplicationSubmitRequestTaskPayload.class);
        assertThat(((AviationDoECorsiaApplicationSubmitRequestTaskPayload) result).getDoe()).isEqualTo(doe);
        assertThat(((AviationDoECorsiaApplicationSubmitRequestTaskPayload) result).getSectionCompleted()).isTrue();
    }




    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getRequestTaskTypes()).containsExactlyInAnyOrder(
                RequestTaskType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT);
    }
}
