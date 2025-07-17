package uk.gov.pmrv.api.permit.domain.sourcestreams;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SourceStreamType {

    //Category 1
    COMBUSTION_COMMERCIAL_STANDARD_FUELS(SourceStreamCategory.CATEGORY_1, "Combustion: Commercial standard fuels"),
    COMBUSTION_OTHER_GASEOUS_LIQUID_FUELS(SourceStreamCategory.CATEGORY_1, "Combustion: Other gaseous & liquid fuels"),
    COMBUSTION_SOLID_FUELS(SourceStreamCategory.CATEGORY_1, "Combustion: Solid fuels"),
    COMBUSTION_FLARES(SourceStreamCategory.CATEGORY_1, "Combustion: Flares"),
    OTHER(SourceStreamCategory.CATEGORY_1, "Other"),

    //Category 2
    COMBUSTION_GAS_PROCESSING_TERMINALS(SourceStreamCategory.CATEGORY_2, "Combustion: Gas Processing Terminals"),
    REFINERIES_MASS_BALANCE(SourceStreamCategory.CATEGORY_2, "Refineries: Mass balance"),
    COKE_MASS_BALANCE(SourceStreamCategory.CATEGORY_2, "Coke: Mass balance"),
    METAL_ORE_MASS_BALANCE(SourceStreamCategory.CATEGORY_2, "Metal ore: Mass balance"),
    IRON_STEEL_MASS_BALANCE(SourceStreamCategory.CATEGORY_2, "Iron & steel: Mass balance"),
    CARBON_BLACK_MASS_BALANCE_METHODOLOGY(SourceStreamCategory.CATEGORY_2, "Carbon black: Mass balance methodology"),
    HYDROGEN_AND_SYNTHESIS_GAS_MASS_BALANCE_METHODOLOGY(SourceStreamCategory.CATEGORY_2, "Hydrogen and synthesis gas: Mass balance methodology"),
    BULK_ORGANIC_CHEMICALS_MASS_BALANCE_METHODOLOGY(SourceStreamCategory.CATEGORY_2, "Bulk organic chemicals: Mass balance methodology"),
    NON_FERROUS_SEC_ALUMINIUM_MASS_BALANCE_METHODOLOGY(SourceStreamCategory.CATEGORY_2, "Non ferrous, sec. aluminium: Mass balance methodology"),
    SODA_ASH_SODIUM_BICARBONATE_MASS_BALANCE_METHODOLOGY(SourceStreamCategory.CATEGORY_2, "Soda ash / sodium bicarbonate: Mass balance methodology"),
    PRIMARY_ALUMINIUM_MASS_BALANCE_METHODOLOGY(SourceStreamCategory.CATEGORY_2, "Primary aluminium: Mass balance methodology"),

    //Category 4
    REFINERIES_HYDROGEN_PRODUCTION(SourceStreamCategory.CATEGORY_4, "Refineries: Hydrogen production"),
    CEMENT_CLINKER_CKD(SourceStreamCategory.CATEGORY_4, "Cement clinker: CKD"),
    CERAMICS_SCRUBBING(SourceStreamCategory.CATEGORY_4, "Ceramics: Scrubbing"),
    UPSTREAM_GHG_REMOVAL_VENTING_CO2(SourceStreamCategory.CATEGORY_4, "Upstream GHG removal: Venting CO2"),

    //Category 5
    REFINERIES_CATALYTIC_CRACKER_REGENERATION(SourceStreamCategory.CATEGORY_5, "Refineries: Catalytic cracker regeneration"),

    //Category 6
    COKE_FUEL_AS_PROCESS_INPUT(SourceStreamCategory.CATEGORY_6, "Coke: Fuel as process input"),
    IRON_STEEL_FUEL_AS_PROCESS_INPUT(SourceStreamCategory.CATEGORY_6, "Iron & steel: Fuel as process input"),
    AMMONIA_FUEL_AS_PROCESS_INPUT(SourceStreamCategory.CATEGORY_6, "Ammonia: Fuel as process input"),
    HYDROGEN_AND_SYNTHESIS_GAS_FUEL_AS_PROCESS_INPUT(SourceStreamCategory.CATEGORY_6, "Hydrogen and synthesis gas: Fuel as process input"),

    //Category 7
    COKE_CARBONATE_INPUT_METHOD_A(SourceStreamCategory.CATEGORY_7, "Coke: Carbonate input (Method A)"),
    COKE_OXIDE_OUTPUT_METHOD_B(SourceStreamCategory.CATEGORY_7, "Coke: Oxide output (Method B)"),
    METAL_ORE_CARBONATE_INPUT(SourceStreamCategory.CATEGORY_7, "Metal ore: Carbonate input"),
    IRON_STEEL_CARBONATE_INPUT(SourceStreamCategory.CATEGORY_7, "Iron & steel: Carbonate input"),
    CEMENT_CLINKER_KILN_INPUT_BASED_METHOD_A(SourceStreamCategory.CATEGORY_7, "Cement clinker: Kiln input based (Method A)"),
    CEMENT_CLINKER_CLINKER_OUTPUT_METHOD_B(SourceStreamCategory.CATEGORY_7, "Cement clinker: Clinker output (Method B)"),
    CEMENT_CLINKER_NON_CARBONATE_CARBON(SourceStreamCategory.CATEGORY_7, "Cement clinker: Non-carbonate carbon"),
    LIME_DOLOMITE_MAGNESITE_CARBONATES_METHOD_A(SourceStreamCategory.CATEGORY_7, "Lime / dolomite / magnesite: Carbonates (Method A)"),
    LIME_DOLOMITE_MAGNESITE_KILN_DUST_METHOD_B(SourceStreamCategory.CATEGORY_7, "Lime / dolomite / magnesite: Kiln dust (Method B)"),
    LIME_DOLOMITE_MAGNESITE_ALKALI_EARTH_OXIDE_METHOD_B(SourceStreamCategory.CATEGORY_7, "Lime / dolomite / magnesite: Alkali earth oxide (Method B)"),
    GLASS_AND_MINERAL_WOOL_CARBONATES_INPUT(SourceStreamCategory.CATEGORY_7, "Glass and mineral wool: Carbonates (input)"),
    CERAMICS_CARBON_INPUTS_METHOD_A(SourceStreamCategory.CATEGORY_7, "Ceramics: Carbon inputs (Method A)"),
    CERAMICS_ALKALI_OXIDE_METHOD_B(SourceStreamCategory.CATEGORY_7, "Ceramics: Alkali oxide (Method B)"),
    PULP_PAPER_MAKE_UP_CHEMICALS(SourceStreamCategory.CATEGORY_7, "Pulp & paper: Make up chemicals"),
    NON_FERROUS_SEC_ALUMINIUM_PROCESS_EMISSIONS(SourceStreamCategory.CATEGORY_7, "Non ferrous, sec. aluminium: Process emissions"),
    COMBUSTION_SCRUBBING_CARBONATE(SourceStreamCategory.CATEGORY_7, "Combustion: Scrubbing (carbonate)"),
    COMBUSTION_SCRUBBING_UREA(SourceStreamCategory.CATEGORY_7, "Combustion: Scrubbing (urea)"),
    COMBUSTION_SCRUBBING_GYPSUM(SourceStreamCategory.CATEGORY_7, "Combustion: Scrubbing (gypsum)"),


    //No category, workaround for PMRV-6287
    PRIMARY_ALUMINIUM_PFC(null, "Primary aluminium: PFC");


    private final SourceStreamCategory category;
    private final String description;

}
