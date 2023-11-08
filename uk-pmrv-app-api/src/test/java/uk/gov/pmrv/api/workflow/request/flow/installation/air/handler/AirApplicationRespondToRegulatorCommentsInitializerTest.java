package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovement;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementCalculationCO2;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementCalculationPFC;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementYesResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirReviewResponse;

@ExtendWith(MockitoExtension.class)
class AirApplicationRespondToRegulatorCommentsInitializerTest {

    @InjectMocks
    private AirApplicationRespondToRegulatorCommentsInitializer initializer;

    @Test
    void initializePayload() {


        final Map<Integer, AirImprovement> airImprovementMap = Map.of(
            1, AirImprovementCalculationPFC.builder()
                .type(MonitoringApproachType.CALCULATION_PFC)
                .categoryType(CategoryType.MAJOR)
                .tier("Tier 2")
                .build(),

            2, AirImprovementCalculationCO2.builder()
                .type(MonitoringApproachType.CALCULATION_CO2)
                .categoryType(CategoryType.MAJOR)
                .tier("Tier 1")
                .build());

        final Map<Integer, OperatorAirImprovementResponse> operatorImprovementResponses = Map.of(
            1,
            OperatorAirImprovementYesResponse.builder()
                .proposal("proposal")
                .proposedDate(LocalDate.of(2022, 1, 1))
                .build()
        );
        final LocalDate now = LocalDate.now();
        final Map<Integer, @NotNull @Valid RegulatorAirImprovementResponse> regulatorImprovementResponses = Map.of(
            1, RegulatorAirImprovementResponse.builder()
                .improvementRequired(true)
                .improvementDeadline(now)
                .comments("the comments")
                .build(),
            2, RegulatorAirImprovementResponse.builder().improvementRequired(false).build()
        );
        final RegulatorAirReviewResponse regulatorReviewResponse = RegulatorAirReviewResponse.builder()
            .regulatorImprovementResponses(regulatorImprovementResponses)
            .build();

        final Request request = Request.builder()
            .type(RequestType.AIR)
            .payload(AirRequestPayload.builder()
                .payloadType(RequestPayloadType.AIR_REQUEST_PAYLOAD)
                .airImprovements(airImprovementMap)
                .operatorImprovementResponses(operatorImprovementResponses)
                .regulatorReviewResponse(regulatorReviewResponse)
                .build())
            .build();

        final AirApplicationRespondToRegulatorCommentsRequestTaskPayload expected =
            AirApplicationRespondToRegulatorCommentsRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                .airImprovements(airImprovementMap)
                .operatorImprovementResponses(operatorImprovementResponses)
                .regulatorImprovementResponses(Map.of(
                    1, RegulatorAirImprovementResponse.builder()
                        .improvementRequired(true)
                        .improvementDeadline(now)
                        .build()))
                .build();

        // Invoke
        RequestTaskPayload actual = initializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(AirApplicationRespondToRegulatorCommentsRequestTaskPayload.class)
            .isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
            .containsExactly(RequestTaskType.AIR_RESPOND_TO_REGULATOR_COMMENTS);
    }
}
