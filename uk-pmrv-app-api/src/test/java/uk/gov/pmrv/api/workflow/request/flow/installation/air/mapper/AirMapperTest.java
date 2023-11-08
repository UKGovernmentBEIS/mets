package uk.gov.pmrv.api.workflow.request.flow.installation.air.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Year;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondedToRegulatorCommentsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovement;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementCalculationPFC;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementMeasurement;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementYesResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;

@ExtendWith(MockitoExtension.class)
class AirMapperTest {

    private final AirMapper AIR_MAPPER = Mappers.getMapper(AirMapper.class);

    @Test
    void toAirApplicationRespondedToRegulatorCommentsRequestActionPayload() {

        final UUID file1 = UUID.randomUUID();
        final UUID file2 = UUID.randomUUID();
        
        final AirImprovement airImprovement1 = AirImprovementCalculationPFC.builder().build();
        final AirImprovement airImprovement2 = AirImprovementMeasurement.builder().build();
        
        final OperatorAirImprovementYesResponse operatorResponse1 =
            OperatorAirImprovementYesResponse.builder().files(Set.of(file1)).build();
        final OperatorAirImprovementYesResponse operatorResponse2 =
            OperatorAirImprovementYesResponse.builder().files(Set.of(file2)).build();

        final RegulatorAirImprovementResponse regulatorResponse1 = RegulatorAirImprovementResponse.builder().officialResponse("officialResponse1").build();
        final RegulatorAirImprovementResponse regulatorResponse2 = RegulatorAirImprovementResponse.builder().officialResponse("officialResponse2").build();
        final AirApplicationRespondToRegulatorCommentsRequestTaskPayload taskPayload =
            AirApplicationRespondToRegulatorCommentsRequestTaskPayload.builder()
                .airImprovements(Map.of(1, airImprovement1, 2, airImprovement2))
                .operatorImprovementResponses(Map.of(1, operatorResponse1, 2, operatorResponse2))
                .regulatorImprovementResponses(Map.of(1, regulatorResponse1, 2, regulatorResponse2))
                .airAttachments(Map.of(file1, "file1", file2, "file2"))
                .build();

        final Year year = Year.of(2022);

        final OperatorAirImprovementFollowUpResponse followUpResponse =
            OperatorAirImprovementFollowUpResponse.builder().build();

        final Integer reference = 1;

        final AirApplicationRespondedToRegulatorCommentsRequestActionPayload expected =
            AirApplicationRespondedToRegulatorCommentsRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD)
                .reportingYear(year)
                .reference(reference)
                .airImprovement(airImprovement1)
                .operatorImprovementResponse(operatorResponse1)
                .regulatorImprovementResponse(regulatorResponse1)
                .operatorImprovementFollowUpResponse(followUpResponse)
                .airAttachments(Map.of(file1, "file1"))
                .build();

        final AirApplicationRespondedToRegulatorCommentsRequestActionPayload actual =
            AIR_MAPPER.toAirApplicationRespondedToRegulatorCommentsRequestActionPayload(
                taskPayload,
                year,
                followUpResponse,
                reference
            );

        assertEquals(expected, actual);
    }
}
