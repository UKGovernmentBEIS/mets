package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRReviewDecisionType;

@Service
@RequiredArgsConstructor
public class WasteQDRValidationService {

    public void validateWasteQDR(@Valid @NotNull WasteQDR qdr) {}

    public void validateReturnForAmends(final WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload) {

        boolean amendExists = WasteQDRReviewDecisionType.OPERATOR_AMENDS_NEEDED.equals(taskPayload.getReviewDecision().getType());

        if (!amendExists) {
            throw new BusinessException(MetsErrorCode.INVALID_WASTE_QDR_REVIEW);
        }
    }


    public void validateReviewDecision(final WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload) {
        boolean accepted = WasteQDRReviewDecisionType.ACCEPTED.equals(taskPayload.getReviewDecision().getType());

        if (!accepted) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}
