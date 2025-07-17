package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRBdrDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRBdrDataRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationReportDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationReportDataRegulatorReviewDecisionType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class BDRValidationServiceTest {

    @InjectMocks
    private BDRValidationService bdrValidationService;


    @Test
    void validateRegulatorReviewOutcome_hasRegulatorSentFreeAllocationIsNull_throwBusinessException() {
        BDRApplicationRegulatorReviewOutcome reviewOutcome = BDRApplicationRegulatorReviewOutcome
                .builder()
                .hasRegulatorSentFreeAllocation(null)
                .hasRegulatorSentHSE(true)
                .hasRegulatorSentUSE(true)
                .hasOperatorMetDataSubmissionRequirements(true)
                .build();

		BusinessException be = assertThrows(BusinessException.class, () ->
                bdrValidationService.validateRegulatorReviewOutcome(reviewOutcome));
		assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_BDR_REVIEW_OUTCOME);
    }


    @Test
    void validateRegulatorReviewOutcome_hasRegulatorSentHSEIsNull_throwBusinessException() {
        BDRApplicationRegulatorReviewOutcome reviewOutcome = BDRApplicationRegulatorReviewOutcome
                .builder()
                .hasRegulatorSentFreeAllocation(true)
                .hasRegulatorSentHSE(null)
                .hasRegulatorSentUSE(true)
                .hasOperatorMetDataSubmissionRequirements(true)
                .build();

		BusinessException be = assertThrows(BusinessException.class, () ->
                bdrValidationService.validateRegulatorReviewOutcome(reviewOutcome));
		assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_BDR_REVIEW_OUTCOME);
    }

    @Test
    void validateRegulatorReviewOutcome_hasRegulatorSentUSEIsNull_throwBusinessException() {
        BDRApplicationRegulatorReviewOutcome reviewOutcome = BDRApplicationRegulatorReviewOutcome
                .builder()
                .hasRegulatorSentFreeAllocation(true)
                .hasRegulatorSentHSE(true)
                .hasRegulatorSentUSE(null)
                .hasOperatorMetDataSubmissionRequirements(true)
                .build();

		BusinessException be = assertThrows(BusinessException.class, () ->
                bdrValidationService.validateRegulatorReviewOutcome(reviewOutcome));
		assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_BDR_REVIEW_OUTCOME);
    }



    @Test
    void validateRegulatorReviewOutcome_reviewOutcomeValid_operatorHasNotMetDataSubmissionRequirements_throwBusinessException() {
        BDRApplicationRegulatorReviewOutcome reviewOutcome = BDRApplicationRegulatorReviewOutcome
                .builder()
                .hasRegulatorSentFreeAllocation(false)
                .hasRegulatorSentHSE(false)
                .hasRegulatorSentUSE(false)
                .hasOperatorMetDataSubmissionRequirements(null)
                .build();

		BusinessException be = assertThrows(BusinessException.class, () ->
                bdrValidationService.validateRegulatorReviewOutcome(reviewOutcome));
		assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_BDR_REVIEW_OUTCOME);
    }

    @Test
    void validateRegulatorReviewOutcome_reviewOutcomeValid_operatorHasMetDataSubmissionRequirements_doNotThrowBusinessException() {
            BDRApplicationRegulatorReviewOutcome reviewOutcome = BDRApplicationRegulatorReviewOutcome
                .builder()
                .hasRegulatorSentFreeAllocation(true)
                .hasRegulatorSentHSE(true)
                .hasRegulatorSentUSE(true)
                .hasOperatorMetDataSubmissionRequirements(true)
                .build();

        assertThatCode(() -> bdrValidationService.validateRegulatorReviewOutcome(reviewOutcome)).doesNotThrowAnyException();
    }

    @Test
    void validateRegulatorReviewGroupDecisions_decisionDoesNotExistForABdrDataGroup_throwBusinessException() {
        Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = Map.of();

        BusinessException be = assertThrows(BusinessException.class, () ->
                bdrValidationService.validateRegulatorReviewGroupDecisions(reviewGroupDecisions,false));
		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateRegulatorReviewGroupDecisions_decisionDoesNotExistForAVerificationReportGroup_throwBusinessException() {
        Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = Map.of(
                BDRReviewGroup.BDR,
                BDRBdrDataRegulatorReviewDecision
                        .builder()
                        .type(BDRBdrDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.BDR_DATA)
                        .build(),
                BDRReviewGroup.OVERALL_DECISION,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()
        );

        BusinessException be = assertThrows(BusinessException.class, () ->
                bdrValidationService.validateRegulatorReviewGroupDecisions(reviewGroupDecisions,true));
		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateRegulatorReviewGroupDecisions_notAllGroupsAccepted_throwBusinessException() {
        Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = Map.of(
                BDRReviewGroup.BDR,
                BDRBdrDataRegulatorReviewDecision
                        .builder()
                        .type(BDRBdrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(BDRReviewDataType.BDR_DATA)
                        .build(),
                BDRReviewGroup.OVERALL_DECISION,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build(),
                BDRReviewGroup.OPINION_STATEMENT,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()

        );
        BusinessException be = assertThrows(BusinessException.class, () ->
                bdrValidationService.validateRegulatorReviewGroupDecisions(reviewGroupDecisions,false));
		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateRegulatorReviewGroupDecisions_allGroupsAcceptedAndNoneMissing_doNotThrowBusinessException() {
        Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = Map.of(
                BDRReviewGroup.BDR,
                BDRBdrDataRegulatorReviewDecision
                        .builder()
                        .type(BDRBdrDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.BDR_DATA)
                        .build(),
                BDRReviewGroup.OVERALL_DECISION,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build(),
                BDRReviewGroup.OPINION_STATEMENT,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()

        );

        assertThatCode(() -> bdrValidationService.validateRegulatorReviewGroupDecisions(reviewGroupDecisions, true)).doesNotThrowAnyException();
    }

    @Test
    void validateReturnForAmends_amendDoesNotExist_throwBusinessException() {
        Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = Map.of(
                BDRReviewGroup.BDR,
                BDRBdrDataRegulatorReviewDecision
                        .builder()
                        .type(BDRBdrDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.BDR_DATA)
                        .details(BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder().verificationRequired(true).build())
                        .build(),
                BDRReviewGroup.OVERALL_DECISION,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build(),
                BDRReviewGroup.OPINION_STATEMENT,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()

        );
        BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

       BusinessException be = assertThrows(BusinessException.class, () ->
                bdrValidationService.validateReturnForAmends(taskPayload));
		assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_BDR_REVIEW);
    }

    @Test
    void validateReturnForAmends_missingBDRReviewDecisionEntry_throwBusinessException() {
        Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = Map.of(
                BDRReviewGroup.OVERALL_DECISION,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build(),
                BDRReviewGroup.OPINION_STATEMENT,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()

        );
        BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

       BusinessException be = assertThrows(BusinessException.class, () ->
                bdrValidationService.validateReturnForAmends(taskPayload));
		assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_BDR_REVIEW);

    }



    @Test
    void validateReturnForAmends_applicationIsNotForFreeAllocation_verificationRequiredIsNull_doNotThrowException() {
        Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = Map.of(
                BDRReviewGroup.BDR,
                BDRBdrDataRegulatorReviewDecision
                        .builder()
                        .type(BDRBdrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(BDRReviewDataType.BDR_DATA)
                        .details(BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder().verificationRequired(null).build())
                        .build(),
                BDRReviewGroup.OVERALL_DECISION,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build(),
                BDRReviewGroup.OPINION_STATEMENT,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()

        );
        BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .bdr(BDR.builder().isApplicationForFreeAllocation(false).build())
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        assertThatCode(() -> bdrValidationService.validateReturnForAmends(taskPayload)).doesNotThrowAnyException();
    }

    @Test
    void validateReturnForAmends_applicationIsForFreeAllocation_BDRReviewDecisionEntryButVerificationRequiredIsNull_doNotThrowException() {
        Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = Map.of(
                BDRReviewGroup.BDR,
                BDRBdrDataRegulatorReviewDecision
                        .builder()
                        .type(BDRBdrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(BDRReviewDataType.BDR_DATA)
                        .details(BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder().verificationRequired(null).build())
                        .build(),
                BDRReviewGroup.OVERALL_DECISION,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build(),
                BDRReviewGroup.OPINION_STATEMENT,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()

        );
        BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .bdr(BDR.builder().isApplicationForFreeAllocation(true).build())
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        assertThatCode(() -> bdrValidationService.validateReturnForAmends(taskPayload)).doesNotThrowAnyException();
    }

    @Test
    void validateReturnForAmends_allPreconditionsSatisfied_doNotThrowBusinessException() {
        Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = Map.of(
                BDRReviewGroup.BDR,
                BDRBdrDataRegulatorReviewDecision
                        .builder()
                        .type(BDRBdrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(BDRReviewDataType.BDR_DATA)
                        .details(BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder().verificationRequired(true).build())
                        .build(),
                BDRReviewGroup.OVERALL_DECISION,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build(),
                BDRReviewGroup.OPINION_STATEMENT,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()

        );
        BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .bdr(BDR.builder().isApplicationForFreeAllocation(true).build())
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        assertThatCode(() -> bdrValidationService.validateReturnForAmends(taskPayload)).doesNotThrowAnyException();
    }

    @Test
    void validateAmendsVerification_applicationIsForFreeAllocation_verificationRequiredTrue_verificationPerformed_doNotThrowException() {
        Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = Map.of(
                BDRReviewGroup.BDR,
                BDRBdrDataRegulatorReviewDecision
                        .builder()
                        .type(BDRBdrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(BDRReviewDataType.BDR_DATA)
                        .details(BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder().verificationRequired(true).build())
                        .build(),
                BDRReviewGroup.OVERALL_DECISION,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build(),
                BDRReviewGroup.OPINION_STATEMENT,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()

        );

        BDRRequestPayload requestPayload = BDRRequestPayload
                .builder()
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        BDRApplicationAmendsSubmitRequestTaskPayload taskPayload = BDRApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .verificationPerformed(true)
                .bdr(BDR.builder().isApplicationForFreeAllocation(true).build())
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        assertThatCode(() -> bdrValidationService.validateAmendsVerification(requestPayload, taskPayload)).doesNotThrowAnyException();
    }

    @Test
    void validateAmendsVerification_applicationIsForFreeAllocation_verificationRequiredTrue_verificationNotPerformed_throwBusinessException() {
        Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = Map.of(
                BDRReviewGroup.BDR,
                BDRBdrDataRegulatorReviewDecision
                        .builder()
                        .type(BDRBdrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(BDRReviewDataType.BDR_DATA)
                        .details(BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder().verificationRequired(true).build())
                        .build(),
                BDRReviewGroup.OVERALL_DECISION,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build(),
                BDRReviewGroup.OPINION_STATEMENT,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()

        );

        BDRRequestPayload requestPayload = BDRRequestPayload
                .builder()
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        BDRApplicationAmendsSubmitRequestTaskPayload taskPayload = BDRApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .verificationPerformed(false)
                .bdr(BDR.builder().isApplicationForFreeAllocation(true).build())
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        BusinessException be = assertThrows(BusinessException.class, () ->
            bdrValidationService.validateAmendsVerification(requestPayload, taskPayload));
         assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.BDR_MUST_UNDERGO_VERIFICATION);

    }

    @Test
    void validateAmendsVerification_applicationIsForFreeAllocation_verificationNotRequired_verificationNotPerformed_doNotThrowException() {
        Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = Map.of(
                BDRReviewGroup.BDR,
                BDRBdrDataRegulatorReviewDecision
                        .builder()
                        .type(BDRBdrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(BDRReviewDataType.BDR_DATA)
                        .details(BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder().verificationRequired(false).build())
                        .build(),
                BDRReviewGroup.OVERALL_DECISION,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build(),
                BDRReviewGroup.OPINION_STATEMENT,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()

        );

        BDRRequestPayload requestPayload = BDRRequestPayload
                .builder()
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        BDRApplicationAmendsSubmitRequestTaskPayload taskPayload = BDRApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .verificationPerformed(false)
                .bdr(BDR.builder().isApplicationForFreeAllocation(true).build())
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        assertThatCode(() -> bdrValidationService.validateAmendsVerification(requestPayload, taskPayload)).doesNotThrowAnyException();

    }

    @Test
    void validateAmendsVerification_bdrIsNotVerifiedButVerificationIsRequired_throwBusinessException() {
          Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = Map.of(
                BDRReviewGroup.BDR,
                BDRBdrDataRegulatorReviewDecision
                        .builder()
                        .type(BDRBdrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(BDRReviewDataType.BDR_DATA)
                        .details(BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder().verificationRequired(true).build())
                        .build(),
                BDRReviewGroup.OVERALL_DECISION,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build(),
                BDRReviewGroup.OPINION_STATEMENT,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()

        );

        BDRRequestPayload requestPayload = BDRRequestPayload
                .builder()
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        BDRApplicationAmendsSubmitRequestTaskPayload taskPayload = BDRApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .bdr(BDR.builder().isApplicationForFreeAllocation(true).build())
                .verificationPerformed(false)
                .build();

        BusinessException be = assertThrows(BusinessException.class, () ->
                bdrValidationService.validateAmendsVerification(requestPayload, taskPayload));
		assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.BDR_MUST_UNDERGO_VERIFICATION);
    }

    @Test
    void validateAmendsVerification_bdrIsNotVerifiedAndVerificationIsNotRequired_doNotThrowBusinessException() {
          Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = Map.of(
                BDRReviewGroup.BDR,
                BDRBdrDataRegulatorReviewDecision
                        .builder()
                        .type(BDRBdrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(BDRReviewDataType.BDR_DATA)
                        .details(BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder().verificationRequired(false).build())
                        .build(),
                BDRReviewGroup.OVERALL_DECISION,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build(),
                BDRReviewGroup.OPINION_STATEMENT,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()

        );

        BDRRequestPayload requestPayload = BDRRequestPayload
                .builder()
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        BDRApplicationAmendsSubmitRequestTaskPayload taskPayload = BDRApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .bdr(BDR.builder().isApplicationForFreeAllocation(false).build())
                .verificationPerformed(false)
                .build();

        assertThatCode(() -> bdrValidationService.validateAmendsVerification(requestPayload, taskPayload)).doesNotThrowAnyException();
    }


    @Test
    void validateAmendsVerification_bdrIsVerifiedAndVerificationIsRequired_doNotThrowBusinessException() {
          Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions = Map.of(
                BDRReviewGroup.BDR,
                BDRBdrDataRegulatorReviewDecision
                        .builder()
                        .type(BDRBdrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(BDRReviewDataType.BDR_DATA)
                        .details(BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder().verificationRequired(true).build())
                        .build(),
                BDRReviewGroup.OVERALL_DECISION,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build(),
                BDRReviewGroup.OPINION_STATEMENT,
                BDRVerificationReportDataRegulatorReviewDecision
                        .builder()
                        .type(BDRVerificationReportDataRegulatorReviewDecisionType.ACCEPTED)
                        .reviewDataType(BDRReviewDataType.VERIFICATION_REPORT_DATA)
                        .build()

        );

        BDRRequestPayload requestPayload = BDRRequestPayload
                .builder()
                .regulatorReviewGroupDecisions(reviewGroupDecisions)
                .build();

        BDRApplicationAmendsSubmitRequestTaskPayload taskPayload = BDRApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .bdr(BDR.builder().isApplicationForFreeAllocation(true).build())
                .verificationPerformed(true)
                .build();

        assertThatCode(() -> bdrValidationService.validateAmendsVerification(requestPayload, taskPayload)).doesNotThrowAnyException();
    }

    @Test
    public void validateBDRFileName_valid() {
        String[] validFileNames = {
                "BDR00017-2025-v1-uploaded by Operator-OPp5.txt",        // Account ID 5 digits, valid extension, valid name
                "BDR12345-2024-v2-uploaded by Regulator-Test.pdf",       // Account ID 5 digits, valid extension, valid name
                "BDR45678-2023-v10-uploaded by Operator-Plant1.csv",     // Account ID 5 digits, valid extension, valid name
                "BDR00099-2022-v99-uploaded by Regulator-Alpha.DOCX",    // Account ID 5 digits, case-insensitive extension, valid name
                "BDR10001-2026-v5-uploaded by Operator-GH12.Jpg",         // Account ID 5 digits, case-insensitive extension, valid name
                "BDR10001-2026-v5-uploaded by Operator-Inst,#$! n.Jpg"         // Account ID 5 digits, case-insensitive extension, installation name with symbols
        };

        for (String fileName : validFileNames) {
            assertDoesNotThrow(() -> bdrValidationService.validateBDRFileName(fileName),
                    "Expected no exception for valid filename: " + fileName);
        }
    }

    @Test
    public void validateBDRFileName_invalid() {
        String[] invalidFileNames = {
                "XYZBDR00017-2025-v1-uploaded by Operator-OPp5.txt", // ❌ Does not start with BDR
                "BDR123-2025-v1-uploaded by Operator-OPp5.txt",      // ❌ Account ID not 5 digits
                "BDR123456-2024-v2-uploaded by Regulator-Test.Pdf",   // ❌ Account ID more than 5 digits
                "BDR00017-2025-v-uploaded by Operator-OPp5.txt",      // ❌ Missing version number
                "BDR00017-2025-v1-uploaded by User-OPp5.txt",         // ❌ "uploaded by User" is invalid
                "BDR00017-2025-v1-uploaded by Operator-LongFileName.txt", // ❌ Installation name > 10 chars
                "BDR00017-2025-v1-uploaded by Operator-OPp5",         // ❌ Missing file extension
                "BDR00017-2025-v1-uploaded by Operator-OPp5.ZIP",     // ❌ Invalid extension (case-sensitive)
                "BDR00017-2025-v1-uploaded by Operator-VERY LONG NAME01234.pdf"     // ❌ Installation name > 10 characters
        };

        for (String fileName : invalidFileNames) {
            BusinessException thrown = assertThrows(BusinessException.class,
                    () -> bdrValidationService.validateBDRFileName(fileName),
                    "Expected BusinessException for invalid filename: " + fileName);
            Assertions.assertEquals(MetsErrorCode.BDR_FILENAME_NOT_VALID, thrown.getErrorCode());
        }
    }

}
