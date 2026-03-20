package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ContinueApplicationForFreeAllocationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2GuardQuestions;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RegulatorReviewFreeAllocationOpinion;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RegulatorReviewCovidAdjustmentsOpinion;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RegulatorReviewInstallationSectorOpinion;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RegulatorReviewCbamSplitOpinion;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationReportDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationReportDataRegulatorReviewDecisionType;

import java.util.Map;
import java.util.stream.Stream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BDRS2ValidationServiceTest {

    @InjectMocks
    private BDRS2ValidationService bdrs2ValidationService;

    @Test
    void validateBDRS2FileName_valid() {
        String[] validFileNames = {
                "BDRS2-00001-2025-v1-uploaded by Operator-OPp5.txt",
                "BDRS2-12345-2024-v2-uploaded by Regulator-Test.pdf",
                "BDRS2-45678-2023-v10-uploaded by Operator-Plant1.csv",
                "BDRS2-00099-2022-v99-uploaded by Regulator-Alpha.DOCX",
                "BDRS2-10001-2026-v5-uploaded by Operator-GH12.Jpg",
                "BDRS2-10001-2026-v5-uploaded by Operator-Inst#$.png" // symbols allowed (.{1,10})
        };

        for (String fileName : validFileNames) {
            assertDoesNotThrow(
                    () -> bdrs2ValidationService.validateBDRS2FileName(fileName),
                    "Expected no exception for valid filename: " + fileName
            );
        }
    }

    @Test
    void validateBDRS2FileName_invalid() {
        String[] invalidFileNames = {
                "XYZBDRS2-00001-2025-v1-uploaded by Operator-OPp5.txt", // Doesn't start with BDRS2-
                "BDRS2-123-2025-v1-uploaded by Operator-OPp5.txt",     // Account ID < 5 digits
                "BDRS2-123456-2024-v2-uploaded by Regulator-Test.pdf", // Account ID > 5 digits
                "BDRS2-00001-2025-v-uploaded by Operator-OPp5.txt",    // Missing version number
                "BDRS2-00001-2025-v1-uploaded by User-OPp5.txt",       // Invalid uploader
                "BDRS2-00001-2025-v1-uploaded by Operator-LongFileName.txt", // >10 chars
                "BDRS2-00001-2025-v1-uploaded by Operator-OPp5",       // Missing extension
                "BDRS2-00001-2025-v1-uploaded by Operator-OPp5.ZIP",   // Extension not allowed
                "BDRS2-00001-2025-v1-uploaded by Operator-VERY_LONG_NAME.pdf" // Too long
        };

        for (String fileName : invalidFileNames) {
            BusinessException thrown = assertThrows(
                    BusinessException.class,
                    () -> bdrs2ValidationService.validateBDRS2FileName(fileName),
                    "Expected BusinessException for invalid filename: " + fileName
            );

            Assertions.assertEquals(
                    MetsErrorCode.BDRS2_FILENAME_NOT_VALID,
                    thrown.getErrorCode()
            );
        }
    }

    @Test
    void validateBDRS2_valid() {
        final BDRS2GuardQuestions guardQuestions = BDRS2GuardQuestions.builder()
                .continueApplicationForFreeAllocationType(BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_MAIN_SCHEME_PARTICIPANT)
                .covidAdjustments(Boolean.FALSE)
                .inEiteSector(Boolean.TRUE)
                .requiresAdditionalSubInstallationSplitsForCbam(Boolean.FALSE)
                .build();

        final BDRS2 bdrs2 = BDRS2.builder()
                .bdrs2guardQuestions(guardQuestions)
                .build();

        assertDoesNotThrow(
                () -> bdrs2ValidationService.validateBDRS2(bdrs2),
                "Expected no exception for valid BDRS2"
        );
    }

    @Test
    void validateVerificationReport_valid() {
        final BDRS2VerificationReport verificationReport = BDRS2VerificationReport.builder()
                .build();

        assertDoesNotThrow(
                () -> bdrs2ValidationService.validateVerificationReport(verificationReport),
                "Expected no exception for valid verification report"
        );
    }

    @Test
    void validateAmendsVerification_cbamYes_verificationRequired_verificationNotPerformed_throwsException() {
        Map<BDRS2ReviewGroup, BDRS2ReviewDecision> reviewGroupDecisions = Map.of(
                BDRS2ReviewGroup.BDRS2,
                BDRS2Bdrs2DataRegulatorReviewDecision.builder()
                        .type(BDRS2Bdrs2DataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(BDRS2ReviewDataType.BDRS2_DATA)
                        .details(BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                                .verificationRequired(true).build())
                        .build(),
                BDRS2ReviewGroup.OVERALL_DECISION,
                BDRS2VerificationReportDataRegulatorReviewDecision.builder()
                        .type(BDRS2VerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRS2ReviewDataType.VERIFICATION_REPORT_DATA)
                        .build(),
                BDRS2ReviewGroup.OPINION_STATEMENT,
                BDRS2VerificationReportDataRegulatorReviewDecision.builder()
                        .type(BDRS2VerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRS2ReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()
        );

        BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder()
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        BDRS2ApplicationAmendsSubmitRequestTaskPayload taskPayload = BDRS2ApplicationAmendsSubmitRequestTaskPayload.builder()
                .verificationPerformed(false)
                .bdrs2(BDRS2.builder()
                        .bdrs2guardQuestions(BDRS2GuardQuestions.builder()
                                .requiresAdditionalSubInstallationSplitsForCbam(true)
                                .build())
                        .build())
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        BusinessException be = assertThrows(BusinessException.class, () ->
                bdrs2ValidationService.validateAmendsVerification(requestPayload, taskPayload));
        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.BDRS2_MUST_UNDERGO_VERIFICATION);
    }

    @ParameterizedTest
    @MethodSource("provideValidateAmendsVerificationDoesNotThrowParams")
    void validateAmendsVerification_doesNotThrow(Boolean verificationRequired, Boolean requiresCbam, boolean verificationPerformed) {
        Map<BDRS2ReviewGroup, BDRS2ReviewDecision> reviewGroupDecisions = Map.of(
                BDRS2ReviewGroup.BDRS2,
                BDRS2Bdrs2DataRegulatorReviewDecision.builder()
                        .type(BDRS2Bdrs2DataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(BDRS2ReviewDataType.BDRS2_DATA)
                        .details(BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                                .verificationRequired(verificationRequired).build())
                        .build(),
                BDRS2ReviewGroup.OVERALL_DECISION,
                BDRS2VerificationReportDataRegulatorReviewDecision.builder()
                        .type(BDRS2VerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRS2ReviewDataType.VERIFICATION_REPORT_DATA)
                        .build(),
                BDRS2ReviewGroup.OPINION_STATEMENT,
                BDRS2VerificationReportDataRegulatorReviewDecision.builder()
                        .type(BDRS2VerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRS2ReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()
        );

        BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder()
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        BDRS2ApplicationAmendsSubmitRequestTaskPayload taskPayload = BDRS2ApplicationAmendsSubmitRequestTaskPayload.builder()
                .verificationPerformed(verificationPerformed)
                .bdrs2(BDRS2.builder()
                        .bdrs2guardQuestions(BDRS2GuardQuestions.builder()
                                .requiresAdditionalSubInstallationSplitsForCbam(requiresCbam)
                                .build())
                        .build())
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        assertThatCode(() -> bdrs2ValidationService.validateAmendsVerification(requestPayload, taskPayload))
                .doesNotThrowAnyException();
    }

    private static Stream<Arguments> provideValidateAmendsVerificationDoesNotThrowParams() {
        return Stream.of(
                Arguments.of(true, true, true),   // cbamYes_verificationRequired_verificationPerformed
                Arguments.of(false, true, false), // cbamYes_verificationNotRequired_verificationNotPerformed
                Arguments.of(true, false, false)  // cbamNo_verificationNotPerformed
        );
    }

    @Test
    void validateAmendsVerification_cbamNull_verificationNotPerformed_doesNotThrow() {
        Map<BDRS2ReviewGroup, BDRS2ReviewDecision> reviewGroupDecisions = Map.of(
                BDRS2ReviewGroup.BDRS2,
                BDRS2Bdrs2DataRegulatorReviewDecision.builder()
                        .type(BDRS2Bdrs2DataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(BDRS2ReviewDataType.BDRS2_DATA)
                        .details(BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                                .verificationRequired(true).build())
                        .build()
        );

        BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder()
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        BDRS2ApplicationAmendsSubmitRequestTaskPayload taskPayload = BDRS2ApplicationAmendsSubmitRequestTaskPayload.builder()
                .verificationPerformed(false)
                .bdrs2(BDRS2.builder()
                        .bdrs2guardQuestions(BDRS2GuardQuestions.builder()
                                .requiresAdditionalSubInstallationSplitsForCbam(null)
                                .build())
                        .build())
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        assertThatCode(() -> bdrs2ValidationService.validateAmendsVerification(requestPayload, taskPayload))
                .doesNotThrowAnyException();
    }

    @Test
    void validateAmendsVerification_cbamYes_verificationRequiredNull_verificationNotPerformed_doesNotThrow() {
        Map<BDRS2ReviewGroup, BDRS2ReviewDecision> reviewGroupDecisions = Map.of(
                BDRS2ReviewGroup.BDRS2,
                BDRS2Bdrs2DataRegulatorReviewDecision.builder()
                        .type(BDRS2Bdrs2DataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(BDRS2ReviewDataType.BDRS2_DATA)
                        .details(BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                                .verificationRequired(null).build())
                        .build()
        );

        BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder()
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        BDRS2ApplicationAmendsSubmitRequestTaskPayload taskPayload = BDRS2ApplicationAmendsSubmitRequestTaskPayload.builder()
                .verificationPerformed(false)
                .bdrs2(BDRS2.builder()
                        .bdrs2guardQuestions(BDRS2GuardQuestions.builder()
                                .requiresAdditionalSubInstallationSplitsForCbam(true)
                                .build())
                        .build())
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        assertThatCode(() -> bdrs2ValidationService.validateAmendsVerification(requestPayload, taskPayload))
                .doesNotThrowAnyException();
    }

    @Test
    void validate_noFa_noSections_shouldPass() {
        var payload = payload(
                BDRS2ContinueApplicationForFreeAllocationType.WITHDRAW,
                null,
                null,
                null,
                null
        );

        assertDoesNotThrow(() ->
                bdrs2ValidationService.validateRegulatorReviewOutcome(payload)
        );
    }

    @Test
    void validate_noFa_withCovid_shouldThrow() {
        var payload = payload(
                BDRS2ContinueApplicationForFreeAllocationType.WITHDRAW,
                null,
                BDRS2RegulatorReviewCovidAdjustmentsOpinion.SENT_TO_AUTHORITY,
                null,
                null
        );

        assertThrows(BusinessException.class,
                () -> bdrs2ValidationService.validateRegulatorReviewOutcome(payload));
    }

    @Test
    void validate_fa_missingCovid_shouldThrow() {
        var payload = payload(
                BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_HSE,
                BDRS2RegulatorReviewInstallationSectorOpinion.CBAM_DOES_NOT_APPLY,
                null,
                null,
                null
        );

        assertThrows(BusinessException.class,
                () -> bdrs2ValidationService.validateRegulatorReviewOutcome(payload));
    }

    @Test
    void validate_fa_missingInstallation_shouldThrow() {
        var payload = payload(
                BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_HSE,
                null,
                BDRS2RegulatorReviewCovidAdjustmentsOpinion.SENT_TO_AUTHORITY,
                null,
                null
        );

        assertThrows(BusinessException.class,
                () -> bdrs2ValidationService.validateRegulatorReviewOutcome(payload));
    }

    @Test
    void validate_cbamVisible_missingOpinion_shouldThrow() {
        var payload = payload(
                BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_HSE,
                BDRS2RegulatorReviewInstallationSectorOpinion.IN_SCOPE_OF_CBAM,
                BDRS2RegulatorReviewCovidAdjustmentsOpinion.SENT_TO_AUTHORITY,
                null,
                null,
                true
        );

        assertThrows(BusinessException.class,
                () -> bdrs2ValidationService.validateRegulatorReviewOutcome(payload));
    }

    @Test
    void validate_cbamNotVisible_butProvided_shouldThrow() {
        var payload = payload(
                BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_HSE,
                BDRS2RegulatorReviewInstallationSectorOpinion.CBAM_DOES_NOT_APPLY,
                BDRS2RegulatorReviewCovidAdjustmentsOpinion.SENT_TO_AUTHORITY,
                BDRS2RegulatorReviewCbamSplitOpinion.SENT_TO_AUTHORITY,
                null
        );

        assertThrows(BusinessException.class,
                () -> bdrs2ValidationService.validateRegulatorReviewOutcome(payload));
    }

    @Test
    void validate_noFa_withFile_shouldThrow() {
        var payload = payload(
                BDRS2ContinueApplicationForFreeAllocationType.WITHDRAW,
                null,
                null,
                null,
                UUID.randomUUID()
        );

        assertThrows(BusinessException.class,
                () -> bdrs2ValidationService.validateRegulatorReviewOutcome(payload));
    }

    @Test
    void validate_fullValidScenario_shouldPass() {
        var payload = payload(
                BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_HSE,
                BDRS2RegulatorReviewInstallationSectorOpinion.IN_SCOPE_OF_CBAM,
                BDRS2RegulatorReviewCovidAdjustmentsOpinion.SENT_TO_AUTHORITY,
                BDRS2RegulatorReviewCbamSplitOpinion.SENT_TO_AUTHORITY,
                UUID.randomUUID(),
                true
        );

        assertDoesNotThrow(() ->
                bdrs2ValidationService.validateRegulatorReviewOutcome(payload)
        );
    }

    private BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload payload(
            BDRS2ContinueApplicationForFreeAllocationType continueType,
            BDRS2RegulatorReviewInstallationSectorOpinion installationOpinion,
            BDRS2RegulatorReviewCovidAdjustmentsOpinion covidOpinion,
            BDRS2RegulatorReviewCbamSplitOpinion cbamOpinion,
            UUID file) {

        BDRS2GuardQuestions guard = BDRS2GuardQuestions.builder()
                .continueApplicationForFreeAllocationType(continueType)
                .build();

        BDRS2RegulatorReviewFreeAllocationOpinion faOpinion =
                BDRS2ContinueApplicationForFreeAllocationType.WITHDRAW.equals(continueType)
                        ? BDRS2RegulatorReviewFreeAllocationOpinion.WITHDRAWN
                        : BDRS2RegulatorReviewFreeAllocationOpinion.SENT_TO_AUTHORITY;

        BDRS2ApplicationRegulatorReviewOutcome outcome =
                BDRS2ApplicationRegulatorReviewOutcome.builder()
                        .freeAllocationOpinion(faOpinion)
                        .installationSectorOpinion(installationOpinion)
                        .covidAdjustmentsOpinion(covidOpinion)
                        .cbamSplitOpinion(cbamOpinion)
                        .file(file)
                        .build();

        BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload payload =
                new BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload();

        payload.setBdrs2(BDRS2.builder()
                .bdrs2guardQuestions(guard)
                .build());

        payload.setRegulatorReviewOutcome(outcome);

        return payload;
    }

    private BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload payload(
            BDRS2ContinueApplicationForFreeAllocationType continueType,
            BDRS2RegulatorReviewInstallationSectorOpinion installationOpinion,
            BDRS2RegulatorReviewCovidAdjustmentsOpinion covidOpinion,
            BDRS2RegulatorReviewCbamSplitOpinion cbamOpinion,
            UUID file,
            Boolean inEiteSector) {

        BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload payload = payload(continueType,installationOpinion,covidOpinion,cbamOpinion, file);
        payload.getBdrs2().getBdrs2guardQuestions().setInEiteSector(inEiteSector);

        return payload;
    }
}
