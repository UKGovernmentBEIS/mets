package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovement;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementCalculationCO2;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementFallback;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;

@ExtendWith(MockitoExtension.class)
class AirApplicationSubmitInitializerTest {

    @InjectMocks
    private AirApplicationSubmitInitializer initializer;

    @Test
    void initializePayload() {

        final Map<Integer, AirImprovement> airImprovements =
            Map.of(1, AirImprovementCalculationCO2.builder().sourceStreamReference("ref 1").build(),
                2, AirImprovementFallback.builder().sourceStreamReference("ref 2").build());

        final Request request = Request.builder()
            .type(RequestType.AIR)
            .payload(AirRequestPayload.builder()
                .payloadType(RequestPayloadType.AIR_REQUEST_PAYLOAD)
                .airImprovements(airImprovements)
                .build())
            .build();

        final AirApplicationSubmitRequestTaskPayload expected = AirApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AIR_APPLICATION_SUBMIT_PAYLOAD)
            .airImprovements(airImprovements)
            .build();

        // Invoke
        RequestTaskPayload actual = initializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(AirApplicationSubmitRequestTaskPayload.class).isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {

        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.AIR_APPLICATION_SUBMIT);
    }
}
