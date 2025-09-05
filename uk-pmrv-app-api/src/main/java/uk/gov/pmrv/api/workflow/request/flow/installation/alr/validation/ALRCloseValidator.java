package uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.DoalDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.enums.DoalDeterminationType;

@Validated
@Service
@RequiredArgsConstructor
public class ALRCloseValidator {

    private final ALRValidationService alrValidationService;

    public void validate(RequestTask requestTask) {
        ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (ALRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate task payload
        alrValidationService.validateRegulatorSubmitTaskPayload(taskPayload);

        // Validate determination
        DoalDetermination determination = taskPayload.getRegulatorReviewOutcome().getDetermination();
        if(!determination.getType().equals(DoalDeterminationType.CLOSED_ALR)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}
