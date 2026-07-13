package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SubInstallationType {

    // Product Benchmark types
    PRIMARY_ALUMINIUM(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "[Primary] Aluminium", true, SubInstallationValidityPeriod.ALWAYS),
    ADIPIC_ACID(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Adipic acid", false, SubInstallationValidityPeriod.ALWAYS),
    AMMONIA(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "Ammonia", true, SubInstallationValidityPeriod.ALWAYS),
    AROMATICS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, true, "Aromatics", false, SubInstallationValidityPeriod.ALWAYS),
    BOTTLES_AND_JARS_OF_COLOURED_GLASS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Bottles and jars of coloured glass", false, SubInstallationValidityPeriod.ALWAYS),
    BOTTLES_AND_JARS_OF_COLOURLESS_GLASS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Bottles and jars of colourless glass", false, SubInstallationValidityPeriod.ALWAYS),
    CARBON_BLACK(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "Carbon black", false, SubInstallationValidityPeriod.ALWAYS),
    COATED_CARTON_BOARD(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Coated carton board", false, SubInstallationValidityPeriod.ALWAYS),
    COATED_FINE_PAPER(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Coated fine paper", false, SubInstallationValidityPeriod.ALWAYS),
    COKE(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Coke", false, SubInstallationValidityPeriod.ALWAYS),
    CONTINUOUS_FILAMENT_GLASS_FIBRE_PRODUCTS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Continuous filament glass fibre products", false, SubInstallationValidityPeriod.ALWAYS),
    DOLIME(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, true, "Dolime", false, SubInstallationValidityPeriod.ALWAYS),
    DRIED_SECONDARY_GYPSUM(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Dried secondary gypsum", false, SubInstallationValidityPeriod.ALWAYS),
    EAF_CARBON_STEEL(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "EAF carbon steel", true, SubInstallationValidityPeriod.ALWAYS),
    EAF_HIGH_ALLOY_STEEL(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "EAF high alloy steel", true, SubInstallationValidityPeriod.ALWAYS),
    E_PVC(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "E-PVC", false, SubInstallationValidityPeriod.ALWAYS),
    ETHYLENE_OXIDE_ETHYLENE_GLYCOLS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, true, "Ethylene oxide/ ethylene glycols", false, SubInstallationValidityPeriod.ALWAYS),
    FACING_BRICKS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Facing bricks", false, SubInstallationValidityPeriod.ALWAYS),
    FLOAT_GLASS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Float glass", false, SubInstallationValidityPeriod.ALWAYS),
    GREY_CEMENT_CLINKER(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Grey cement clinker", true, SubInstallationValidityPeriod.ALWAYS),
    HOT_METAL(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Hot metal", true, SubInstallationValidityPeriod.ALWAYS),
    HYDROGEN(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, true, "Hydrogen", false, SubInstallationValidityPeriod.UNTIL_12_2026),
    HYDROGEN_CBAM(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, true, "Hydrogen cbam", true, SubInstallationValidityPeriod.FROM_01_2027),
    HYDROGEN_NON_CBAM(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, true, "Hydrogen non cbam", false, SubInstallationValidityPeriod.FROM_01_2027),
    IRON_CASTING(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "Iron casting", false, SubInstallationValidityPeriod.UNTIL_12_2026),
    IRON_CASTING_CBAM(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "Iron casting cbam", true, SubInstallationValidityPeriod.FROM_01_2027),
    IRON_CASTING_NON_CBAM(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "Iron casting non cbam", false, SubInstallationValidityPeriod.FROM_01_2027),
    LIME(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, true, "Lime", false, SubInstallationValidityPeriod.ALWAYS),
    LONG_FIBRE_KRAFT_PULP(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Long fibre kraft pulp", false, SubInstallationValidityPeriod.ALWAYS),
    MINERAL_WOOL(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "Mineral wool", false, SubInstallationValidityPeriod.ALWAYS),
    NEWSPRINT(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Newsprint", false, SubInstallationValidityPeriod.ALWAYS),
    NITRIC_ACID(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Nitric acid", true, SubInstallationValidityPeriod.ALWAYS),
    PAVERS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Pavers", false, SubInstallationValidityPeriod.ALWAYS),
    PHENOL_ACETONE(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Phenol/ acetone", false, SubInstallationValidityPeriod.ALWAYS),
    PLASTER(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Plaster", false, SubInstallationValidityPeriod.ALWAYS),
    PLASTERBOARD(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.NOT_EXPOSED, false, false, "Plasterboard", false, SubInstallationValidityPeriod.ALWAYS),
    PRE_BAKE_ANODE(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Pre-bake anode", false, SubInstallationValidityPeriod.ALWAYS),
    RECOVERED_PAPER_PULP(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Recovered paper pulp", false, SubInstallationValidityPeriod.ALWAYS),
    REFINERY_PRODUCTS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, true, "Refinery products", false, SubInstallationValidityPeriod.ALWAYS),
    ROOF_TILES(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Roof tiles", false, SubInstallationValidityPeriod.ALWAYS),
    SHORT_FIBRE_KRAFT_PULP(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Short fibre kraft pulp", false, SubInstallationValidityPeriod.ALWAYS),
    SINTERED_DOLIME(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Sintered dolime", false, SubInstallationValidityPeriod.ALWAYS),
    SINTERED_ORE(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Sintered ore", true, SubInstallationValidityPeriod.ALWAYS),
    SODA_ASH(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Soda ash", false, SubInstallationValidityPeriod.ALWAYS),
    SPRAY_DRIED_POWDER(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Spray dried powder", false, SubInstallationValidityPeriod.ALWAYS),
    S_PVC(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "S-PVC", false, SubInstallationValidityPeriod.ALWAYS),
    STEAM_CRACKING(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, true, "Steam cracking", false, SubInstallationValidityPeriod.ALWAYS),
    STYRENE(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, false, "Styrene", false, SubInstallationValidityPeriod.ALWAYS),
    SULPHITE_PULP_THERMO_MECHANICAL_AND_MECHANICAL_PULP(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Sulphite pulp, thermo-mechanical and mechanical pulp", false, SubInstallationValidityPeriod.ALWAYS),
    SYNTHESIS_GAS(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, true, true, "Synthesis Gas", false, SubInstallationValidityPeriod.ALWAYS),
    TESTLINER_AND_FLUTING(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Testliner and fluting", false, SubInstallationValidityPeriod.ALWAYS),
    TISSUE(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Tissue", false, SubInstallationValidityPeriod.ALWAYS),
    UNCOATED_CARTON_BOARD(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Uncoated carton board", false, SubInstallationValidityPeriod.ALWAYS),
    UNCOATED_FINE_PAPER(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "Uncoated fine paper", false, SubInstallationValidityPeriod.ALWAYS),
    VINYL_CHLORIDE_MONOMER(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, true, "Vinyl chloride monomer", false, SubInstallationValidityPeriod.ALWAYS),
    WHITE_CEMENT_CLINKER(SubInstallationCategory.PRODUCT_BENCHMARK, SubInstallationCarbonLeakage.EXPOSED, false, false, "White cement clinker", true, SubInstallationValidityPeriod.ALWAYS),

    // Fallback Approach types
    HEAT_BENCHMARK_CL(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.EXPOSED, false, false, "Heat benchmark sub-installation, CL", false, SubInstallationValidityPeriod.UNTIL_12_2026),
    HEAT_BENCHMARK_CL_CBAM(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.EXPOSED, false, false, "Heat benchmark sub-installation, CL cbam", true, SubInstallationValidityPeriod.FROM_01_2027),
    HEAT_BENCHMARK_CL_NON_CBAM(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.EXPOSED, false, false, "Heat benchmark sub-installation, CL non cbam", false, SubInstallationValidityPeriod.FROM_01_2027),
    HEAT_BENCHMARK_NON_CL(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.NOT_EXPOSED, false, false, "Heat benchmark sub-installation, non-CL", false, SubInstallationValidityPeriod.ALWAYS),
    DISTRICT_HEATING_NON_CL(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.NOT_EXPOSED, false, false, "District Heating sub-installation, non-CL", false, SubInstallationValidityPeriod.ALWAYS),
    FUEL_BENCHMARK_CL(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.EXPOSED, false, false, "Fuel benchmark sub-installation, CL", false, SubInstallationValidityPeriod.UNTIL_12_2026),
    FUEL_BENCHMARK_CL_CBAM(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.EXPOSED, false, false, "Fuel benchmark sub-installation, CL cbam", true, SubInstallationValidityPeriod.FROM_01_2027),
    FUEL_BENCHMARK_CL_NON_CBAM(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.EXPOSED, false, false, "Fuel benchmark sub-installation, CL non cbam", false, SubInstallationValidityPeriod.FROM_01_2027),
    FUEL_BENCHMARK_NON_CL(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.NOT_EXPOSED, false, false, "Fuel benchmark sub-installation, non-CL", false, SubInstallationValidityPeriod.ALWAYS),
    PROCESS_EMISSIONS_CL(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.EXPOSED, false, false, "Process emissions sub-installation, CL", false, SubInstallationValidityPeriod.UNTIL_12_2026),
    PROCESS_EMISSIONS_CL_CBAM(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.EXPOSED, false, false, "Process emissions sub-installation, CL cbam", true, SubInstallationValidityPeriod.FROM_01_2027),
    PROCESS_EMISSIONS_CL_NON_CBAM(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.EXPOSED, false, false, "Process emissions sub-installation, CL non cbam", false, SubInstallationValidityPeriod.FROM_01_2027),
    PROCESS_EMISSIONS_NON_CL(SubInstallationCategory.FALLBACK_APPROACH, SubInstallationCarbonLeakage.NOT_EXPOSED, false, false, "Process emissions sub-installation, non-CL", false, SubInstallationValidityPeriod.ALWAYS);

    private final SubInstallationCategory category;
    private final SubInstallationCarbonLeakage carbonLeakage;
    private final boolean isFuelElectricityExchangeable;
    private final boolean hasSpecialProduct;
    private final String description;
    private final boolean isCoveredByUKCBAM;
    @NotNull
    private final SubInstallationValidityPeriod validityPeriod;


    public static SubInstallationType getByValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}
