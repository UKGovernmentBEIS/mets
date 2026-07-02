package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NER;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERNerDataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERNerDataRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewOpinion;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERViolation;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Validated
public class NERValidationService {

    private static final String NER_FILE_NAME_PATTERN = "^NER-\\d{5}-\\d+-v\\d+-(uploaded by (Operator|Regulator))-(.{1,10})\\.(?i)(doc|docx|xls|xlsx|ppt|pptx|vsd|vsdx|jpg|jpeg|pdf|png|tif|txt|dib|bmp|csv)$";
    private static final Pattern PATTERN = Pattern.compile(NER_FILE_NAME_PATTERN);

    public void validateNer(@Valid @NotNull NER ner) {
        // Validation is handled by JSR-303 annotations and SpEL expressions in NER class
    }

    public void validateVerificationReport(@Valid @NotNull NERVerificationReport verificationReport) {
        // Validation is handled by JSR-303 annotations in NERVerificationReport class
    }

    public void validateNerFileName(@Valid @NotNull String nerFileName) {
        boolean isValid = false;

        if (StringUtils.isNotBlank(nerFileName))
            isValid = PATTERN.matcher(nerFileName).matches();

        if(!isValid) {
            throw new BusinessException(MetsErrorCode.NER_FILENAME_NOT_VALID);
        }
    }

    public void validateReturnForAmends(final NERApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload) {

        boolean amendExists = taskPayload.getRegulatorReviewGroupDecisions().entrySet().stream()
                .filter(entry -> entry.getKey().equals(NERReviewGroup.NER))
                .anyMatch(entry ->
                        ((NERNerDataRegulatorReviewDecision) entry.getValue()).getType().equals(NERNerDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                );
        if (!amendExists) {
            throw new BusinessException(MetsErrorCode.INVALID_NER_REVIEW);
        }
    }

    public void validateAmendsVerification(@Valid @NotNull NerRequestPayload requestPayload,
                                           @Valid @NotNull NERApplicationAmendsSubmitRequestTaskPayload taskPayload) {

        if (isVerificationRequiredFromReviewGroupDecisions(requestPayload.getRegulatorReviewGroupDecisions())) {
            if (!taskPayload.isVerificationPerformed())
                throw new BusinessException(MetsErrorCode.NER_MUST_UNDERGO_VERIFICATION);
        }
    }

    public void validateRegulatorReviewOutcome(@Valid @NotNull NERApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload, NERReviewOpinion reviewOpinion) {
        NERApplicationRegulatorReviewOutcome outcome = taskPayload.getRegulatorReviewOutcome();

        if (outcome.getOpinion() != reviewOpinion) {
            throw new BusinessException(
                    MetsErrorCode.INVALID_NER_REVIEW,
                    taskPayload,
                    NERViolation.INVALID_REVIEW_OPINION.getMessage()
            );
        }
    }

    public Boolean isVerificationRequiredFromReviewGroupDecisions(Map<NERReviewGroup, NERReviewDecision> regulatorReviewGroupDecisions) {

        Optional<Map.Entry<NERReviewGroup, NERReviewDecision>> nerReviewDecisionEntry = regulatorReviewGroupDecisions.entrySet().stream()
                .filter(entry -> entry.getKey().equals(NERReviewGroup.NER))
                .findFirst();

        if (nerReviewDecisionEntry.isEmpty()) {
            throw new BusinessException(MetsErrorCode.INVALID_NER_REVIEW);
        }

        NERNerDataRegulatorReviewDecision reviewDecision =
                ((NERNerDataRegulatorReviewDecision) nerReviewDecisionEntry.get().getValue());

        if (!reviewDecision.getType().equals(NERNerDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)) {
            throw new BusinessException(MetsErrorCode.INVALID_NER_REVIEW);
        }

        Boolean isVerificationRequired =
                ((NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails) reviewDecision.getDetails()).getVerificationRequired();

        return !ObjectUtils.isEmpty(isVerificationRequired) && isVerificationRequired ;
    }
}
