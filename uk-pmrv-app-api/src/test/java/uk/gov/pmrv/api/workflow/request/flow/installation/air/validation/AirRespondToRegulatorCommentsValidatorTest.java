package uk.gov.pmrv.api.workflow.request.flow.installation.air.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;

@ExtendWith(MockitoExtension.class)
class AirRespondToRegulatorCommentsValidatorTest {

    @InjectMocks
    private AirRespondToRegulatorCommentsValidator validator;

    @Test
    void validate() {

        final Integer reference = 1;
        final Map<Integer, RegulatorAirImprovementResponse> regulatorImprovementResponses = Map.of(
            1, RegulatorAirImprovementResponse.builder().build()
        );
        final Map<Integer, OperatorAirImprovementFollowUpResponse> operatorImprovementFollowUpResponses = Map.of(
            1, OperatorAirImprovementFollowUpResponse.builder()
                .improvementCompleted(false)
                .reason("Reason")
                .build()
        );

        // Invoke
        validator.validate(reference, operatorImprovementFollowUpResponses, regulatorImprovementResponses);
    }

    @Test
    void validate_not_valid() {

        final Integer reference = 2;
        final Map<Integer, RegulatorAirImprovementResponse> regulatorImprovementResponses = Map.of(
            1, RegulatorAirImprovementResponse.builder().build()
        );
        final Map<Integer, OperatorAirImprovementFollowUpResponse> operatorImprovementFollowUpResponses = Map.of(
            1, OperatorAirImprovementFollowUpResponse.builder()
                .improvementCompleted(false)
                .reason("Reason")
                .build()
        );

        // Invoke
        BusinessException be = assertThrows(BusinessException.class,
            () -> validator.validate(reference, operatorImprovementFollowUpResponses, regulatorImprovementResponses));

        // Verify
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateReferenceOnRegulator() {

        final Integer reference = 1;
        final Map<Integer, RegulatorAirImprovementResponse> regulatorImprovementResponses = Map.of(
            1, RegulatorAirImprovementResponse.builder().build()
        );

        // Invoke
        validator.validateReferenceOnRegulator(reference, regulatorImprovementResponses);
    }

    @Test
    void validateReferenceOnRegulator_not_valid() {

        final Integer reference = 2;
        final Map<Integer, RegulatorAirImprovementResponse> regulatorImprovementResponses = Map.of(
            1, RegulatorAirImprovementResponse.builder().build()
        );

        // Invoke
        BusinessException be = assertThrows(BusinessException.class,
            () -> validator.validateReferenceOnRegulator(reference, regulatorImprovementResponses));

        // Verify
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }
}
