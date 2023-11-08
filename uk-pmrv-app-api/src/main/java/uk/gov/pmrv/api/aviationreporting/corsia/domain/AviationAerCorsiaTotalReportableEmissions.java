package uk.gov.pmrv.api.aviationreporting.corsia.domain;

import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerTotalReportableEmissions;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AviationAerCorsiaTotalReportableEmissions extends AviationAerTotalReportableEmissions {

    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal reportableOffsetEmissions;

    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal reportableReductionClaimEmissions;
}
