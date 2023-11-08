package uk.gov.pmrv.api.reporting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class EmissionsCalculationDTO {

    private BigDecimal reportableEmissions;

    private BigDecimal sustainableBiomassEmissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmissionsCalculationDTO that = (EmissionsCalculationDTO) o;

        return ObjectUtils.compare(reportableEmissions, that.reportableEmissions) == 0
            && ObjectUtils.compare(sustainableBiomassEmissions, that.sustainableBiomassEmissions) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportableEmissions, sustainableBiomassEmissions);
    }
}
