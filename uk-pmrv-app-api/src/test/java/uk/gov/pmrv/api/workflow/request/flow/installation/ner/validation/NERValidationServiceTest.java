package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.reporting.domain.verification.OverallAssessmentType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NER;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERFiles;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationOpinionStatement;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerifiedSatisfactoryOverallVerificationAssessment;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerifiedWithCommentsOverallVerificationAssessment;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERNotVerifiedOverallVerificationAssessment;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERNerDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERNerDataRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewOpinion;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class NERValidationServiceTest {

    @InjectMocks
    private NERValidationService nerValidationService;

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateNerFileName_valid() {
        String[] validFileNames = {
                "NER-00001-1-v1-uploaded by Operator-OPp5.txt",
                "NER-12345-2024-v2-uploaded by Regulator-Test.pdf",
                "NER-45678-2023-v10-uploaded by Operator-Plant1.csv",
                "NER-00099-2022-v99-uploaded by Regulator-Alpha.DOCX",
                "NER-10001-2026-v5-uploaded by Operator-GH12.Jpg",
                "NER-10001-2026-v5-uploaded by Operator-Inst#$.png" // symbols allowed
        };

        for (String fileName : validFileNames) {
            assertDoesNotThrow(
                    () -> nerValidationService.validateNerFileName(fileName),
                    "Expected no exception for valid filename: " + fileName
            );
        }
    }

    @Test
    void validateNerFileName_invalid() {
        String[] invalidFileNames = {
                "XYZNER-00001-2025-v1-uploaded by Operator-OPp5.txt", // wrong prefix
                "NER-123-2025-v1-uploaded by Operator-OPp5.txt",     // < 5 digits
                "NER-123456-2024-v2-uploaded by Regulator-Test.pdf", // > 5 digits
                "NER-00001-2025-v-uploaded by Operator-OPp5.txt",    // missing version number
                "NER-00001-2025-v1-uploaded by User-OPp5.txt",       // invalid uploader
                "NER-00001-2025-v1-uploaded by Operator-LongFileName.txt", // >10 chars
                "NER-00001-2025-v1-uploaded by Operator-OPp5",       // no extension
                "NER-00001-2025-v1-uploaded by Operator-OPp5.ZIP",   // invalid extension
                "NER-00001-2025-v1-uploaded by Operator-VERY_LONG_NAME.pdf" // too long
        };

        for (String fileName : invalidFileNames) {
            BusinessException thrown = assertThrows(
                    BusinessException.class,
                    () -> nerValidationService.validateNerFileName(fileName),
                    "Expected BusinessException for invalid filename: " + fileName
            );

            assertEquals(
                    MetsErrorCode.NER_FILENAME_NOT_VALID,
                    thrown.getErrorCode()
            );
        }
    }

    @Test
    void validateNer_valid() {
        final NER ner = NER.builder()
                .nerFiles(
                        NERFiles.builder()
                                .file(UUID.randomUUID())
                                .build()
                )
                .notes("Some notes")
                .build();

        assertDoesNotThrow(
                () -> nerValidationService.validateNer(ner),
                "Expected no exception for valid NER"
        );
    }

    @Test
    void validateVerificationReport_valid() {
        NERVerificationOpinionStatement opinion = new NERVerificationOpinionStatement();
        opinion.setOpinionStatementFile(UUID.randomUUID());

        NERVerifiedSatisfactoryOverallVerificationAssessment assessment =
                new NERVerifiedSatisfactoryOverallVerificationAssessment();
        assessment.setType(OverallAssessmentType.VERIFIED_AS_SATISFACTORY);

        NERVerificationData data = new NERVerificationData();
        data.setOpinionStatement(opinion);
        data.setOverallAssessment(assessment);

        NERVerificationReport report = new NERVerificationReport();
        report.setVerificationData(data);

        Set<ConstraintViolation<NERVerificationReport>> violations =
                validator.validate(report);

        assertTrue(violations.isEmpty(), "Expected no violations");
    }

    @Test
    void validateVerificationReport_missingOpinionStatementFile() {
        NERVerificationOpinionStatement opinion = new NERVerificationOpinionStatement();
        opinion.setOpinionStatementFile(null); // ❌ invalid

        NERVerifiedSatisfactoryOverallVerificationAssessment assessment =
                new NERVerifiedSatisfactoryOverallVerificationAssessment();
        assessment.setType(OverallAssessmentType.VERIFIED_AS_SATISFACTORY);

        NERVerificationData data = new NERVerificationData();
        data.setOpinionStatement(opinion);
        data.setOverallAssessment(assessment);

        NERVerificationReport report = new NERVerificationReport();
        report.setVerificationData(data);

        Set<ConstraintViolation<NERVerificationReport>> violations =
                validator.validate(report);

        assertFalse(violations.isEmpty(), "Expected violations");
    }

    @Test
    void validateVerificationReport_verifiedWithCommentsWithoutReasons() {
        NERVerificationOpinionStatement opinion = new NERVerificationOpinionStatement();
        opinion.setOpinionStatementFile(UUID.randomUUID());

        NERVerifiedWithCommentsOverallVerificationAssessment assessment =
                new NERVerifiedWithCommentsOverallVerificationAssessment();
        assessment.setType(OverallAssessmentType.VERIFIED_WITH_COMMENTS);
        assessment.setReasons(null); // ❌ invalid

        NERVerificationData data = new NERVerificationData();
        data.setOpinionStatement(opinion);
        data.setOverallAssessment(assessment);

        NERVerificationReport report = new NERVerificationReport();
        report.setVerificationData(data);

        Set<ConstraintViolation<NERVerificationReport>> violations =
                validator.validate(report);

        assertFalse(violations.isEmpty(), "Expected violations");
    }

    @Test
    void validateVerificationReport_notVerifiedWithoutReasons() {
        NERVerificationOpinionStatement opinion = new NERVerificationOpinionStatement();
        opinion.setOpinionStatementFile(UUID.randomUUID());

        NERNotVerifiedOverallVerificationAssessment assessment =
                new NERNotVerifiedOverallVerificationAssessment();
        assessment.setType(OverallAssessmentType.NOT_VERIFIED);
        assessment.setReasons(""); // ❌ blank

        NERVerificationData data = new NERVerificationData();
        data.setOpinionStatement(opinion);
        data.setOverallAssessment(assessment);

        NERVerificationReport report = new NERVerificationReport();
        report.setVerificationData(data);

        Set<ConstraintViolation<NERVerificationReport>> violations =
                validator.validate(report);

        assertFalse(violations.isEmpty(), "Expected violations");
    }

    @Test
    void validateReturnForAmends_whenAmendsNeededExists_shouldPass() {
        // given
        NERNerDataRegulatorReviewDecision decision =
                NERNerDataRegulatorReviewDecision.builder()
                        .type(NERNerDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .build();

        NERApplicationRegulatorReviewSubmitRequestTaskPayload payload =
                NERApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .regulatorReviewGroupDecisions(
                                Map.of(NERReviewGroup.NER, decision)
                        )
                        .build();

        // when / then
        assertDoesNotThrow(() -> nerValidationService.validateReturnForAmends(payload));
    }

    @Test
    void validateReturnForAmends_whenAmendsNeededDoesNotExist_shouldThrowBusinessException() {
        // given
        NERNerDataRegulatorReviewDecision decision =
                NERNerDataRegulatorReviewDecision.builder()
                        .type(NERNerDataRegulatorReviewDecisionType.ACCEPTED)
                        .build();

        NERApplicationRegulatorReviewSubmitRequestTaskPayload payload =
                NERApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .regulatorReviewGroupDecisions(
                                Map.of(NERReviewGroup.NER, decision)
                        )
                        .build();

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> nerValidationService.validateReturnForAmends(payload)
        );

        // then
        assertEquals(MetsErrorCode.INVALID_NER_REVIEW, exception.getErrorCode());
    }
    @Test
    void validateAmendsVerification_whenVerificationNotRequired_shouldNotThrow() {
        // given
        NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails details =
                NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                        .verificationRequired(false)
                        .build();

        NERNerDataRegulatorReviewDecision decision =
                NERNerDataRegulatorReviewDecision.builder()
                        .type(NERNerDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .details(details)
                        .build();

        NerRequestPayload requestPayload = NerRequestPayload.builder()
                .regulatorReviewGroupDecisions(Map.of(NERReviewGroup.NER, decision))
                .build();

        NERApplicationAmendsSubmitRequestTaskPayload taskPayload =
                NERApplicationAmendsSubmitRequestTaskPayload.builder()
                        .verificationPerformed(false)
                        .build();

        // when / then
        assertDoesNotThrow(() ->
                nerValidationService.validateAmendsVerification(requestPayload, taskPayload));
    }

    @Test
    void validateAmendsVerification_whenVerificationRequiredAndNotPerformed_throwBusinessException() {
        // given
        NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails details =
                NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                        .verificationRequired(true)
                        .build();

        NERNerDataRegulatorReviewDecision decision =
                NERNerDataRegulatorReviewDecision.builder()
                        .type(NERNerDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .details(details)
                        .build();

        NerRequestPayload requestPayload = NerRequestPayload.builder()
                .regulatorReviewGroupDecisions(Map.of(NERReviewGroup.NER, decision))
                .build();

        NERApplicationAmendsSubmitRequestTaskPayload taskPayload =
                NERApplicationAmendsSubmitRequestTaskPayload.builder()
                        .verificationPerformed(false)
                        .build();

        // when / then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> nerValidationService.validateAmendsVerification(requestPayload, taskPayload));

        assertEquals(
                MetsErrorCode.ALR_MUST_UNDERGO_VERIFICATION,
                exception.getErrorCode());
    }

    @Test
    void isVerificationRequiredFromReviewGroupDecisions_returnTrue() {
        // given
        NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails details =
                NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                        .verificationRequired(true)
                        .build();

        NERNerDataRegulatorReviewDecision decision =
                NERNerDataRegulatorReviewDecision.builder()
                        .type(NERNerDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .details(details)
                        .build();

        // when
        Boolean result = nerValidationService.isVerificationRequiredFromReviewGroupDecisions(
                Map.of(NERReviewGroup.NER, decision));

        // then
        assertTrue(result);
    }

    @Test
    void isVerificationRequiredFromReviewGroupDecisions_whenNoNerDecision_throwBusinessException() {
        // when / then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> nerValidationService.isVerificationRequiredFromReviewGroupDecisions(Map.of()));

        assertEquals(MetsErrorCode.INVALID_NER_REVIEW, exception.getErrorCode());
    }

    @Test
    void isVerificationRequiredFromReviewGroupDecisions_whenWrongDecisionType_throwBusinessException() {
        // given
        NERNerDataRegulatorReviewDecision decision =
                NERNerDataRegulatorReviewDecision.builder()
                        .type(NERNerDataRegulatorReviewDecisionType.ACCEPTED)
                        .build();

        // when / then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> nerValidationService.isVerificationRequiredFromReviewGroupDecisions(
                        Map.of(NERReviewGroup.NER, decision)));

        assertEquals(MetsErrorCode.INVALID_NER_REVIEW, exception.getErrorCode());
    }

    @Test
    void validateRegulatorReviewOutcome_whenOpinionMatches_thenNoException() {
        NERApplicationRegulatorReviewOutcome outcome =
                NERApplicationRegulatorReviewOutcome.builder()
                        .opinion(NERReviewOpinion.PROCEED_TO_AUTHORITY)
                        .build();

        NERApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                NERApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .regulatorReviewOutcome(outcome)
                        .build();

        assertDoesNotThrow(() ->
                nerValidationService.validateRegulatorReviewOutcome(
                        taskPayload,
                        NERReviewOpinion.PROCEED_TO_AUTHORITY
                )
        );
    }

    @Test
    void validateRegulatorReviewOutcome_whenOpinionDoesNotMatch_thenThrowBusinessException() {
        NERApplicationRegulatorReviewOutcome outcome =
                NERApplicationRegulatorReviewOutcome.builder()
                        .opinion(NERReviewOpinion.WITHDRAW)
                        .build();

        NERApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                NERApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .regulatorReviewOutcome(outcome)
                        .build();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> nerValidationService.validateRegulatorReviewOutcome(
                        taskPayload,
                        NERReviewOpinion.PROCEED_TO_AUTHORITY
                )
        );

        assertEquals(MetsErrorCode.INVALID_NER_REVIEW, exception.getErrorCode());
    }
}
