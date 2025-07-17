package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct;

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
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationHierarchicalOrder;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.aromatics.AromaticsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.dolime.DolimeSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.ethyleneoxideethyleneglycols.EthyleneOxideEthyleneGlycolsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen.HydrogenSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.lime.LimeSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.refineryproducts.RefineryProductsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.steamcracking.SteamCrackingSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.synthesisgas.SynthesisGasSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.vinylchloridemonomer.VinylChlorideMonomerSP;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = RefineryProductsSP.class, value = "REFINERY_PRODUCTS"),
                @DiscriminatorMapping(schema = LimeSP.class, value = "LIME"),
                @DiscriminatorMapping(schema = DolimeSP.class, value = "DOLIME"),
                @DiscriminatorMapping(schema = SteamCrackingSP.class, value = "STEAM_CRACKING"),
                @DiscriminatorMapping(schema = AromaticsSP.class, value = "AROMATICS"),
                @DiscriminatorMapping(schema = HydrogenSP.class, value = "HYDROGEN"),
                @DiscriminatorMapping(schema = SynthesisGasSP.class, value = "SYNTHESIS_GAS"),
                @DiscriminatorMapping(schema = EthyleneOxideEthyleneGlycolsSP.class, value = "ETHYLENE_OXIDE_ETHYLENE_GLYCOLS"),
                @DiscriminatorMapping(schema = VinylChlorideMonomerSP.class, value = "VINYL_CHLORIDE_MONOMER")
        },
        discriminatorProperty = "specialProductType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "specialProductType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value =  RefineryProductsSP.class, name = "REFINERY_PRODUCTS"),
        @JsonSubTypes.Type(value =  LimeSP.class, name = "LIME"),
        @JsonSubTypes.Type(value =  DolimeSP.class, name = "DOLIME"),
        @JsonSubTypes.Type(value =  SteamCrackingSP.class, name = "STEAM_CRACKING"),
        @JsonSubTypes.Type(value =  AromaticsSP.class, name = "AROMATICS"),
        @JsonSubTypes.Type(value =  HydrogenSP.class, name = "HYDROGEN"),
        @JsonSubTypes.Type(value =  SynthesisGasSP.class, name = "SYNTHESIS_GAS"),
        @JsonSubTypes.Type(value =  EthyleneOxideEthyleneGlycolsSP.class, name = "ETHYLENE_OXIDE_ETHYLENE_GLYCOLS"),
        @JsonSubTypes.Type(value =  VinylChlorideMonomerSP.class, name = "VINYL_CHLORIDE_MONOMER")
})
public abstract class SpecialProduct {

    @NotNull
    private SpecialProductType specialProductType;

    @NotBlank
    @Size(max = 10000)
    private String methodologyAppliedDescription;

    @Valid
    @NotNull
    private SubInstallationHierarchicalOrder hierarchicalOrder;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();
}
