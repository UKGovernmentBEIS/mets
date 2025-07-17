package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.validation;

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
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

@ExtendWith(MockitoExtension.class)
class AviationVirSubmitValidatorTest {

    @InjectMocks
    private AviationVirSubmitValidator validator;

    @Test
    void validate() {

        final VirVerificationData verificationData = VirVerificationData.builder()
            .uncorrectedNonConformities(Map.of(
                "A1", UncorrectedItem.builder().build(),
                "A2", UncorrectedItem.builder().build()
            ))
            .priorYearIssues(Map.of(
                "B1", VerifierComment.builder().build()
            ))
            .build();
        final Map<String, OperatorImprovementResponse> operatorImprovements = Map.of(
            "A1", OperatorImprovementResponse.builder().build(),
            "A2", OperatorImprovementResponse.builder().build(),
            "B1", OperatorImprovementResponse.builder().build()
        );

        assertDoesNotThrow(() -> validator.validate(operatorImprovements, verificationData));
    }

    @Test
    void validate_not_valid_missing_reference() {

        final VirVerificationData verificationData = VirVerificationData.builder()
            .uncorrectedNonConformities(Map.of(
                "A1", UncorrectedItem.builder().build(),
                "A2", UncorrectedItem.builder().build()
            ))
            .priorYearIssues(Map.of(
                "B1", VerifierComment.builder().build()
            ))
            .build();
        final Map<String, OperatorImprovementResponse> operatorImprovements = Map.of(
            "A1", OperatorImprovementResponse.builder().build()
        );

        BusinessException be = assertThrows(BusinessException.class,
            () -> validator.validate(operatorImprovements, verificationData));

        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_VIR);
        assertThat(be.getData()).containsExactly("A2", "B1");
    }

    @Test
    void validate_not_valid_extra_reference() {

        final VirVerificationData verificationData = VirVerificationData.builder()
            .uncorrectedNonConformities(Map.of(
                "A1", UncorrectedItem.builder().build(),
                "A2", UncorrectedItem.builder().build()
            ))
            .priorYearIssues(Map.of(
                "B1", VerifierComment.builder().build()
            ))
            .build();
        final Map<String, OperatorImprovementResponse> operatorImprovements = Map.of(
            "A1", OperatorImprovementResponse.builder().build(),
            "A2", OperatorImprovementResponse.builder().build(),
            "B1", OperatorImprovementResponse.builder().build(),
            "B2", OperatorImprovementResponse.builder().build()
        );

        BusinessException be = assertThrows(BusinessException.class,
            () -> validator.validate(operatorImprovements, verificationData));

        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_VIR);
        assertThat(be.getData()).containsExactly("B2");
    }
}
