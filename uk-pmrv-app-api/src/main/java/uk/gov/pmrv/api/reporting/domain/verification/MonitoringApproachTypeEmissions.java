package uk.gov.pmrv.api.reporting.domain.verification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonitoringApproachTypeEmissions {

    @NotNull
    @Valid
    private ReportableAndBiomassEmission calculationCombustionEmissions;

    @NotNull
    @Valid
    private ReportableAndBiomassEmission calculationProcessEmissions;

    @NotNull
    @Valid
    private ReportableAndBiomassEmission calculationMassBalanceEmissions;

    @NotNull
    @Valid
    private ReportableAndBiomassEmission calculationTransferredCO2Emissions;

    @NotNull
    @Valid
    private ReportableAndBiomassEmission measurementCO2Emissions;

    @NotNull
    @Valid
    private ReportableAndBiomassEmission measurementTransferredCO2Emissions;

    @NotNull
    @Valid
    private ReportableAndBiomassEmission measurementN2OEmissions;

    @NotNull
    @Valid
    private ReportableAndBiomassEmission measurementTransferredN2OEmissions;

    @NotNull
    @Valid
    private ReportableAndBiomassEmission fallbackEmissions;

    @NotNull
    @Valid
    private ReportableEmission calculationPFCEmissions;

    @NotNull
    @Valid
    private ReportableEmission inherentCO2Emissions;

    @JsonIgnore
    public BigDecimal getReportableEmissions() {

        final List<ReportableEmission> reportableEmissions = List.of(calculationCombustionEmissions, calculationProcessEmissions, calculationMassBalanceEmissions,
                calculationTransferredCO2Emissions, measurementCO2Emissions, measurementTransferredCO2Emissions,
                measurementN2OEmissions, measurementTransferredN2OEmissions, fallbackEmissions,
                calculationPFCEmissions, inherentCO2Emissions);

        return reportableEmissions.stream()
                .map(ReportableEmission::getReportableEmissions)
                .filter(Objects::nonNull)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

}
