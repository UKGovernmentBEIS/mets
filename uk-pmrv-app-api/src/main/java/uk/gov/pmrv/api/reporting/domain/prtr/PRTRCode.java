package uk.gov.pmrv.api.reporting.domain.prtr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PRTRCode {

    _1_A_MINERAL_OIL_GAS_REFINERIES("1.A Mineral oil and gas refineries"),
    _1_B_INSTALLATIONS_FOR_GASIFICATION_LIGUEFACTION("1.B Installations for gasification and liquefaction"),
    _1_C_THERMAL_POWER_STATIONS_OTHER_COMBUSTION_INSTALLATIONS("1.C Thermal power stations and other combustion installations"),
    _1_D_COKE_OVENS("1.D Coke ovens"),
    _1_E_COAL_ROLLING_MILLS("1.E Coal rolling mills"),
    _1_F_INSTALLATIONS_MANUFACTURE_OF_COAL_PRODUCTS_SOLID_SMOKELESS_FUEL("1.F Installations for the manufacture of coal products and solid smokeless fuel"),

    _2_A_METAL_ORE_ROASTING_OR_SINTERING_INSTALLATIONS("2.A Metal ore (including sulfide ore) roasting or sintering installations"),
    _2_B_INSTALLATIONS_FOR_PRODUCTION_OF_PIG_IRON_OR_STEEL("2.B Installations for the production of pig iron or steel (primary or secondary melting) including continuous casting"),
    _2_C_1_HOT_ROLLING_MILLS("2.C.1 hot-rolling mills"),
    _2_C_2_SMITHERIES_WITH_HAMMERS("2.C.2 smitheries with hammers"),
    _2_C_3_APPLICATION_OF_PROTECTIVE_FUSED_METAL_COATS("2.C.3 application of protective fused metal coats"),
    _2_D_FERROUS_METAL_FOUNDRIES_WITH_PRODUCTION_CAPACITY_OF_20_TONNES_PER_DAY("2.D Ferrous metal foundries with a production capacity of 20 tonnes per day"),
    _2_E_1_PRODUCTION_OF_NON_FERROUS_CRUDE_METALS("2.E.1 for the production of non-ferrous crude metals from ore, concentrates or secondary raw materials by metallurgical, chemical, or electrolytic processes"),
    _2_E_2_SMELTING_OF_NON_FERROUS_METALS("2.E.2 for the smelting, including the alloying, of non-ferrous metals, including recovered products (refining, foundry casting and so on)"),
    _2_F_INSTALLATIONS_FOR_SURFACE_TREATMENT_OF_METALS_AND_PLASTIC_MATERIALS("2.F Installations for surface treatment of metals and plastic materials using an electrolytic or chemical process"),

    _3_A_UNDERGROUND_MINING_AND_RELATED_OPERATIONS("3.A Underground mining and related operations"),
    _3_B_OPENCAST_MINING_AND_QUARRYING("3.B Opencast mining and quarrying"),
    _3_C_1_CEMENT_CLINKER_IN_ROTARY_KILNS("3.C.1 cement clinker in rotary kilns"),
    _3_C_2_LIME_IN_ROTARY_KILNS("3.C.2 lime in rotary kilns"),
    _3_C_3_CEMENT_CLINKER_OR_LIME_IN_OTHER_FURNACES("3.C.3 cement clinker or lime in other furnaces"),
    _3_D_INSTALLATIONS_FOR_THE_PRODUCTION_OF_ASBESTOS_AND_MANUFACTURE_OF_ASBESTOS_BASED_PRODUCTS("3.D Installations for the production of asbestos and the manufacture of asbestos-based products"),
    _3_E_INSTALLATIONS_FOR_THE_MANUFACTURE_OF_GLASS_INCLUDING_GLASS_FIBRE("3.E Installations for the manufacture of glass including glass fibre"),
    _3_F_INSTALLATIONS_FOR_MELTING_MINERAL_SUBSTANCES("3.F Installations for melting mineral substances including the production of mineral fibres"),
    _3_G_INSTALLATIONS_FOR_THE_MANUFACTURE_OF_CERAMIC_PRODUCTS("3.G Installations for the manufacture of ceramic products by firing, in particular roofing tiles, bricks, refractory bricks, tiles, stoneware or porcelain"),

    _4_A_1_SIMPLE_HYDROCARBONS("4.A.1 simple hydrocarbons"),
    _4_A_2_OXYGEN_CONTAINING_HYDROCARBONS("4.A.2 oxygen-containing hydrocarbons"),
    _4_A_3_SULFUROUS_HYDROCARBONS("4.A.3 sulfurous hydrocarbons"),
    _4_A_4_NITROGENOUS_HYDROCARBONS("4.A.4 nitrogenous hydrocarbons"),
    _4_A_5_PHOSPHOROUS_HYDROCARBONS("4.A.5 phosphorus-containing hydrocarbons"),
    _4_A_6_HALOGENIC_HYDROCARBONS("4.A.6 halogenic hydrocarbons"),
    _4_A_7_ORGANOMETALLIC_COMPOUNDS("4.A.7 organometallic compounds"),
    _4_A_8_BASIC_PLASTIC_MATERIALS("4.A.8 basic plastic materials"),
    _4_A_9_SYNTHETIC_RUBBERS("4.A.9 synthetic rubbers"),
    _4_A_10_DYES_AND_PIGMENTS("4.A.10 dyes and pigments"),
    _4_A_11_SURFACE_ACTIVE_AGENTS_AND_SURFACTANS("4.A.11 surface-active agents and surfactants"),
    _4_B_1_GASES("4.B.1 gases"),
    _4_B_2_ACIDS("4.B.2 acids"),
    _4_B_3_BASES("4.B.3 bases"),
    _4_B_4_SALTS("4.B.4 salts"),
    _4_B_5_NON_METALS_METAL_OXIDES("4.B.5 non-metals, metal oxides or other inorganic compounds"),
    _4_C_CHEMICAL_INSTALLATIONS_FOR_PRODUCTION_ON_INDUSTRIAL_SCALE_OF_PHOSPHOROUS("4.C Chemical installations for the production on an industrial scale of phosphorous-, nitrogen- or potassium-based fertilisers"),
    _4_D_CHEMICAL_INSTALLATIONS_FOR_PRODUCTION_ON_INDUSTRIAL_SCALE_OF_BASIC_PLANT_HEALTH("4.D Chemical installations for the production on an industrial scale of basic plant health products and of biocides"),
    _4_E_INSTALLATIONS_USING_CHEMICAL_OR_BIOLOGICAL_PROCESS("4.E Installations using a chemical or biological process for the production on an industrial scale of basic pharmaceutical products"),
    _4_F_INSTALLATIONS_FOR_PRODUCTION_ON_INDUSTRIAL_SCALE_OF_EXPLOSIVES("Installations for the production on an industrial scale of explosives and pyrotechnic products"),

    _5_A_INSTALLATIONS_FOR_THE_RECOVERY_OR_DISPOSAL_OF_HAZARDOUS_WASTE("5.A Installations for the recovery or disposal of hazardous waste"),
    _5_B_INSTALLATIONS_FOR_THE_INCINERATION_OF_NON_HAZARDOUS_WASTE("5.B Installations for the incineration of non-hazardous waste"),
    _5_C_INSTALLATIONS_FOR_THE_DISPOSAL_OF_NON_HAZARDOUS_WASTE("5.C Installations for the disposal of non-hazardous waste"),
    _5_D_LANDFILLS("5.D Landfills"),
    _5_E_INSTALLATIONS_FOR_DISPOSAL_OR_RECYCLING_OF_ANIMAL_CARCASSES("5.E Installations for the disposal or recycling of animal carcasses and animal waste"),
    _5_F_URBAN_WASTE_WATER_TREATMENT_PLANTS("5.F Urban waste-water treatment plants"),
    _5_G_INDEPENDENTLY_OPERATED_INDUSTRIAL_WASTE_WATER_TREATMENT("5.G Independently operated industrial waste-water treatment plants which serve one or more activities of this annex"),

    _6_A_INDUSTRIAL_PLANTS_FOR_PRODUCTION_OF_PULP_FROM_WOOD_OR_OTHER_FIBROUS("6.A Industrial plants for the production of pulp from wood or other fibrous materials"),
    _6_B_INDUSTRIAL_PLANTS_FOR_PRODUCTION_OF_PAPER_AND_BOARD("6.B Industrial plants for the production of paper and board and other primary wood products (such as chipboard, fibreboard and plywood)"),
    _6_C_INDUSTRIAL_PLANTS_FOR_PRESERVATION_OF_WOOD("6.C Industrial plants for the preservation of wood and wood products with chemicals"),

    _7_A_1_WITH_40000_PLACES_FOR_POULTRY("7.A.1 with 40,000 places for poultry"),
    _7_A_2_WITH_2000_PLACES_FOR_PRODUCTION_PIGS("7.A.2 with 2,000 places for production pigs (over 30 kg)"),
    _7_A_3_WITH_750_PLACES_FOR_SOWS("7.A.3 with 750 places for sows"),
    _7_B_INTENSIVE_AQUACULTURE("7.B Intensive aquaculture"),

    _8_A_SLAGHTERHOUSES("8.A Slaughterhouses"),
    _8_B_1_ANIMAL_RAW_MATERIALS("8.B.1 animal raw materials (other than milk)"),
    _8_B_2_VEGETABLE_RAW_MATERIALS("8.B.2 vegetable raw materials"),
    _8_C_TREATMENT_AND_PROCESSING_OF_MILK("8.C Treatment and processing of milk"),

    _9_A_PLANTS_FOR_THE_PRETREATMENT("9.A Plants for the pre-treatment (operations such as washing, bleaching, mercerisation) or dyeing of fibres or textiles"),
    _9_B_PLANTS_FOR_THE_TANNING("9.B Plants for the tanning of hides and skins"),
    _9_C_INSTALLATIONS_FOR_THE_SURFACE_TREATMENT("9.C Installations for the surface treatment of substances, objects or products using organic solvents, in particular for dressing, printing, coating, degreasing, waterproofing, sizing, painting, cleaning, or impregnating"),
    _9_D_INSTALLATIONS_FOR_THE_PRODUCTION_OF_CARBON("9.D Installations for the production of carbon (hard-burnt coal) or electro-graphite by means of incineration or graphitisation"),
    _9_E_INSTALLATIONS_FOR_THE_BUILDING_AND_PAINTING_OR_REMOVAL_OF_PAINT("9.B Installations for the building of and painting or removal of paint from ships");

    private final String description;
}
