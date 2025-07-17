package uk.gov.pmrv.api.workflow.request.flow.installation.vir.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class VirSubmitValidatorServiceTest {

    @InjectMocks
    private VirSubmitValidatorService virSubmitValidatorService;

    @Test
    void validate() {
        final VirVerificationData verificationData = VirVerificationData.builder()
                .recommendedImprovements(Map.of(
                        "D1", VerifierComment.builder().build(),
                        "D2", VerifierComment.builder().build()
                ))
                .uncorrectedNonConformities(Map.of(
                        "B1", UncorrectedItem.builder().build()
                ))
                .build();
        final Map<String, OperatorImprovementResponse> operatorImprovements = Map.of(
                "D1", OperatorImprovementResponse.builder().build(),
                "D2", OperatorImprovementResponse.builder().build(),
                "B1", OperatorImprovementResponse.builder().build()
        );

        // Invoke
        virSubmitValidatorService.validate(operatorImprovements, verificationData);
    }

    @Test
    void validate_not_valid_missing_reference() {
        final VirVerificationData verificationData = VirVerificationData.builder()
                .recommendedImprovements(Map.of(
                        "D1", VerifierComment.builder().build(),
                        "D2", VerifierComment.builder().build()
                ))
                .uncorrectedNonConformities(Map.of(
                        "B1", UncorrectedItem.builder().build()
                ))
                .build();
        final Map<String, OperatorImprovementResponse> operatorImprovements = Map.of(
                "D1", OperatorImprovementResponse.builder().build()
        );

        // Invoke
        BusinessException be = assertThrows(BusinessException.class,
                () -> virSubmitValidatorService.validate(operatorImprovements, verificationData));

        // Verify
        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_VIR);
        assertThat(be.getData()).containsExactly("D2", "B1");
    }

    @Test
    void validate_not_valid_extra_reference() {
        final VirVerificationData verificationData = VirVerificationData.builder()
                .recommendedImprovements(Map.of(
                        "D1", VerifierComment.builder().build(),
                        "D2", VerifierComment.builder().build()
                ))
                .uncorrectedNonConformities(Map.of(
                        "B1", UncorrectedItem.builder().build()
                ))
                .build();
        final Map<String, OperatorImprovementResponse> operatorImprovements = Map.of(
                "D1", OperatorImprovementResponse.builder().build(),
                "D2", OperatorImprovementResponse.builder().build(),
                "B1", OperatorImprovementResponse.builder().build(),
                "B2", OperatorImprovementResponse.builder().build()
        );

        // Invoke
        BusinessException be = assertThrows(BusinessException.class,
                () -> virSubmitValidatorService.validate(operatorImprovements, verificationData));

        // Verify
        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_VIR);
        assertThat(be.getData()).containsExactly("B2");
    }
}
