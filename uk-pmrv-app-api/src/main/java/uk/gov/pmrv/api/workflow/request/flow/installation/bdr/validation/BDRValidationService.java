package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRBdrDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRBdrDataRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationReport;

import java.util.regex.Pattern;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class BDRValidationService {
    private final String BDR_FILE_NAME_PATTERN = "^BDR\\d{5}-\\d{4}-v\\d+-(uploaded by (Operator|Regulator))-(.{1,10})\\.(?i)(doc|docx|xls|xlsx|ppt|pptx|vsd|vsdx|jpg|jpeg|pdf|png|tif|txt|dib|bmp|csv)$";
    private final Pattern PATTERN = Pattern.compile(BDR_FILE_NAME_PATTERN);


    public void validateBDR(@Valid @NotNull BDR bdr) {}

    public void validateVerificationReport(@Valid @NotNull BDRVerificationReport verificationReport) {}

    public void validateRegulatorReviewOutcome(@Valid @NotNull BDRApplicationRegulatorReviewOutcome reviewOutcome) {

        if (ObjectUtils.isEmpty(reviewOutcome.getHasRegulatorSentFreeAllocation()) ||
            ObjectUtils.isEmpty(reviewOutcome.getHasRegulatorSentHSE()) ||
            ObjectUtils.isEmpty(reviewOutcome.getHasRegulatorSentUSE())) {
            throw new BusinessException(MetsErrorCode.INVALID_BDR_REVIEW_OUTCOME);
        }


        if ((!reviewOutcome.getHasRegulatorSentFreeAllocation() &&
            !reviewOutcome.getHasRegulatorSentHSE() &&
            !reviewOutcome.getHasRegulatorSentUSE()) &&
                ObjectUtils.isEmpty(reviewOutcome.getHasOperatorMetDataSubmissionRequirements())) {
            throw new BusinessException(MetsErrorCode.INVALID_BDR_REVIEW_OUTCOME);
        }
    }

    public void validateRegulatorReviewGroupDecisions(@NotEmpty Map<BDRReviewGroup, @Valid BDRReviewDecision> reviewGroupDecisions,
                                        boolean isVerificationPerformed) {

          boolean allAccepted = reviewGroupDecisions.values().stream()
                    .filter(reviewDecision -> reviewDecision.getReviewDataType().equals(BDRReviewDataType.BDR_DATA))
                    .map(BDRBdrDataRegulatorReviewDecision.class::cast)
                    .allMatch(bdrDataReviewDecision -> bdrDataReviewDecision.getType().equals(BDRBdrDataRegulatorReviewDecisionType.ACCEPTED));

         if (!decisionExistsForAllReviewGroups(reviewGroupDecisions, isVerificationPerformed) || !allAccepted) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    public void validateReturnForAmends(final BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload) {

        boolean amendExists = taskPayload.getRegulatorReviewGroupDecisions().entrySet().stream()
            .filter(entry -> entry.getKey().equals(BDRReviewGroup.BDR))
                .anyMatch(entry ->
                    ((BDRBdrDataRegulatorReviewDecision) entry.getValue()).getType().equals(BDRBdrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                );
        if (!amendExists) {
            throw new BusinessException(MetsErrorCode.INVALID_BDR_REVIEW);
        }

        validateVerificationRequiredFromReviewGroupDecisions(taskPayload.getRegulatorReviewGroupDecisions(), taskPayload.getBdr().getIsApplicationForFreeAllocation());
    }

    public void validateAmendsVerification(@Valid @NotNull BDRRequestPayload requestPayload,
                                           @Valid @NotNull BDRApplicationAmendsSubmitRequestTaskPayload taskPayload) {

        if (!taskPayload.isVerificationPerformed() && validateVerificationRequiredFromReviewGroupDecisions(
                requestPayload.getRegulatorReviewGroupDecisions(),
                taskPayload.getBdr().getIsApplicationForFreeAllocation())
            ){
              throw new BusinessException(MetsErrorCode.BDR_MUST_UNDERGO_VERIFICATION);
        }
    }


    private Boolean validateVerificationRequiredFromReviewGroupDecisions(Map<BDRReviewGroup, BDRReviewDecision> regulatorReviewGroupDecisions, Boolean isApplicationForFreeAllocation) {

        Optional<Map.Entry<BDRReviewGroup, BDRReviewDecision>> bdrReviewDecisionEntry = regulatorReviewGroupDecisions.entrySet().stream()
                .filter(entry -> entry.getKey().equals(BDRReviewGroup.BDR))
                .findFirst();

        if (bdrReviewDecisionEntry.isEmpty()) {
            throw new BusinessException(MetsErrorCode.INVALID_BDR_REVIEW);
        }

        BDRBdrDataRegulatorReviewDecision reviewDecision =
                ((BDRBdrDataRegulatorReviewDecision) bdrReviewDecisionEntry.get().getValue());

        if (!reviewDecision.getType().equals(BDRBdrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)) {
             throw new BusinessException(MetsErrorCode.INVALID_BDR_REVIEW);
        }

        Boolean isVerificationRequired =
                ((BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails) reviewDecision.getDetails()).getVerificationRequired();

        return isApplicationForFreeAllocation && !ObjectUtils.isEmpty(isVerificationRequired) && isVerificationRequired ;
    }

    private boolean decisionExistsForAllReviewGroups(Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions,
                                                     boolean isVerificationPerformed) {
        return decisionExistsForAllBDRDataReviewGroups(reviewGroupDecisions)
            && decisionExistsForAllVerificationReviewGroups(reviewGroupDecisions, isVerificationPerformed);
    }

    private boolean decisionExistsForAllBDRDataReviewGroups(Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions) {
        final Set<BDRReviewGroup> verificationDataReviewGroups =
            BDRReviewGroup.getVerificationDataReviewGroups();

        final Set<BDRReviewGroup> bdrDataReviewGroups = reviewGroupDecisions.keySet().stream()
            .filter(bdrReviewGroup -> !verificationDataReviewGroups.contains(bdrReviewGroup))
            .collect(Collectors.toSet());

        return CollectionUtils.isEqualCollection(bdrDataReviewGroups, BDRReviewGroup.getBDRDataReviewGroups());
    }

    private boolean decisionExistsForAllVerificationReviewGroups(Map<BDRReviewGroup, BDRReviewDecision> reviewGroupDecisions,
                                                                 boolean isVerificationPerformed) {

        final Set<BDRReviewGroup> verificationDataReviewGroups = BDRReviewGroup.getVerificationDataReviewGroups();
        return isVerificationPerformed
            ? reviewGroupDecisions.keySet().containsAll(verificationDataReviewGroups)
            : reviewGroupDecisions.keySet().stream().noneMatch(verificationDataReviewGroups::contains);
    }


    public void validateBDRFileName(@Valid @NotNull String bdrFileName) {
        boolean isValid = false;

        if (StringUtil.isNotBlank(bdrFileName))
            isValid = PATTERN.matcher(bdrFileName).matches();

        if(!isValid) {
            throw new BusinessException(MetsErrorCode.BDR_FILENAME_NOT_VALID);
        }
    }
}
