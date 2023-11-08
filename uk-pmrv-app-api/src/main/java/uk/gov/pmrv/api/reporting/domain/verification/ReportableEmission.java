package uk.gov.pmrv.api.reporting.domain.verification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ReportableEmission {

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal reportableEmissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportableEmission that = (ReportableEmission) o;

        return ObjectUtils.compare(reportableEmissions, that.reportableEmissions) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), reportableEmissions);
    }
}
