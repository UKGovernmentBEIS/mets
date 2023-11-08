package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManuallyProvidedEmissions {

    @NotNull
    private String reasonForProvidingManualEmissions;

    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    @NotNull
    private BigDecimal totalProvidedReportableEmissions;

    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal totalProvidedSustainableBiomassEmissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ManuallyProvidedEmissions that = (ManuallyProvidedEmissions) o;

        return ObjectUtils.compare(totalProvidedReportableEmissions, that.totalProvidedReportableEmissions) == 0
                && ObjectUtils.compare(totalProvidedSustainableBiomassEmissions, that.totalProvidedSustainableBiomassEmissions) == 0
                && ObjectUtils.compare(reasonForProvidingManualEmissions, that.reasonForProvidingManualEmissions) == 0;
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), totalProvidedReportableEmissions, totalProvidedSustainableBiomassEmissions, reasonForProvidingManualEmissions);
    }
}
