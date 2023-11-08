package uk.gov.pmrv.api.aviationreporting.common.domain;

import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AviationAerTotalReportableEmissions {

    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal reportableEmissions;
}
