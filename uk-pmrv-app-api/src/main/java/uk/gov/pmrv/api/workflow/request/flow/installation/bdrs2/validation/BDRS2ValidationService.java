package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2GuardQuestions;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ContinueApplicationForFreeAllocationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Violation;


import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class BDRS2ValidationService {

    private static final String BDRS2_FILE_NAME_PATTERN = "^BDRS2-\\d{5}-(\\d{4})-v\\d+-(uploaded by (Operator|Regulator))-(.{1,10})\\.(?i)(doc|docx|xls|xlsx|ppt|pptx|vsd|vsdx|jpg|jpeg|pdf|png|tif|txt|dib|bmp|csv)$";
    private static final Pattern PATTERN = Pattern.compile(BDRS2_FILE_NAME_PATTERN);

    public void validateBDRS2(@Valid @NotNull BDRS2 bdrs2) {
        // Validation is handled by JSR-303 annotations and SpEL expressions in BDRS2 class
    }

    public void validateVerificationReport(@Valid @NotNull BDRS2VerificationReport verificationReport) {
        // Validation is handled by JSR-303 annotations in BDRS2VerificationReport class
    }

    public void validateBDRS2FileName(@Valid @NotNull String bdrs2FileName) {
        boolean isValid = false;

        if (StringUtil.isNotBlank(bdrs2FileName))
            isValid = PATTERN.matcher(bdrs2FileName).matches();

        if(!isValid) {
            throw new BusinessException(MetsErrorCode.BDRS2_FILENAME_NOT_VALID);
        }
    }

    public void validateRegulatorReviewOutcome(
            @Valid @NotNull BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload) {

        BDRS2GuardQuestions guardQuestions = taskPayload.getBdrs2().getBdrs2guardQuestions();
        BDRS2ApplicationRegulatorReviewOutcome outcome = taskPayload.getRegulatorReviewOutcome();

        if (outcome.getFreeAllocationOpinion() == null) {
            throw new BusinessException(
                    MetsErrorCode.INVALID_BDRS2_REVIEW,
                    taskPayload,
                    BDRS2Violation.INVALID_FREE_ALLOCATION_SECTION.getMessage()
            );
        }

        BDRS2ContinueApplicationForFreeAllocationType continueType =
                guardQuestions.getContinueApplicationForFreeAllocationType();

        boolean hasFa =
                BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_HSE.equals(continueType) ||
                        BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_MAIN_SCHEME_PARTICIPANT.equals(continueType);


        boolean covidOpinionPresent = outcome.getCovidAdjustmentsOpinion() != null;

        if (hasFa != covidOpinionPresent) {
            throw new BusinessException(
                    MetsErrorCode.INVALID_BDRS2_REVIEW,
                    taskPayload,
                    BDRS2Violation.INVALID_COVID_ADJUSTMENTS_SECTION.getMessage()
            );
        }

        boolean installationOpinionPresent = outcome.getInstallationSectorOpinion() != null;

        if (hasFa != installationOpinionPresent) {
            throw new BusinessException(
                    MetsErrorCode.INVALID_BDRS2_REVIEW,
                    taskPayload,
                    BDRS2Violation.INVALID_INSTALLATION_SECTOR_SECTION.getMessage()
            );
        }

        boolean installationSectorInScope = Boolean.TRUE.equals(guardQuestions.getInEiteSector());

        boolean cbamVisible = hasFa && installationSectorInScope;
        boolean cbamOpinionPresent = outcome.getCbamSplitOpinion() != null;

        if (cbamVisible != cbamOpinionPresent) {
            throw new BusinessException(
                    MetsErrorCode.INVALID_BDRS2_REVIEW,
                    taskPayload,
                    BDRS2Violation.INVALID_CBAM_SPLIT_SECTION.getMessage()
            );
        }

        if (!hasFa && outcome.getFile() != null) {
            throw new BusinessException(
                    MetsErrorCode.INVALID_BDRS2_REVIEW,
                    taskPayload,
                    BDRS2Violation.INVALID_FILE_SECTION.getMessage()
            );
        }
    }

    public void validateRegulatorReviewGroupDecisions(@NotEmpty Map<BDRS2ReviewGroup, @Valid BDRS2ReviewDecision> reviewGroupDecisions,
                                        boolean isVerificationPerformed) {

          boolean allAccepted = reviewGroupDecisions.values().stream()
                    .filter(reviewDecision -> reviewDecision.getReviewDataType().equals(BDRS2ReviewDataType.BDRS2_DATA))
                    .map(BDRS2Bdrs2DataRegulatorReviewDecision.class::cast)
                    .allMatch(bdrs2DataReviewDecision -> bdrs2DataReviewDecision.getType().equals(BDRS2Bdrs2DataRegulatorReviewDecisionType.ACCEPTED));

         if (!decisionExistsForAllReviewGroups(reviewGroupDecisions, isVerificationPerformed) || !allAccepted) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    public void validateReturnForAmends(final BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload) {

        boolean amendExists = taskPayload.getRegulatorReviewGroupDecisions().entrySet().stream()
            .filter(entry -> entry.getKey().equals(BDRS2ReviewGroup.BDRS2))
                .anyMatch(entry ->
                    ((BDRS2Bdrs2DataRegulatorReviewDecision) entry.getValue()).getType().equals(BDRS2Bdrs2DataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                );
        if (!amendExists) {
            throw new BusinessException(MetsErrorCode.INVALID_BDRS2_REVIEW);
        }
    }

    private boolean decisionExistsForAllReviewGroups(Map<BDRS2ReviewGroup, BDRS2ReviewDecision> reviewGroupDecisions,
                                                     boolean isVerificationPerformed) {
        return decisionExistsForAllBDRS2DataReviewGroups(reviewGroupDecisions)
            && decisionExistsForAllVerificationReviewGroups(reviewGroupDecisions, isVerificationPerformed);
    }

    private boolean decisionExistsForAllBDRS2DataReviewGroups(Map<BDRS2ReviewGroup, BDRS2ReviewDecision> reviewGroupDecisions) {
        final Set<BDRS2ReviewGroup> verificationDataReviewGroups =
            BDRS2ReviewGroup.getVerificationDataReviewGroups();

        final Set<BDRS2ReviewGroup> bdrs2DataReviewGroups = reviewGroupDecisions.keySet().stream()
            .filter(bdrs2ReviewGroup -> !verificationDataReviewGroups.contains(bdrs2ReviewGroup))
            .collect(Collectors.toSet());

        return CollectionUtils.isEqualCollection(bdrs2DataReviewGroups, BDRS2ReviewGroup.getBDRS2DataReviewGroups());
    }

    private boolean decisionExistsForAllVerificationReviewGroups(Map<BDRS2ReviewGroup, BDRS2ReviewDecision> reviewGroupDecisions,
                                                                 boolean isVerificationPerformed) {

        final Set<BDRS2ReviewGroup> verificationDataReviewGroups = BDRS2ReviewGroup.getVerificationDataReviewGroups();
        return isVerificationPerformed
            ? reviewGroupDecisions.keySet().containsAll(verificationDataReviewGroups)
            : reviewGroupDecisions.keySet().stream().noneMatch(verificationDataReviewGroups::contains);
    }

    public void validateAmendsVerification(@Valid @NotNull BDRS2RequestPayload requestPayload,
                                           @Valid @NotNull BDRS2ApplicationAmendsSubmitRequestTaskPayload taskPayload) {

        if (!taskPayload.isVerificationPerformed() &&
            isVerificationRequiredFromReviewGroupDecisions(
                requestPayload.getRegulatorReviewGroupDecisions(),
                taskPayload.getBdrs2().getBdrs2guardQuestions().getRequiresAdditionalSubInstallationSplitsForCbam())) {
            throw new BusinessException(MetsErrorCode.BDRS2_MUST_UNDERGO_VERIFICATION);
        }
    }

    private boolean isVerificationRequiredFromReviewGroupDecisions(
            Map<BDRS2ReviewGroup, BDRS2ReviewDecision> regulatorReviewGroupDecisions,
            Boolean requiresCbam) {

        Optional<Map.Entry<BDRS2ReviewGroup, BDRS2ReviewDecision>> bdrs2ReviewDecisionEntry =
            regulatorReviewGroupDecisions.entrySet().stream()
                .filter(entry -> entry.getKey().equals(BDRS2ReviewGroup.BDRS2))
                .findFirst();

        if (bdrs2ReviewDecisionEntry.isEmpty()) {
            throw new BusinessException(MetsErrorCode.INVALID_BDRS2_REVIEW);
        }

        Boolean isVerificationRequired = isVerificationRequired(bdrs2ReviewDecisionEntry);

        return Boolean.TRUE.equals(requiresCbam) &&
               !ObjectUtils.isEmpty(isVerificationRequired) && isVerificationRequired;
    }

    private static Boolean isVerificationRequired(Optional<Map.Entry<BDRS2ReviewGroup, BDRS2ReviewDecision>> bdrs2ReviewDecisionEntry) {
        BDRS2Bdrs2DataRegulatorReviewDecision reviewDecision =
                bdrs2ReviewDecisionEntry.map(
                        bdrs2ReviewGroupBDRS2ReviewDecisionEntry ->
                                (BDRS2Bdrs2DataRegulatorReviewDecision) bdrs2ReviewGroupBDRS2ReviewDecisionEntry.getValue())
                        .orElseThrow(() -> new BusinessException(MetsErrorCode.INVALID_BDRS2_REVIEW));

        if (!BDRS2Bdrs2DataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED.equals(reviewDecision.getType())) {
            throw new BusinessException(MetsErrorCode.INVALID_BDRS2_REVIEW);
        }

        return ((BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails) reviewDecision.getDetails())
            .getVerificationRequired();
    }
}
