package uk.gov.pmrv.api.reporting.domain.dto;

import java.math.BigDecimal;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.reporting.domain.GlobalWarmingPotential;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PfcEmissionsCalculationDTO {

    private final BigDecimal globalWarmingPotentialCF4 = GlobalWarmingPotential.PFC_CF4.getValue();
    private final BigDecimal globalWarmingPotentialC2F6 = GlobalWarmingPotential.PFC_C2F6.getValue();
    private BigDecimal amountOfCF4;
    private BigDecimal totalCF4Emissions;
    private BigDecimal amountOfC2F6;
    private BigDecimal totalC2F6Emissions;
    private BigDecimal reportableEmissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PfcEmissionsCalculationDTO that = (PfcEmissionsCalculationDTO) o;

        return ObjectUtils.compare(globalWarmingPotentialCF4, that.globalWarmingPotentialCF4) == 0
            && ObjectUtils.compare(globalWarmingPotentialC2F6, that.globalWarmingPotentialC2F6) == 0
            && ObjectUtils.compare(amountOfCF4, that.amountOfCF4) == 0
            && ObjectUtils.compare(totalCF4Emissions, that.totalCF4Emissions) == 0
            && ObjectUtils.compare(amountOfC2F6, that.amountOfC2F6) == 0
            && ObjectUtils.compare(totalC2F6Emissions, that.totalC2F6Emissions) == 0
            && ObjectUtils.compare(reportableEmissions, that.reportableEmissions) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(globalWarmingPotentialCF4, globalWarmingPotentialC2F6, amountOfCF4, amountOfC2F6, totalC2F6Emissions, totalCF4Emissions,
            reportableEmissions);
    }

}
