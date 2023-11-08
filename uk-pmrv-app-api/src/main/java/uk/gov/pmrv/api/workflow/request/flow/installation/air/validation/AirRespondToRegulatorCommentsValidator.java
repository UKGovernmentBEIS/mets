package uk.gov.pmrv.api.workflow.request.flow.installation.air.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;

@Validated
@Service
public class AirRespondToRegulatorCommentsValidator {

    public void validate(final @NotNull Integer reference,
                         final Map<Integer, @Valid @NotNull OperatorAirImprovementFollowUpResponse> operatorImprovementFollowUpResponses,
                         final Map<Integer, RegulatorAirImprovementResponse> regulatorImprovementResponses) {

        if (!operatorImprovementFollowUpResponses.containsKey(reference)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
        this.validateReferenceOnRegulator(reference, regulatorImprovementResponses);
    }

    public void validateReferenceOnRegulator(final @NotNull Integer reference,
                                             Map<Integer, RegulatorAirImprovementResponse> regulatorImprovementResponses) {

        if (!regulatorImprovementResponses.containsKey(reference)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}
