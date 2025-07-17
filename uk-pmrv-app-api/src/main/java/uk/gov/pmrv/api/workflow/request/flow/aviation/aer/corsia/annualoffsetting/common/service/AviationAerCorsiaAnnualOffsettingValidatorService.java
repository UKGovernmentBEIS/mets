package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsetting;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Validated
@AllArgsConstructor
public class AviationAerCorsiaAnnualOffsettingValidatorService {
    private static final double EPSILON = 1e-6;
    private static final double PERCENTAGE = 100.0;

    public void validateAviationAerCorsiaAnnualOffsetting(@NotNull @Valid AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting) {
        BigDecimal expectedValue = BigDecimal.valueOf(aviationAerCorsiaAnnualOffsetting.getSectorGrowth()
                / PERCENTAGE * aviationAerCorsiaAnnualOffsetting.getTotalChapter().doubleValue()).setScale(0, RoundingMode.HALF_UP);

        boolean result = Math.abs(aviationAerCorsiaAnnualOffsetting.getCalculatedAnnualOffsetting() - expectedValue.floatValue()) < EPSILON;

        if(!result){
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

}
