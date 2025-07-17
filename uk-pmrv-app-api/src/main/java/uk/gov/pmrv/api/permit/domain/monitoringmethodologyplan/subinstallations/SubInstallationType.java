package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SubInstallationType {

    // Product Benchmark types
    PRIMARY_ALUMINIUM(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "[Primary] Aluminium"),
    ADIPIC_ACID(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Adipic acid"),
    AMMONIA(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "Ammonia"),
    AROMATICS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, true, "Aromatics"),
    BOTTLES_AND_JARS_OF_COLOURED_GLASS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Bottles and jars of coloured glass"),
    BOTTLES_AND_JARS_OF_COLOURLESS_GLASS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Bottles and jars of colourless glass"),
    CARBON_BLACK(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "Carbon black"),
    COATED_CARTON_BOARD(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Coated carton board"),
    COATED_FINE_PAPER(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Coated fine paper"),
    COKE(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Coke"),
    CONTINUOUS_FILAMENT_GLASS_FIBRE_PRODUCTS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Continuous filament glass fibre products"),
    DOLIME(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, true, "Dolime"),
    DRIED_SECONDARY_GYPSUM(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Dried secondary gypsum"),
    EAF_CARBON_STEEL(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "EAF carbon steel"),
    EAF_HIGH_ALLOY_STEEL(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "EAF high alloy steel"),
    E_PVC(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "E-PVC"),
    ETHYLENE_OXIDE_ETHYLENE_GLYCOLS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, true, "Ethylene oxide/ ethylene glycols"),
    FACING_BRICKS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Facing bricks"),
    FLOAT_GLASS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Float glass"),
    GREY_CEMENT_CLINKER(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Grey cement clinker"),
    HOT_METAL(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Hot metal"),
    HYDROGEN(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, true, "Hydrogen"),
    IRON_CASTING(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "Iron casting"),
    LIME(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, true, "Lime"),
    LONG_FIBRE_KRAFT_PULP(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Long fibre kraft pulp"),
    MINERAL_WOOL(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "Mineral wool"),
    NEWSPRINT(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Newsprint"),
    NITRIC_ACID(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Nitric acid"),
    PAVERS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Pavers"),
    PHENOL_ACETONE(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Phenol/ acetone"),
    PLASTER(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Plaster"),
    PLASTERBOARD(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.NOT_EXPOSED, false, false, "Plasterboard"),
    PRE_BAKE_ANODE(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Pre-bake anode"),
    RECOVERED_PAPER_PULP(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Recovered paper pulp"),
    REFINERY_PRODUCTS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, true, "Refinery products"),
    ROOF_TILES(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Roof tiles"),
    SHORT_FIBRE_KRAFT_PULP(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Short fibre kraft pulp"),
    SINTERED_DOLIME(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Sintered dolime"),
    SINTERED_ORE(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Sintered ore"),
    SODA_ASH(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Soda ash"),
    SPRAY_DRIED_POWDER(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Spray dried powder"),
    S_PVC(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "S-PVC"),
    STEAM_CRACKING(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, true, "Steam cracking"),
    STYRENE(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "Styrene"),
    SULPHITE_PULP_THERMO_MECHANICAL_AND_MECHANICAL_PULP(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Sulphite pulp, thermo-mechanical and mechanical pulp"),
    SYNTHESIS_GAS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, true, "Synthesis Gas"),
    TESTLINER_AND_FLUTING(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Testliner and fluting"),
    TISSUE(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Tissue"),
    UNCOATED_CARTON_BOARD(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Uncoated carton board"),
    UNCOATED_FINE_PAPER(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Uncoated fine paper"),
    VINYL_CHLORIDE_MONOMER(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, true, "Vinyl chloride monomer"),
    WHITE_CEMENT_CLINKER(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "White cement clinker"),

    // Fallback Approach types
    HEAT_BENCHMARK_CL(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.EXPOSED, false, false, "Heat benchmark sub-installation, CL"),
    HEAT_BENCHMARK_NON_CL(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.NOT_EXPOSED, false, false, "Heat benchmark sub-installation, non-CL"),
    DISTRICT_HEATING_NON_CL(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.NOT_EXPOSED, false, false, "District Heating sub-installation, non-CL"),
    FUEL_BENCHMARK_CL(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.EXPOSED, false, false, "Fuel benchmark sub-installation, CL"),
    FUEL_BENCHMARK_NON_CL(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.NOT_EXPOSED, false, false, "Fuel benchmark sub-installation, non-CL"),
    PROCESS_EMISSIONS_CL(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.EXPOSED, false, false, "Process emissions sub-installation, CL"),
    PROCESS_EMISSIONS_NON_CL(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.NOT_EXPOSED, false, false, "Process emissions sub-installation, non-CL");

    private final SubInstallationCategory category;
    private final SubInstallationCarbonLeakage carbonLeakage;
    private final boolean isFuelElectricityExchangeable;
    private final boolean hasSpecialProduct;
    private final String description;

    public static SubInstallationType getByValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}
