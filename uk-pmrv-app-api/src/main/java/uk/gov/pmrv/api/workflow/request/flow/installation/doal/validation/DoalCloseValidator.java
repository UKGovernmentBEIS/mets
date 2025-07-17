package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalDeterminationType;

@Validated
@Service
@RequiredArgsConstructor
public class DoalCloseValidator {

    private final DoalSubmitValidator doalSubmitValidator;

    public void validate(RequestTask requestTask) {
        DoalApplicationSubmitRequestTaskPayload taskPayload =
                (DoalApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate task payload
        doalSubmitValidator.validate(taskPayload);

        // Validate determination
        DoalDetermination determination = taskPayload.getDoal().getDetermination();
        if(!determination.getType().equals(DoalDeterminationType.CLOSED)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}
