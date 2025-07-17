package uk.gov.pmrv.api.workflow.request.flow.common.vir.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;

import java.util.Map;

@Validated
@Service
public class VirRespondToRegulatorCommentsValidator {

    public void validate(@NotBlank String reference, Map<String, @Valid @NotNull OperatorImprovementFollowUpResponse> operatorImprovementFollowUpResponses,
                         Map<String, RegulatorImprovementResponse> regulatorImprovementResponses) {

        if(!operatorImprovementFollowUpResponses.containsKey(reference)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        validateReferenceOnRegulator(reference, regulatorImprovementResponses);
    }

    public void validateReferenceOnRegulator(@NotBlank String reference, Map<String, RegulatorImprovementResponse> regulatorImprovementResponses) {
        if(!regulatorImprovementResponses.containsKey(reference)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}
