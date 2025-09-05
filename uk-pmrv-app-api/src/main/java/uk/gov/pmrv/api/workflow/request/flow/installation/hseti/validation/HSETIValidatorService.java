package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETI;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewOverallDecisionType;

@Service
@Validated
public class HSETIValidatorService {

    public void validateHSETI(@NotNull @Valid HSETI hseti) {}

    public void validateReturnForAmends(final HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload) {

        if (ObjectUtils.isEmpty(taskPayload.getRegulatorReviewGroupDecisions()) || !amendsTypeExists(taskPayload)) {
            throw new BusinessException(MetsErrorCode.INVALID_HSE_TI_REVIEW);
        }

    }

    public void validateRegulatorReview(HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload) {
        validateRegulatorReviewOverallDecision(taskPayload);
    }


    private void validateRegulatorReviewOverallDecision(final HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload) {
        if (taskPayload
                .getRegulatorReviewGroupDecisions()
                .values()
                .stream()
                .noneMatch(v -> HSETIRegulatorReviewDecisionType.REJECTED.equals(v.getType())) &&
                HSETIRegulatorReviewOverallDecisionType.REJECTED.equals(taskPayload.getOverallDecision().getType()) ) {
             throw new BusinessException(MetsErrorCode.INVALID_HSE_TI_REVIEW);
        }
    }

    private boolean amendsTypeExists(final HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload) {
        return taskPayload.getRegulatorReviewGroupDecisions().values().stream()
                .anyMatch(v -> HSETIRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED.equals(v.getType()));
    }
}
