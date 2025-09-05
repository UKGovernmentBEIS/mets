package uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.allowance.validation.AllowanceAllocationValidator;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAlrDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAlrDataRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationReportDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationReportDataRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALR;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRPreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.ArticleReasonGroupType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.ArticleReasonItemType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.enums.DoalDeterminationType;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ALRValidationServiceTest {

    @InjectMocks
    private ALRValidationService alrValidationService;

    @Mock
    private AllowanceAllocationValidator allowanceAllocationValidator;

    @Test
    public void validateALRFileName_valid() {
        String[] validFileNames = {
                "ALR00017-2025-v1-uploaded by Operator-OPp5.txt",
                "ALR12345-2024-v2-uploaded by Regulator-Test.pdf",
                "ALR45678-2023-v10-uploaded by Operator-Plant1.csv",
                "ALR00099-2022-v99-uploaded by Regulator-Alpha.DOCX",
                "ALR10001-2026-v5-uploaded by Operator-GH12.Jpg",
                "ALR10001-2026-v5-uploaded by Operator-Inst#$.Jpg" // symbols allowed if using .{1,10}
        };

        for (String fileName : validFileNames) {
            assertDoesNotThrow(() -> alrValidationService.validateALRFileName(fileName),
                    "Expected no exception for valid filename: " + fileName);
        }
    }

    @Test
    public void validateALRFileName_invalid() {
        String[] invalidFileNames = {
                "XYZALR00017-2025-v1-uploaded by Operator-OPp5.txt", // ❌ Doesn't start with ALR
                "ALR123-2025-v1-uploaded by Operator-OPp5.txt",       // ❌ Account ID < 5 digits
                "ALR123456-2024-v2-uploaded by Regulator-Test.Pdf",   // ❌ Account ID > 5 digits
                "ALR00017-2025-v-uploaded by Operator-OPp5.txt",      // ❌ Missing version number
                "ALR00017-2025-v1-uploaded by User-OPp5.txt",         // ❌ Invalid uploader
                "ALR00017-2025-v1-uploaded by Operator-LongFileName.txt", // ❌ Installation name > 10 characters
                "ALR00017-2025-v1-uploaded by Operator-OPp5",          // ❌ Missing file extension
                "ALR00017-2025-v1-uploaded by Operator-OPp5.ZIP",      // ❌ Invalid extension (not in whitelist)
                "ALR00017-2025-v1-uploaded by Operator-VERY_LONG_NAME.pdf" // ❌ Installation name too long
        };

        for (String fileName : invalidFileNames) {
            BusinessException thrown = assertThrows(BusinessException.class,
                    () -> alrValidationService.validateALRFileName(fileName),
                    "Expected BusinessException for invalid filename: " + fileName);
            Assertions.assertEquals(MetsErrorCode.ALR_FILENAME_NOT_VALID, thrown.getErrorCode());
        }
    }

    @Test
    void validateReturnForAmends_amendDoesNotExist_throwBusinessException() {
        Map<ALRReviewGroup, ALRReviewDecision> reviewGroupDecisions = Map.of(
                ALRReviewGroup.ALR,
                ALRAlrDataRegulatorReviewDecision.builder()
                        .type(ALRAlrDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(ALRReviewDataType.ALR_DATA)
                        .details(ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder().verificationRequired(true).build())
                        .build(),
                ALRReviewGroup.OVERALL_DECISION,
                ALRVerificationReportDataRegulatorReviewDecision.builder()
                        .type(ALRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(ALRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()
        );

        ALRApplicationRegulatorReviewSubmitRequestTaskPayload payload = ALRApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> alrValidationService.validateReturnForAmends(payload));
        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_ALR_REVIEW);
    }

    @Test
    void validateReturnForAmends_missingALRReviewDecisionEntry_throwBusinessException() {
        Map<ALRReviewGroup, ALRReviewDecision> reviewGroupDecisions = Map.of(
                ALRReviewGroup.OVERALL_DECISION,
                ALRVerificationReportDataRegulatorReviewDecision.builder()
                        .type(ALRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(ALRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()
        );

        ALRApplicationRegulatorReviewSubmitRequestTaskPayload payload = ALRApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> alrValidationService.validateReturnForAmends(payload));
        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_ALR_REVIEW);
    }

    @Test
    void validateReturnForAmends_verificationRequiredIsNull_doNotThrowException() {
        Map<ALRReviewGroup, ALRReviewDecision> reviewGroupDecisions = Map.of(
                ALRReviewGroup.ALR,
                ALRAlrDataRegulatorReviewDecision.builder()
                        .type(ALRAlrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(ALRReviewDataType.ALR_DATA)
                        .details(ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder().verificationRequired(null).build())
                        .build()
        );

        ALRApplicationRegulatorReviewSubmitRequestTaskPayload payload = ALRApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                .alr(ALR.builder().build())
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        assertThatCode(() -> alrValidationService.validateReturnForAmends(payload)).doesNotThrowAnyException();
    }

    @Test
    void validateReturnForAmends_allPreconditionsSatisfied_doNotThrowException() {
        Map<ALRReviewGroup, ALRReviewDecision> reviewGroupDecisions = Map.of(
                ALRReviewGroup.ALR,
                ALRAlrDataRegulatorReviewDecision.builder()
                        .type(ALRAlrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(ALRReviewDataType.ALR_DATA)
                        .details(ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder().verificationRequired(true).build())
                        .build(),
                ALRReviewGroup.OVERALL_DECISION,
                ALRVerificationReportDataRegulatorReviewDecision.builder()
                        .type(ALRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(ALRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()
        );

        ALRApplicationRegulatorReviewSubmitRequestTaskPayload payload = ALRApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                .alr(ALR.builder().build())
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        assertThatCode(() -> alrValidationService.validateReturnForAmends(payload)).doesNotThrowAnyException();
    }
    @Test
    void validateRegulatorSubmitTaskPayload_validInput_shouldPass() {
        // Given
        ALRApplicationRegulatorReviewSubmitRequestTaskPayload payload = buildPayload(
                DoalDeterminationType.PROCEED_TO_AUTHORITY,
                ArticleReasonGroupType.ARTICLE_34H_REASONS,
                Set.of(ArticleReasonItemType.ERROR_IN_NEW_ENTRANT_DATA_REPORT),
                true
        );

        when(allowanceAllocationValidator.isValid(anySet())).thenReturn(true);

        // When / Then
        assertDoesNotThrow(() -> alrValidationService.validateRegulatorSubmitTaskPayload(payload));
    }

    @Test
    void validateRegulatorSubmitTaskPayload_invalidArticleReasons_shouldThrow() {
        // Given
        ALRApplicationRegulatorReviewSubmitRequestTaskPayload payload = buildPayload(
                DoalDeterminationType.PROCEED_TO_AUTHORITY,
                ArticleReasonGroupType.ARTICLE_6A_REASONS,
                Set.of(ArticleReasonItemType.ERROR_IN_BASELINE_DATA_REPORT),
                true
        );


        // When / Then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> alrValidationService.validateRegulatorSubmitTaskPayload(payload));

        assertEquals(MetsErrorCode.INVALID_ALR_ARTICLE_REASONS, ex.getErrorCode());
    }

    @Test
    void validateRegulatorSubmitTaskPayload_invalidAllocations_shouldThrow() {
        // Given
        ALRApplicationRegulatorReviewSubmitRequestTaskPayload payload = buildPayload(
                DoalDeterminationType.PROCEED_TO_AUTHORITY,
                ArticleReasonGroupType.ARTICLE_6A_REASONS,
                Set.of(ArticleReasonItemType.ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5),
                true
        );

        when(allowanceAllocationValidator.isValid(any())).thenReturn(false);

        // When / Then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> alrValidationService.validateRegulatorSubmitTaskPayload(payload));

        assertEquals(MetsErrorCode.INVALID_ALR_PRELIMINARY_ALLOCATIONS, ex.getErrorCode());
    }

    private ALRApplicationRegulatorReviewSubmitRequestTaskPayload buildPayload(
            DoalDeterminationType type,
            ArticleReasonGroupType groupType,
            Set<ArticleReasonItemType> itemTypes,
            boolean allocationPresent
    ) {
        DoalProceedToAuthorityDetermination determination = new DoalProceedToAuthorityDetermination();
        determination.setType(type);
        determination.setArticleReasonGroupType(groupType);
        determination.setArticleReasonItems(itemTypes);
        determination.setHasWithholdingOfAllowances(false);
        determination.setNeedsOfficialNotice(false);

        ALRApplicationRegulatorReviewOutcome outcome = new ALRApplicationRegulatorReviewOutcome();
        outcome.setDetermination(determination);

        if (allocationPresent) {
            ALRPreliminaryAllocation mockAllocation = Mockito.mock(ALRPreliminaryAllocation.class);
            outcome.setAllocations(Set.of(mockAllocation));
        } else {
            outcome.setAllocations(Collections.emptySet());
        }

        ALRApplicationRegulatorReviewSubmitRequestTaskPayload payload = new ALRApplicationRegulatorReviewSubmitRequestTaskPayload();
        payload.setRegulatorReviewOutcome(outcome);

        return payload;
    }

    @Test
    void validateAmendsVerification_verificationRequiredAndNotPerformed_shouldThrow() {
        // Given
        Map<ALRReviewGroup, ALRReviewDecision> decisions = Map.of(
                ALRReviewGroup.ALR,
                ALRAlrDataRegulatorReviewDecision.builder()
                        .type(ALRAlrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(ALRReviewDataType.ALR_DATA)
                        .details(ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                                .verificationRequired(true)
                                .build())
                        .build()
        );

        ALRRequestPayload requestPayload = ALRRequestPayload.builder()
                .regulatorReviewGroupDecisions(decisions)
                .build();

        ALRApplicationAmendsSubmitRequestTaskPayload taskPayload = ALRApplicationAmendsSubmitRequestTaskPayload.builder()
                .verificationPerformed(false)
                .build();

        // When / Then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> alrValidationService.validateAmendsVerification(requestPayload, taskPayload));

        assertEquals(MetsErrorCode.ALR_MUST_UNDERGO_VERIFICATION, ex.getErrorCode());
    }

    @Test
    void validateAmendsVerification_verificationRequiredAndPerformed_shouldNotThrow() {
        // Given
        Map<ALRReviewGroup, ALRReviewDecision> decisions = Map.of(
                ALRReviewGroup.ALR,
                ALRAlrDataRegulatorReviewDecision.builder()
                        .type(ALRAlrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(ALRReviewDataType.ALR_DATA)
                        .details(ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                                .verificationRequired(true)
                                .build())
                        .build()
        );

        ALRRequestPayload requestPayload = ALRRequestPayload.builder()
                .regulatorReviewGroupDecisions(decisions)
                .build();

        ALRApplicationAmendsSubmitRequestTaskPayload taskPayload = ALRApplicationAmendsSubmitRequestTaskPayload.builder()
                .verificationPerformed(true)
                .build();

        // When / Then
        assertDoesNotThrow(() -> alrValidationService.validateAmendsVerification(requestPayload, taskPayload));
    }

    @Test
    void validateAmendsVerification_verificationNotRequired_shouldNotThrow() {
        // Given
        Map<ALRReviewGroup, ALRReviewDecision> decisions = Map.of(
                ALRReviewGroup.ALR,
                ALRAlrDataRegulatorReviewDecision.builder()
                        .type(ALRAlrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(ALRReviewDataType.ALR_DATA)
                        .details(ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                                .verificationRequired(false)
                                .build())
                        .build()
        );

        ALRRequestPayload requestPayload = ALRRequestPayload.builder()
                .regulatorReviewGroupDecisions(decisions)
                .build();

        ALRApplicationAmendsSubmitRequestTaskPayload taskPayload = ALRApplicationAmendsSubmitRequestTaskPayload.builder()
                .verificationPerformed(false) // doesn't matter
                .build();

        // When / Then
        assertDoesNotThrow(() -> alrValidationService.validateAmendsVerification(requestPayload, taskPayload));
    }

    @Test
    void isVerificationRequiredFromReviewGroupDecisions_validInput_returnsTrue() {
        // Given
        Map<ALRReviewGroup, ALRReviewDecision> decisions = Map.of(
                ALRReviewGroup.ALR,
                ALRAlrDataRegulatorReviewDecision.builder()
                        .type(ALRAlrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(ALRReviewDataType.ALR_DATA)
                        .details(ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                                .verificationRequired(true)
                                .build())
                        .build()
        );

        // When
        Boolean result = alrValidationService.isVerificationRequiredFromReviewGroupDecisions(decisions);

        // Then
        assertTrue(result);
    }

    @Test
    void isVerificationRequiredFromReviewGroupDecisions_verificationNotRequired_returnsFalse() {
        // Given
        Map<ALRReviewGroup, ALRReviewDecision> decisions = Map.of(
                ALRReviewGroup.ALR,
                ALRAlrDataRegulatorReviewDecision.builder()
                        .type(ALRAlrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(ALRReviewDataType.ALR_DATA)
                        .details(ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                                .verificationRequired(false)
                                .build())
                        .build()
        );

        // When
        Boolean result = alrValidationService.isVerificationRequiredFromReviewGroupDecisions(decisions);

        // Then
        assertFalse(result);
    }

    @Test
    void isVerificationRequiredFromReviewGroupDecisions_missingALRGroup_shouldThrow() {
        // Given
        Map<ALRReviewGroup, ALRReviewDecision> decisions = Map.of(); // empty

        // When / Then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> alrValidationService.isVerificationRequiredFromReviewGroupDecisions(decisions));
        Assertions.assertEquals(MetsErrorCode.INVALID_ALR_REVIEW, ex.getErrorCode());
    }

    @Test
    void isVerificationRequiredFromReviewGroupDecisions_wrongReviewType_shouldThrow() {
        // Given
        Map<ALRReviewGroup, ALRReviewDecision> decisions = Map.of(
                ALRReviewGroup.ALR,
                ALRAlrDataRegulatorReviewDecision.builder()
                        .type(ALRAlrDataRegulatorReviewDecisionType.ACCEPTED) // not AMENDS_NEEDED
                        .reviewDataType(ALRReviewDataType.ALR_DATA)
                        .details(ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                                .verificationRequired(true)
                                .build())
                        .build()
        );

        // When / Then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> alrValidationService.isVerificationRequiredFromReviewGroupDecisions(decisions));
        Assertions.assertEquals(MetsErrorCode.INVALID_ALR_REVIEW, ex.getErrorCode());
    }
}
