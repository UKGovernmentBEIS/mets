package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PfcManuallyProvidedEmissions {

    @NotNull
    private String reasonForProvidingManualEmissions;

    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    @NotNull
    private BigDecimal totalProvidedReportableEmissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PfcManuallyProvidedEmissions that = (PfcManuallyProvidedEmissions) o;

        return ObjectUtils.compare(totalProvidedReportableEmissions, that.totalProvidedReportableEmissions) == 0
                && ObjectUtils.compare(reasonForProvidingManualEmissions, that.reasonForProvidingManualEmissions) == 0;
    }


    @Override
    public int hashCode() {
        return Objects.hash(totalProvidedReportableEmissions, reasonForProvidingManualEmissions);
    }
}
