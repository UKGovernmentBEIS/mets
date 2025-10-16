package uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.validation.AllowanceAllocationValidator;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALR;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAlrDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAlrDataRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRPreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.enums.DoalDeterminationType;


import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class ALRValidationService {

    private final AllowanceAllocationValidator allowanceAllocationValidator;

    private final String ALR_FILE_NAME_PATTERN = "^ALR\\d{5}-(\\d{4}|FINAL)-v\\d+-(uploaded by (Operator|Regulator))-(.{1,10})\\.(?i)(doc|docx|xls|xlsx|ppt|pptx|vsd|vsdx|jpg|jpeg|pdf|png|tif|txt|dib|bmp|csv)$";
    private final Pattern PATTERN = Pattern.compile(ALR_FILE_NAME_PATTERN);

    public void validateALR(@Valid @NotNull ALR alr) {}

    public void validateVerificationReport(@Valid @NotNull ALRVerificationReport verificationReport) {}

    public void validateALRFileName(@Valid @NotNull String alrFileName) {
        boolean isValid = false;

        if (StringUtil.isNotBlank(alrFileName))
            isValid = PATTERN.matcher(alrFileName).matches();

        if(!isValid) {
            throw new BusinessException(MetsErrorCode.ALR_FILENAME_NOT_VALID);
        }
    }

    public void validateReturnForAmends(final ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload) {

        boolean amendExists = taskPayload.getRegulatorReviewGroupDecisions().entrySet().stream()
                .filter(entry -> entry.getKey().equals(ALRReviewGroup.ALR))
                .anyMatch(entry ->
                        ((ALRAlrDataRegulatorReviewDecision) entry.getValue()).getType().equals(ALRAlrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                );
        if (!amendExists) {
            throw new BusinessException(MetsErrorCode.INVALID_ALR_REVIEW);
        }
    }

    public void validateRegulatorSubmitTaskPayload(@Valid @NotNull ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload) {
        validateALR(taskPayload.getAlr());

        Set<ALRPreliminaryAllocation> alrPreliminaryAllocations = taskPayload.getRegulatorReviewOutcome().getAllocations();
        Set<PreliminaryAllocation> preliminaryAllocations = alrPreliminaryAllocations.stream()
                .map(pa -> (PreliminaryAllocation) pa)
                .collect(Collectors.toSet());

        // Validate Proceed to Authority
        if(taskPayload.getRegulatorReviewOutcome().getDetermination().getType().equals(DoalDeterminationType.PROCEED_TO_AUTHORITY)) {
            validateProceedToAuthorityDetermination(taskPayload);
        }


        if(!preliminaryAllocations.isEmpty() && !allowanceAllocationValidator.isValid(preliminaryAllocations)) {
            throw new BusinessException(MetsErrorCode.INVALID_ALR_PRELIMINARY_ALLOCATIONS, taskPayload);
        }
    }

    private void validateProceedToAuthorityDetermination(final ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload) {
        // Validate article reasons
        DoalProceedToAuthorityDetermination determination =
                (DoalProceedToAuthorityDetermination) taskPayload.getRegulatorReviewOutcome().getDetermination();
        boolean areArticleReasonsValid = determination.getArticleReasonItems().stream()
                .allMatch(item -> item.getGroupType().equals(determination.getArticleReasonGroupType()));

        if(!areArticleReasonsValid) {
            throw new BusinessException(MetsErrorCode.INVALID_ALR_ARTICLE_REASONS,
                    taskPayload);
        }
    }

    public void validateAmendsVerification(@Valid @NotNull ALRRequestPayload requestPayload,
                                           @Valid @NotNull ALRApplicationAmendsSubmitRequestTaskPayload taskPayload) {

        if (isVerificationRequiredFromReviewGroupDecisions(requestPayload.getRegulatorReviewGroupDecisions())) {
            if (!taskPayload.isVerificationPerformed())
                throw new BusinessException(MetsErrorCode.ALR_MUST_UNDERGO_VERIFICATION);
        }
    }

    public Boolean isVerificationRequiredFromReviewGroupDecisions(Map<ALRReviewGroup, ALRReviewDecision> regulatorReviewGroupDecisions) {

        Optional<Map.Entry<ALRReviewGroup, ALRReviewDecision>> alrReviewDecisionEntry = regulatorReviewGroupDecisions.entrySet().stream()
                .filter(entry -> entry.getKey().equals(ALRReviewGroup.ALR))
                .findFirst();

        if (alrReviewDecisionEntry.isEmpty()) {
            throw new BusinessException(MetsErrorCode.INVALID_ALR_REVIEW);
        }

        ALRAlrDataRegulatorReviewDecision reviewDecision =
                ((ALRAlrDataRegulatorReviewDecision) alrReviewDecisionEntry.get().getValue());

        if (!reviewDecision.getType().equals(ALRAlrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)) {
            throw new BusinessException(MetsErrorCode.INVALID_ALR_REVIEW);
        }

        Boolean isVerificationRequired =
                ((ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails) reviewDecision.getDetails()).getVerificationRequired();

        return !ObjectUtils.isEmpty(isVerificationRequired) && isVerificationRequired ;
    }
}
