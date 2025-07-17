package uk.gov.pmrv.api.workflow.request.flow.installation.air.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementYesResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirReviewResponse;

@ExtendWith(MockitoExtension.class)
class AirReviewValidatorTest {

    @InjectMocks
    private AirReviewValidator validator;

    @Test
    void validate() {

        final Map<Integer, RegulatorAirImprovementResponse> regulatorAirImprovementResponses =
            Map.of(1, RegulatorAirImprovementResponse.builder().build());
        final Map<Integer, OperatorAirImprovementResponse> operatorImprovementResponses = Map.of(
            1, OperatorAirImprovementYesResponse.builder().build()
        );
        final AirApplicationReviewRequestTaskPayload taskPayload = AirApplicationReviewRequestTaskPayload.builder()
            .operatorImprovementResponses(operatorImprovementResponses)
            .regulatorReviewResponse(RegulatorAirReviewResponse.builder().regulatorImprovementResponses(regulatorAirImprovementResponses).build())
            .build();

        assertDoesNotThrow(() -> validator.validate(taskPayload));
    }

    @Test
    void validate_missing_reference() {

        final Map<Integer, RegulatorAirImprovementResponse> regulatorAirImprovementResponses =
            Map.of(1, RegulatorAirImprovementResponse.builder().build());
        final Map<Integer, OperatorAirImprovementResponse> operatorImprovementResponses = Map.of(
            1, OperatorAirImprovementYesResponse.builder().build(),
            2, OperatorAirImprovementYesResponse.builder().build()
        );
        final AirApplicationReviewRequestTaskPayload taskPayload = AirApplicationReviewRequestTaskPayload.builder()
            .operatorImprovementResponses(operatorImprovementResponses)
            .regulatorReviewResponse(RegulatorAirReviewResponse.builder().regulatorImprovementResponses(regulatorAirImprovementResponses).build())
            .build();

        // Invoke
        BusinessException be = assertThrows(BusinessException.class,
            () -> validator.validate(taskPayload));

        // Verify
        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_AIR_REVIEW);
        assertThat(be.getData()).containsExactly(2);
    }
}
