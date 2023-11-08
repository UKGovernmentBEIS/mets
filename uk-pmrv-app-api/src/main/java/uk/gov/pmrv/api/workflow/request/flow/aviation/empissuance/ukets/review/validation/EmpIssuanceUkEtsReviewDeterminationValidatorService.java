package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;

import java.util.List;

@Validated
@Service
@RequiredArgsConstructor
public class EmpIssuanceUkEtsReviewDeterminationValidatorService {

    private final List<EmpIssuanceUkEtsReviewDeterminationTypeValidator> validators;

    public boolean isValid(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload,
                           EmpIssuanceDeterminationType determinationType) {

        EmpIssuanceUkEtsReviewDeterminationTypeValidator validator = validators.stream()
            .filter(v -> determinationType.equals(v.getType()))
            .findFirst()
            .orElseThrow(() -> {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            });

        return validator.isValid(requestTaskPayload);
    }

    public void validateDeterminationObject(@NotNull @Valid EmpIssuanceDetermination determination) {}
}
