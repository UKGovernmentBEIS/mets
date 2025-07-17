package uk.gov.pmrv.api.workflow.request.flow.installation.vir.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.validation.VirRespondToRegulatorCommentsValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class VirRespondToRegulatorCommentsValidatorTest {

    @InjectMocks
    private VirRespondToRegulatorCommentsValidator validator;

    @Test
    void validate() {
        final String reference = "A1";
        final Map<String, RegulatorImprovementResponse> regulatorImprovementResponses = Map.of(
                "A1", RegulatorImprovementResponse.builder().build()
        );
        final Map<String, OperatorImprovementFollowUpResponse> operatorImprovementFollowUpResponses = Map.of(
                "A1", OperatorImprovementFollowUpResponse.builder()
                        .improvementCompleted(false)
                        .reason("Reason")
                        .build()
        );

        // Invoke
        validator.validate(reference, operatorImprovementFollowUpResponses, regulatorImprovementResponses);
    }

    @Test
    void validate_not_valid() {
        final String reference = "A2";
        final Map<String, RegulatorImprovementResponse> regulatorImprovementResponses = Map.of(
                "A1", RegulatorImprovementResponse.builder().build()
        );
        final Map<String, OperatorImprovementFollowUpResponse> operatorImprovementFollowUpResponses = Map.of(
                "A1", OperatorImprovementFollowUpResponse.builder()
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
        final String reference = "A1";
        final Map<String, RegulatorImprovementResponse> regulatorImprovementResponses = Map.of(
                "A1", RegulatorImprovementResponse.builder().build()
        );

        // Invoke
        validator.validateReferenceOnRegulator(reference, regulatorImprovementResponses);
    }

    @Test
    void validateReferenceOnRegulator_not_valid() {
        final String reference = "A2";
        final Map<String, RegulatorImprovementResponse> regulatorImprovementResponses = Map.of(
                "A1", RegulatorImprovementResponse.builder().build()
        );

        // Invoke
        BusinessException be = assertThrows(BusinessException.class,
                () -> validator.validateReferenceOnRegulator(reference, regulatorImprovementResponses));

        // Verify
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }
}
