package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationHierarchicalOrder;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.AnnualActivityFuelLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.AnnualActivityHeatLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.AnnualActivityProcessLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.anualproductionlevels.AnnualProductionLevel;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = AnnualProductionLevel.class, value = "PRODUCTION"),
                @DiscriminatorMapping(schema = AnnualActivityHeatLevel.class, value = "ACTIVITY_HEAT"),
                @DiscriminatorMapping(schema = AnnualActivityFuelLevel.class, value = "ACTIVITY_FUEL"),
                @DiscriminatorMapping(schema = AnnualActivityProcessLevel.class, value = "ACTIVITY_PROCESS")
        },
        discriminatorProperty = "annualLevelType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "annualLevelType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value =  AnnualProductionLevel.class, name = "PRODUCTION"),
        @JsonSubTypes.Type(value =  AnnualActivityHeatLevel.class, name = "ACTIVITY_HEAT"),
        @JsonSubTypes.Type(value =  AnnualActivityFuelLevel.class, name = "ACTIVITY_FUEL"),
        @JsonSubTypes.Type(value =  AnnualActivityProcessLevel.class, name = "ACTIVITY_PROCESS")
})
@SpELExpression(
        expression = "{" +
                "#hierarchicalOrder != null && " +
                "(#annualLevelType == 'PRODUCTION' || " +
                "#annualLevelType == 'ACTIVITY_HEAT' || " +
                "#annualLevelType == 'ACTIVITY_FUEL') || " +
                "(#hierarchicalOrder == null && " +
                "#annualLevelType == 'ACTIVITY_PROCESS')" +
                "}",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.annuallevel.invalid_input_hierarchical_order"
)
public abstract class AnnualLevel {

    @NotNull
    private AnnualLevelType annualLevelType;

    @NotBlank
    @Size(max = 10000)
    private String methodologyAppliedDescription;

    @Valid
    private SubInstallationHierarchicalOrder hierarchicalOrder;

    @NotBlank
    @Size(max = 10000)
    private String trackingMethodologyDescription;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();

}
