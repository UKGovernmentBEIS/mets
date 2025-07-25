package uk.gov.pmrv.api.workflow.request.flow.installation.air.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;

import java.util.Map;
import java.util.Set;

@Validated
@Service
public class AirReviewValidator {

    public void validate(final @NotNull @Valid AirApplicationReviewRequestTaskPayload taskPayload) {

        final Map<Integer, OperatorAirImprovementResponse> operatorImprovementResponses =
            taskPayload.getOperatorImprovementResponses();
        final Map<Integer, RegulatorAirImprovementResponse> regulatorImprovementResponses =
            taskPayload.getRegulatorReviewResponse().getRegulatorImprovementResponses();

        final Set<Integer> difference =
            SetUtils.disjunction(operatorImprovementResponses.keySet(), regulatorImprovementResponses.keySet());

        if (!difference.isEmpty()) {
            throw new BusinessException(MetsErrorCode.INVALID_AIR_REVIEW, difference.toArray());
        }
    }
}
