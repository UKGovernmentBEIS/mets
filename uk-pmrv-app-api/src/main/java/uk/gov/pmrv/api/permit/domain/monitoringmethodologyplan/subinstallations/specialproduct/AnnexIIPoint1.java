package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AnnexIIPoint1 {
    ATMOSPHERIC_CRUDE_DISTILLATION("Atmospheric Crude Distillation"),
    VACUUM_DISTILLATION("Vacuum Distillation"),
    SOLVENT_DEASPHALTING("Solvent Deasphalting"),
    VISBREAKING("Visbreaking"),
    THERMAL_CRACKING("Thermal Cracking"),
    DELAYED_COKING("Delayed Coking"),
    FLUID_COKING("Fluid Coking"),
    FLEXICOKING("Flexicoking"),
    COKE_CALCINING("Coke Calcining"),
    FLUID_CATALYTIC_CRACKING("Fluid Catalytic Cracking"),
    OTHER_CATALYTIC_CRACKING("Other Catalytic Cracking"),
    DISTILLATE_GASOIL_HYDROCRACKING("Distillate / Gasoil Hydrocracking"),
    RESIDUAL_HYDROCRACKING("Residual Hydrocracking"),
    NAPHTHA_GASOLINE_HYDROTREATING("Naphtha/Gasoline Hydrotreating"),
    KEROSENE_DIESEL_HYDROTREATING("Kerosene/ Diesel Hydrotreating"),
    RESIDUAL_HYDROTREATING("Residual Hydrotreating"),
    VGO_HYDROTREATING("VGO Hydrotreating"),
    HYDROGEN_PRODUCTION("Hydrogen Production"),
    CATALYTIC_REFORMING("Catalytic Reforming"),
    ALKYLATION("Alkylation"),
    C4_ISOMERISATION("C4 Isomerisation"),
    C5_C6_ISOMERISATION("C5/C6 Isomerisation"),
    OXYGENATE_PRODUCTION("Oxygenate Production"),
    PROPYLENE_PRODUCTION("Propylene Production"),
    ASPHALT_MANUFACTURE("Asphalt Manufacture"),
    POLYMER_MODIFIED_ASPHALT_BLENDING("Polymer-Modified Asphalt Blending"),
    SULPHUR_RECOVERY("Sulphur Recovery"),
    AROMATIC_SOLVENT_EXTRACTION("Aromatic Solvent Extraction"),
    HYDRODEALKYLATION("Hydrodealkylation"),
    TDP_TDA("TDP/ TDA"),
    CYCLOHEXANE_PRODUCTION("Cyclohexane production"),
    XYLENE_ISOMERISATION("Xylene Isomerisation"),
    PARAXYLENE_PRODUCTION("Paraxylene production"),
    METAXYLENE_PRODUCTION("Metaxylene production"),
    PHTHALIC_ANHYDRIDE_PRODUCTION("Phthalic anhydride production"),
    MALEIC_ANHYDRIDE_PRODUCTION("Maleic anhydride production"),
    ETHYLBENZENE_PRODUCTION("Ethylbenzene production"),
    CUMENE_PRODUCTION("Cumene production"),
    PHENOL_PRODUCTION("Phenol production"),
    LUBE_SOLVENT_EXTRACTION("Lube solvent extraction"),
    LUBE_SOLVENT_DEWAXING("Lube solvent dewaxing"),
    CATALYTIC_WAX_ISOMERISATION("Catalytic Wax Isomerisation"),
    LUBE_HYDROCRACKER("Lube Hydrocracker"),
    WAX_DEOILING("Wax Deoiling"),
    LUBE_WAX_HYDROTREATING("Lube/Wax Hydrotreating"),
    SOLVENT_HYDROTREATING("Solvent Hydrotreating"),
    SOLVENT_FRACTIONATION("Solvent Fractionation"),
    MOL_SIEVE_C10_PLUS_PARAFFINS("Mol sieve for C10+ paraffins"),
    PARTIAL_OXIDATION_RESIDUAL_FEEDS_POX_FUEL("Partial Oxidation of Residual Feeds (POX) for Fuel"),
    PARTIAL_OXIDATION_RESIDUAL_FEEDS_POX_HYDROGEN_METHANOL("Partial Oxidation of Residual Feeds (POX) for Hydrogen or Methanol"),
    METHANOL_FROM_SYNGAS("Methanol from syngas"),
    AIR_SEPARATION("Air Separation"),
    FRACTIONATION_PURCHASED_NGL("Fractionation of purchased NGL"),
    FLUE_GAS_TREATMENT("Flue gas treatment"),
    TREATMENT_COMPRESSION_FUEL_GAS_SALES("Treatment and Compression of Fuel Gas for Sales"),
    SEAWATER_DESALINATION("Seawater Desalination");

    private final String description;

    public static AnnexIIPoint1 getByValue(String value) {
        return Arrays.stream(values())
                .filter(section -> section.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}
