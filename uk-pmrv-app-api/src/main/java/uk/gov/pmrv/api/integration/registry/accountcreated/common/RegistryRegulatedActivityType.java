package uk.gov.pmrv.api.integration.registry.accountcreated.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;

@Getter
@AllArgsConstructor
public enum RegistryRegulatedActivityType {

    // waste
    //TODO:Redefine the TBC
    WASTE("Waste", RegulatedActivityType.WASTE),

    // combustion
    COMBUSTION_OF_FUELS("Combustion of fuels", RegulatedActivityType.COMBUSTION),

    UPSTREAM("The upstream GHG Removal", RegulatedActivityType.UPSTREAM_GHG_REMOVAL),

    // refining
    REFINING_OF_MINERAL_OIL("Refining of mineral oil", RegulatedActivityType.MINERAL_OIL_REFINING),

    // metals
    PRODUCTION_OF_COKE("Production of coke", RegulatedActivityType.COKE_PRODUCTION),
    METAL_ORE_ROASTING_OR_SINTERING("Metal ore (including sulphide ore) roasting or sintering, including pelletisation",
            RegulatedActivityType.ORE_ROASTING_OR_SINTERING),
    PRODUCTION_OF_PIG_IRON_OR_STEEL("Production of pig iron or steel",
            RegulatedActivityType.PIG_IRON_STEEL_PRODUCTION),
    PRODUCTION_OR_PROCESSING_OF_FERROUS_METALS("Production or processing of ferrous metals (including ferro-alloys)",
            RegulatedActivityType.FERROUS_METALS_PRODUCTION),
    PRODUCTION_OR_PROCESSING_OF_NON_FERROUS_METALS("Production or processing of non-ferrous metals",
            RegulatedActivityType.NON_FERROUS_METALS_PRODUCTION),
    PRODUCTION_OF_PRIMARY_ALUMINIUM("Production of primary aluminium",
            RegulatedActivityType.PRIMARY_ALUMINIUM_PRODUCTION),
    PRODUCTION_OF_SECONDARY_ALUMINIUM("Production of secondary aluminium",
            RegulatedActivityType.SECONDARY_ALUMINIUM_PRODUCTION),

    // minerals
    PRODUCTION_OF_CEMENT_CLINKER("Production of cement clinker in rotary kilns",
            RegulatedActivityType.CEMENT_CLINKER_PRODUCTION),
    PRODUCTION_OF_LIME_OR_CALCINATION_OF_DOLOMITE_MAGNESITE("Production of lime or calcination of dolomite or magnesite",
            RegulatedActivityType.LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE),
    MANUFACTURE_OF_CERAMICS("Manufacture of ceramic products by firing",
            RegulatedActivityType.CERAMICS_MANUFACTURING),
    PRODUCTION_OR_PROCESSING_OF_GYPSUM_OR_PLASTERBOARD("Drying or calcination of gypsum or production of plaster boards and other gypsum products",
            RegulatedActivityType.GYPSUM_OR_PLASTERBOARD_PRODUCTION),

    // glass and mineral wool
    MANUFACTURE_OF_GLASS("Manufacture of glass including glass fibre",
            RegulatedActivityType.GLASS_MANUFACTURING),
    MANUFACTURE_OF_MINERAL_WOOL("Manufacture of mineral wool insulation material",
            RegulatedActivityType.MINERAL_WOOL_MANUFACTURING),

    // pulp and paper
    PRODUCTION_OF_PULP("Production of pulp from timber or other fibrous materials",
            RegulatedActivityType.PULP_PRODUCTION),
    PRODUCTION_OF_PAPER_OR_CARDBOARD("Production of paper or cardboard",
            RegulatedActivityType.PAPER_OR_CARDBOARD_PRODUCTION),

    // chemicals
    PRODUCTION_OF_CARBON_BLACK("Production of carbon black involving the carbonisation of organic substances",
            RegulatedActivityType.CARBON_BLACK_PRODUCTION),
    PRODUCTION_OF_BULK_CHEMICALS("Production of bulk organic chemicals",
            RegulatedActivityType.BULK_ORGANIC_CHEMICAL_PRODUCTION),
    PRODUCTION_OF_GLYOXAL_AND_GLYOXYLIC_ACID("Production of glyoxal and glyoxylic acid",
            RegulatedActivityType.GLYOXAL_GLYOXYLIC_ACID_PRODUCTION),
    PRODUCTION_OF_NITRIC_ACID("Production of nitric acid",
            RegulatedActivityType.NITRIC_ACID_PRODUCTION),
    PRODUCTION_OF_ADIPIC_ACID("Production of adipic acid",
            RegulatedActivityType.ADIPIC_ACID_PRODUCTION),
    PRODUCTION_OF_AMMONIA("Production of ammonia",
            RegulatedActivityType.AMMONIA_PRODUCTION),
    PRODUCTION_OF_SODA_ASH_AND_SODIUM_BICARBONATE("Production of soda ash (Na₂CO₃) and sodium bicarbonate (NaHCO₃)",
            RegulatedActivityType.SODA_ASH_AND_SODIUM_BICARBONATE_PRODUCTION),
    PRODUCTION_OF_HYDROGEN_AND_SYNTHESIS_GAS("Production of hydrogen (H₂) and synthesis gas",
            RegulatedActivityType.HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION),

    // carbon capture and storage
    CAPTURE_OF_GREENHOUSE_GASES("Capture of greenhouse gases from other installations",
            RegulatedActivityType.CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE),
    TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_2009_31_EC("Transport of greenhouse gases by pipelines",
            RegulatedActivityType.TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE),
    STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_2009_31_EC("Geological storage of greenhouse gases",
            RegulatedActivityType.STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE);

    private final String description;
    private final RegulatedActivityType activityType;

    public static String getByRegulatedActivityType(RegulatedActivityType type) {
        for (RegistryRegulatedActivityType option : RegistryRegulatedActivityType.values()) {
            if (option.getActivityType().equals(type)) {
                return option.name();
            }
        }
        throw new IllegalArgumentException("No description for type: " + type);
    }
}
