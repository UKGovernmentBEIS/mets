package uk.gov.pmrv.api.integration.registry.accountcreated.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;

@Getter
@AllArgsConstructor
public enum RegistryRegulatedActivityType {

    // waste
    //TODO:Redefine the TBC
    WASTE("TBC", RegulatedActivityType.WASTE),

    // combustion
    COMBUSTION("Combustion of fuels", RegulatedActivityType.COMBUSTION),
    //TODO:Redefine the TBC
    UPSTREAM_GHG_REMOVAL("TBC", RegulatedActivityType.UPSTREAM_GHG_REMOVAL),

    // refining
    MINERAL_OIL_REFINING("Refining of mineral oil", RegulatedActivityType.MINERAL_OIL_REFINING),

    // metals
    COKE_PRODUCTION("Production of coke", RegulatedActivityType.COKE_PRODUCTION),
    ORE_ROASTING_OR_SINTERING("Metal ore (including sulphide ore) roasting or sintering, including pelletisation",
            RegulatedActivityType.ORE_ROASTING_OR_SINTERING),
    PIG_IRON_STEEL_PRODUCTION("Production of pig iron or steel",
            RegulatedActivityType.PIG_IRON_STEEL_PRODUCTION),
    FERROUS_METALS_PRODUCTION("Production or processing of ferrous metals (including ferro-alloys)",
            RegulatedActivityType.FERROUS_METALS_PRODUCTION),
    NON_FERROUS_METALS_PRODUCTION("Production or processing of non-ferrous metals",
            RegulatedActivityType.NON_FERROUS_METALS_PRODUCTION),
    PRIMARY_ALUMINIUM_PRODUCTION("Production of primary aluminium",
            RegulatedActivityType.PRIMARY_ALUMINIUM_PRODUCTION),
    SECONDARY_ALUMINIUM_PRODUCTION("Production of secondary aluminium",
            RegulatedActivityType.SECONDARY_ALUMINIUM_PRODUCTION),

    // minerals
    CEMENT_CLINKER_PRODUCTION("Production of cement clinker in rotary kilns",
            RegulatedActivityType.CEMENT_CLINKER_PRODUCTION),
    LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE("Production of lime or calcination of dolomite or magnesite",
            RegulatedActivityType.LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE),
    CERAMICS_MANUFACTURING("Manufacture of ceramic products by firing",
            RegulatedActivityType.CERAMICS_MANUFACTURING),
    GYPSUM_OR_PLASTERBOARD_PRODUCTION("Drying or calcination of gypsum or production of plaster boards and other gypsum products",
            RegulatedActivityType.GYPSUM_OR_PLASTERBOARD_PRODUCTION),

    // glass and mineral wool
    GLASS_MANUFACTURING("Manufacture of glass including glass fibre",
            RegulatedActivityType.GLASS_MANUFACTURING),
    MINERAL_WOOL_MANUFACTURING("Manufacture of mineral wool insulation material",
            RegulatedActivityType.MINERAL_WOOL_MANUFACTURING),

    // pulp and paper
    PULP_PRODUCTION("Production of pulp from timber or other fibrous materials",
            RegulatedActivityType.PULP_PRODUCTION),
    PAPER_OR_CARDBOARD_PRODUCTION("Production of paper or cardboard",
            RegulatedActivityType.PAPER_OR_CARDBOARD_PRODUCTION),

    // chemicals
    CARBON_BLACK_PRODUCTION("Production of carbon black involving the carbonisation of organic substances",
            RegulatedActivityType.CARBON_BLACK_PRODUCTION),
    BULK_ORGANIC_CHEMICAL_PRODUCTION("Production of bulk organic chemicals",
            RegulatedActivityType.BULK_ORGANIC_CHEMICAL_PRODUCTION),
    GLYOXAL_GLYOXYLIC_ACID_PRODUCTION("Production of glyoxal and glyoxylic acid",
            RegulatedActivityType.GLYOXAL_GLYOXYLIC_ACID_PRODUCTION),
    NITRIC_ACID_PRODUCTION("Production of nitric acid",
            RegulatedActivityType.NITRIC_ACID_PRODUCTION),
    ADIPIC_ACID_PRODUCTION("Production of adipic acid",
            RegulatedActivityType.ADIPIC_ACID_PRODUCTION),
    AMMONIA_PRODUCTION("Production of ammonia",
            RegulatedActivityType.AMMONIA_PRODUCTION),
    SODA_ASH_AND_SODIUM_BICARBONATE_PRODUCTION("Production of soda ash (Na₂CO₃) and sodium bicarbonate (NaHCO₃)",
            RegulatedActivityType.SODA_ASH_AND_SODIUM_BICARBONATE_PRODUCTION),
    HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION("Production of hydrogen (H₂) and synthesis gas",
            RegulatedActivityType.HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION),

    // carbon capture and storage
    CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE("Capture of greenhouse gases from other installations",
            RegulatedActivityType.CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE),
    TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE("Transport of greenhouse gases by pipelines",
            RegulatedActivityType.TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE),
    STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE("Geological storage of greenhouse gases",
            RegulatedActivityType.STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE);

    private final String description;              // every third line from your table
    private final RegulatedActivityType activityType;   // reference to your existing enum

    public static String getByRegulatedActivityType(RegulatedActivityType type) {
        for (RegistryRegulatedActivityType option : RegistryRegulatedActivityType.values()) {
            if (option.getActivityType().equals(type)) {
                return option.getDescription();
            }
        }
        throw new IllegalArgumentException("No description for type: " + type);
    }
}
