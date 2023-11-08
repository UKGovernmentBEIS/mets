package uk.gov.pmrv.api.workflow.request.flow.common.vir.validation;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorReviewResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Validated
@Service
public class VirReviewValidator {

    public void validate(final @Valid @NotNull RegulatorReviewResponse regulatorReviewResponse,
                         final Map<String, OperatorImprovementResponse> operatorImprovementResponses) {
        Set<String> references = operatorImprovementResponses.keySet();
        Set<String> regulatorReferences = regulatorReviewResponse.getRegulatorImprovementResponses().keySet();

        Collection<String> difference = CollectionUtils.disjunction(references, regulatorReferences);

        if(!difference.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_VIR_REVIEW, difference.toArray());
        }
    }
}
