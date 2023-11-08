package uk.gov.pmrv.api.workflow.request.flow.installation.vir.validation;


import lombok.RequiredArgsConstructor;

import org.apache.commons.collections.CollectionUtils;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Validated
@RequiredArgsConstructor
public class VirSubmitValidatorService {

    public void validate(@NotEmpty Map<String, @Valid @NotNull OperatorImprovementResponse> operatorImprovementResponseMap,
                         VirVerificationData verificationData) {
        Set<String> references = Stream.of(
                verificationData.getUncorrectedNonConformities().keySet(),
                verificationData.getPriorYearIssues().keySet(),
                verificationData.getRecommendedImprovements().keySet()
        ).flatMap(Set::stream).collect(Collectors.toSet());

        Collection<String> difference = CollectionUtils.disjunction(references, operatorImprovementResponseMap.keySet());

        if(!difference.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_VIR, difference.toArray());
        }
    }
}
