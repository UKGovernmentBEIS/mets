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
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovement;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementCalculationCO2;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementFallback;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementYesResponse;

@ExtendWith(MockitoExtension.class)
class AirApplicationReviewInitializerTest {

    @InjectMocks
    private AirApplicationReviewInitializer initializer;

    @Test
    void initializePayload() {

        final Map<Integer, AirImprovement> airImprovements =
            Map.of(1, AirImprovementCalculationCO2.builder().sourceStreamReference("ref 1").build(),
                2, AirImprovementFallback.builder().sourceStreamReference("ref 2").build());
        
        final Map<Integer, OperatorAirImprovementResponse> operatorImprovementResponses =
            Map.of(1, OperatorAirImprovementYesResponse.builder().proposal("proposal").build());

        final Request request = Request.builder()
            .type(RequestType.AIR)
            .payload(AirRequestPayload.builder()
                .payloadType(RequestPayloadType.AIR_REQUEST_PAYLOAD)
                .airImprovements(airImprovements)
                .operatorImprovementResponses(operatorImprovementResponses)
                .build())
            .build();

        final AirApplicationReviewRequestTaskPayload expected = AirApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AIR_APPLICATION_REVIEW_PAYLOAD)
            .airImprovements(airImprovements)
            .operatorImprovementResponses(operatorImprovementResponses)
            .build();

        // Invoke
        RequestTaskPayload actual = initializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(AirApplicationReviewRequestTaskPayload.class).isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {

        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.AIR_APPLICATION_REVIEW);
    }
}
