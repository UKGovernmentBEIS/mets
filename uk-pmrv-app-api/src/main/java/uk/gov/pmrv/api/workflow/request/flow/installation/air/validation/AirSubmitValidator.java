package uk.gov.pmrv.api.workflow.request.flow.installation.air.validation;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovement;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementResponse;

@Service
@Validated
@RequiredArgsConstructor
public class AirSubmitValidator {

    public void validate(
        final Map<Integer, @Valid @NotNull OperatorAirImprovementResponse> operatorImprovementResponses,
        final Map<Integer, AirImprovement> airImprovements
    ) {

        final Set<Integer> difference =
            SetUtils.disjunction(airImprovements.keySet(), operatorImprovementResponses.keySet());

        if (!difference.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_AIR, difference.toArray());
        }
    }
}
