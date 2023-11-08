package uk.gov.pmrv.api.reporting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FallbackEmissionsCalculationDTO {
    private BigDecimal reportableEmissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FallbackEmissionsCalculationDTO that = (FallbackEmissionsCalculationDTO) o;

        return ObjectUtils.compare(reportableEmissions, that.reportableEmissions) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportableEmissions);
    }
}
