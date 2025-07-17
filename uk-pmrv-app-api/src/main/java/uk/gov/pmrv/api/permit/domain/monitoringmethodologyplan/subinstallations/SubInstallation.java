package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.AnnualLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelandelectricityexchangeability.FuelAndElectricityExchangeability;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeat;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.MeasurableHeat;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.SpecialProduct;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.wastegasbalance.WasteGasBalance;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(
        expression = "{(T(uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType).valueOf(#subInstallationType).hasSpecialProduct and #specialProduct != null) or " +
                "(T(uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType).valueOf(#subInstallationType).hasSpecialProduct == false and #specialProduct == null)}",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.specialProduct.invalid_input_1"
)
@SpELExpression(
        expression = "((T(uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType).valueOf(#subInstallationType).category == " +
                "T(uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationCategory).PRODUCT_BENCHMARK " +
                "and #wasteGasBalance != null and #importedMeasurableHeatFlow != null and #importedExportedMeasurableHeat != null) " +
                "or (T(uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType).valueOf(#subInstallationType).category == " +
                "T(uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationCategory).FALLBACK_APPROACH " +
                "and #wasteGasBalance == null and #importedMeasurableHeatFlow == null and #importedExportedMeasurableHeat == null))",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.produceBenchmark.invalid_input"
)
public class SubInstallation {

    //Sub-installation details
    @NotBlank
    @Size(max = 255)
    private String subInstallationNo;

    @NotNull
    private SubInstallationType subInstallationType;

    @NotBlank
    @Size(max = 10000)
    private String description;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();

    @Valid
    @NotNull
    private AnnualLevel annualLevel;

    @Valid
    private FuelAndElectricityExchangeability fuelAndElectricityExchangeability;

    @Valid
    private ImportedMeasurableHeatFlow importedMeasurableHeatFlow;

    @Valid
    private DirectlyAttributableEmissions directlyAttributableEmissions;

    @Valid
    private FuelInputAndRelevantEmissionFactor fuelInputAndRelevantEmissionFactor;

    @Valid
    private ImportedExportedMeasurableHeat importedExportedMeasurableHeat;

    @Valid
    private WasteGasBalance wasteGasBalance;

    @Valid
    private SpecialProduct specialProduct;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private MeasurableHeat measurableHeat;

}
