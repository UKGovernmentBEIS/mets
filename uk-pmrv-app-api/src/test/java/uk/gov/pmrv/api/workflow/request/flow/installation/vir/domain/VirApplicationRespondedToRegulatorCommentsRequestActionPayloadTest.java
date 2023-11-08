package uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.Year;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;

@ExtendWith(MockitoExtension.class)
class VirApplicationRespondedToRegulatorCommentsRequestActionPayloadTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void VirApplicationRespondedToRegulatorCommentsRequestActionPayload_with_verifierUncorrectedItem_valid() {
        final VirApplicationRespondedToRegulatorCommentsRequestActionPayload actionPayload =
                VirApplicationRespondedToRegulatorCommentsRequestActionPayload.builder()
                        .reportingYear(Year.now())
                        .verifierUncorrectedItem(UncorrectedItem.builder()
                                .explanation("Explanation")
                                .reference("A1")
                                .materialEffect(true)
                                .build())
                        .operatorImprovementResponse(OperatorImprovementResponse.builder()
                                .isAddressed(false)
                                .addressedDescription("Description")
                                .uploadEvidence(false)
                                .build())
                        .regulatorImprovementResponse(RegulatorImprovementResponse.builder()
                                .improvementRequired(true)
                                .improvementDeadline(LocalDate.now())
                                .operatorActions("action to be taken")
                                .build())
                        .operatorImprovementFollowUpResponse(OperatorImprovementFollowUpResponse.builder()
                                .improvementCompleted(false)
                                .reason("Reason")
                                .build())
                        .build();

        final Set<ConstraintViolation<VirApplicationRespondedToRegulatorCommentsRequestActionPayload>> violations =
                validator.validate(actionPayload);

        assertEquals(0, violations.size());
    }

    @Test
    void VirApplicationRespondedToRegulatorCommentsRequestActionPayload_with_verifierComment_valid() {
        final VirApplicationRespondedToRegulatorCommentsRequestActionPayload actionPayload =
                VirApplicationRespondedToRegulatorCommentsRequestActionPayload.builder()
                        .reportingYear(Year.now())
                        .verifierComment(VerifierComment.builder()
                                .reference("A1")
                                .explanation("Explanation")
                                .build())
                        .operatorImprovementResponse(OperatorImprovementResponse.builder()
                                .isAddressed(false)
                                .addressedDescription("Description")
                                .uploadEvidence(false)
                                .build())
                        .regulatorImprovementResponse(RegulatorImprovementResponse.builder()
                                .improvementRequired(true)
                                .improvementDeadline(LocalDate.now())
                                .operatorActions("action to be taken")
                                .build())
                        .operatorImprovementFollowUpResponse(OperatorImprovementFollowUpResponse.builder()
                                .improvementCompleted(false)
                                .reason("Reason")
                                .build())
                        .build();

        final Set<ConstraintViolation<VirApplicationRespondedToRegulatorCommentsRequestActionPayload>> violations =
                validator.validate(actionPayload);

        assertEquals(0, violations.size());
    }

    @Test
    void VirApplicationRespondedToRegulatorCommentsRequestActionPayload_no_verifier_data_not_valid() {
        final VirApplicationRespondedToRegulatorCommentsRequestActionPayload actionPayload =
                VirApplicationRespondedToRegulatorCommentsRequestActionPayload.builder()
                        .reportingYear(Year.now())
                        .operatorImprovementResponse(OperatorImprovementResponse.builder()
                                .isAddressed(false)
                                .addressedDescription("Description")
                                .uploadEvidence(false)
                                .build())
                        .regulatorImprovementResponse(RegulatorImprovementResponse.builder()
                                .improvementRequired(true)
                                .improvementDeadline(LocalDate.now())
                                .operatorActions("action to be taken")
                                .build())
                        .operatorImprovementFollowUpResponse(OperatorImprovementFollowUpResponse.builder()
                                .improvementCompleted(false)
                                .reason("Reason")
                                .build())
                        .build();

        final Set<ConstraintViolation<VirApplicationRespondedToRegulatorCommentsRequestActionPayload>> violations =
                validator.validate(actionPayload);

        assertEquals(1, violations.size());
    }

    @Test
    void VirApplicationRespondedToRegulatorCommentsRequestActionPayload_both_verifier_data_not_valid() {
        final VirApplicationRespondedToRegulatorCommentsRequestActionPayload actionPayload =
                VirApplicationRespondedToRegulatorCommentsRequestActionPayload.builder()
                        .reportingYear(Year.now())
                        .verifierUncorrectedItem(UncorrectedItem.builder()
                                .explanation("Explanation")
                                .reference("A1")
                                .materialEffect(true)
                                .build())
                        .verifierComment(VerifierComment.builder()
                                .reference("A1")
                                .explanation("Explanation")
                                .build())
                        .operatorImprovementResponse(OperatorImprovementResponse.builder()
                                .isAddressed(false)
                                .addressedDescription("Description")
                                .uploadEvidence(false)
                                .build())
                        .regulatorImprovementResponse(RegulatorImprovementResponse.builder()
                                .improvementRequired(true)
                                .improvementDeadline(LocalDate.now())
                                .operatorActions("action to be taken")
                                .build())
                        .operatorImprovementFollowUpResponse(OperatorImprovementFollowUpResponse.builder()
                                .improvementCompleted(false)
                                .reason("Reason")
                                .build())
                        .build();

        final Set<ConstraintViolation<VirApplicationRespondedToRegulatorCommentsRequestActionPayload>> violations =
                validator.validate(actionPayload);

        assertEquals(1, violations.size());
    }
}
