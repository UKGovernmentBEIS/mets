package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;

@Validated
@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaReviewDeterminationValidatorService {

	private final List<EmpVariationCorsiaReviewDeterminationTypeValidator> validators;

    public boolean isValid(EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload,
                           EmpVariationDeterminationType determinationType) {

    	EmpVariationCorsiaReviewDeterminationTypeValidator validator = validators.stream()
            .filter(v -> determinationType.equals(v.getType()))
            .findFirst()
            .orElseThrow(() -> {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            });

        return validator.isValid(requestTaskPayload);
    }
    
    public void validateDeterminationObject(@NotNull @Valid EmpVariationDetermination determination) {}
}
