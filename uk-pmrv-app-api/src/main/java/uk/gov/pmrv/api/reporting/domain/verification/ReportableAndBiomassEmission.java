package uk.gov.pmrv.api.reporting.domain.verification;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ReportableAndBiomassEmission extends ReportableEmission {

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal sustainableBiomass;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportableAndBiomassEmission that = (ReportableAndBiomassEmission) o;

        return ObjectUtils.compare(sustainableBiomass, that.sustainableBiomass) == 0
                && super.equals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sustainableBiomass);
    }
}
