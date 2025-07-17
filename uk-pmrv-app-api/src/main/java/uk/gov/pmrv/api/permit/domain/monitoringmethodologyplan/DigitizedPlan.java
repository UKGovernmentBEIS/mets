package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows.EnergyFlows;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription.DigitizedMmpInstallationDescription;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.methodtasks.MethodTask;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.procedures.Procedure;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.procedures.ProcedureType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DigitizedPlan {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Valid
    private DigitizedMmpInstallationDescription installationDescription;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max = 17)
    private List<@Valid @NotNull SubInstallation> subInstallations = new ArrayList<>();

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotNull
    private MethodTask methodTask;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(min=4, max=4)
    private Map<ProcedureType, @Valid Procedure> procedures = new HashMap<>();

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EnergyFlows energyFlows;

    @JsonIgnore
    public Set<UUID> getAttachmentIds() {

        Set<UUID> attachments = new HashSet<>();
        if (ObjectUtils.isNotEmpty(installationDescription) && ObjectUtils.isNotEmpty(installationDescription.getFlowDiagrams())) {
            attachments.addAll(installationDescription.getFlowDiagrams());
        }

        if (ObjectUtils.isNotEmpty(subInstallations)) {
            for (SubInstallation subInstallation : subInstallations) {
                attachments.addAll(subInstallation.getSupportingFiles());

                if (ObjectUtils.isNotEmpty(subInstallation.getAnnualLevel()))
                    attachments.addAll(subInstallation.getAnnualLevel().getSupportingFiles());

                if (ObjectUtils.isNotEmpty(subInstallation.getFuelAndElectricityExchangeability()))
                    attachments.addAll(subInstallation.getFuelAndElectricityExchangeability().getSupportingFiles());

                if (ObjectUtils.isNotEmpty(subInstallation.getImportedMeasurableHeatFlow()) && subInstallation.getImportedMeasurableHeatFlow().isExist())
                    attachments.addAll(subInstallation.getImportedMeasurableHeatFlow().getSupportingFiles());

                if (ObjectUtils.isNotEmpty(subInstallation.getDirectlyAttributableEmissions()))
                    attachments.addAll(subInstallation.getDirectlyAttributableEmissions().getSupportingFiles());

                if (ObjectUtils.isNotEmpty(subInstallation.getFuelInputAndRelevantEmissionFactor()))
                    attachments.addAll(subInstallation.getFuelInputAndRelevantEmissionFactor().getSupportingFiles());

                if (ObjectUtils.isNotEmpty(subInstallation.getImportedExportedMeasurableHeat()))
                    attachments.addAll(subInstallation.getImportedExportedMeasurableHeat().getSupportingFiles());

                if (ObjectUtils.isNotEmpty(subInstallation.getWasteGasBalance()))
                    attachments.addAll(subInstallation.getWasteGasBalance().getSupportingFiles());

                if (ObjectUtils.isNotEmpty(subInstallation.getSpecialProduct()))
                    attachments.addAll(subInstallation.getSpecialProduct().getSupportingFiles());

                if (ObjectUtils.isNotEmpty(subInstallation.getMeasurableHeat())){
                    if(ObjectUtils.isNotEmpty(subInstallation.getMeasurableHeat().getMeasurableHeatProduced()))
                        attachments.addAll(subInstallation.getMeasurableHeat().getMeasurableHeatProduced().getSupportingFiles());
                    if(ObjectUtils.isNotEmpty(subInstallation.getMeasurableHeat().getMeasurableHeatImported()))
                        attachments.addAll(subInstallation.getMeasurableHeat().getMeasurableHeatImported().getSupportingFiles());
                    if(ObjectUtils.isNotEmpty(subInstallation.getMeasurableHeat().getMeasurableHeatExported()))
                        attachments.addAll(subInstallation.getMeasurableHeat().getMeasurableHeatExported().getSupportingFiles());
                }
            }
        }

        if (ObjectUtils.isNotEmpty(energyFlows)) {
            if(ObjectUtils.isNotEmpty(energyFlows.getFuelInputFlows())) {
                attachments.addAll(energyFlows.getFuelInputFlows().getSupportingFiles());
            }
            if(ObjectUtils.isNotEmpty(energyFlows.getMeasurableHeatFlows())) {
                attachments.addAll(energyFlows.getMeasurableHeatFlows().getSupportingFiles());
            }
            if(ObjectUtils.isNotEmpty(energyFlows.getWasteGasFlows())) {
                attachments.addAll(energyFlows.getWasteGasFlows().getSupportingFiles());
            }
            if(ObjectUtils.isNotEmpty(energyFlows.getElectricityFlows())) {
                attachments.addAll(energyFlows.getElectricityFlows().getSupportingFiles());
            }
        }

        return Collections.unmodifiableSet(attachments);
    }
}
