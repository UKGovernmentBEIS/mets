import { PRTRCodes } from 'pmrv-api';

export type activitySectionType = '_1' | '_2' | '_3' | '_4' | '_5' | '_6' | '_7' | '_8' | '_9';
export type activityIntermediateSectionType =
  | '_2_C'
  | '_2_E'
  | '_3_C'
  | '_4_A'
  | '_4_B'
  | '_7_A'
  | '_8_B'
  | '_2'
  | '_3'
  | '_4'
  | '_7'
  | '_8';

export type activityFinalStepItemType =
  | '_1_A'
  | '_1_B'
  | '_1_C'
  | '_1_D'
  | '_1_E'
  | '_1_F'
  | '_2_A'
  | '_2_B'
  | '_2_C_1'
  | '_2_C_2'
  | '_2_C_3'
  | '_2_D'
  | '_2_E_1'
  | '_2_E_2'
  | '_2_F'
  | '_3_A'
  | '_3_B'
  | '_3_C_1'
  | '_3_C_2'
  | '_3_C_3'
  | '_3_D'
  | '_3_E'
  | '_3_F'
  | '_3_G'
  | '_4_A_1'
  | '_4_A_2'
  | '_4_A_3'
  | '_4_A_4'
  | '_4_A_5'
  | '_4_A_6'
  | '_4_A_7'
  | '_4_A_8'
  | '_4_A_9'
  | '_4_A_10'
  | '_4_A_11'
  | '_4_B_1'
  | '_4_B_2'
  | '_4_B_3'
  | '_4_B_4'
  | '_4_B_5'
  | '_4_C'
  | '_4_D'
  | '_4_E'
  | '_4_F'
  | '_5_A'
  | '_5_B'
  | '_5_C'
  | '_5_D'
  | '_5_E'
  | '_5_F'
  | '_5_G'
  | '_6_A'
  | '_6_B'
  | '_6_C'
  | '_7_A_1'
  | '_7_A_2'
  | '_7_A_3'
  | '_7_B'
  | '_8_A'
  | '_8_B_1'
  | '_8_B_2'
  | '_8_C'
  | '_9_A'
  | '_9_B'
  | '_9_C'
  | '_9_D'
  | '_9_E';

export type activityItemType = activitySectionType | activityIntermediateSectionType | activityFinalStepItemType;

export const activitySections: Array<activitySectionType> = ['_1', '_2', '_3', '_4', '_5', '_6', '_7', '_8', '_9'];

export const activitiesChildSection: {
  [key: string]: (activityIntermediateSectionType | activityFinalStepItemType)[];
} = {
  _1: ['_1_A', '_1_B', '_1_C', '_1_D', '_1_E', '_1_F'],
  _2: ['_2_A', '_2_B', '_2_C', '_2_D', '_2_E', '_2_F'],
  _2_C: ['_2_C_1', '_2_C_2', '_2_C_3'],
  _2_E: ['_2_E_1', '_2_E_2'],
  _3: ['_3_A', '_3_B', '_3_C', '_3_D', '_3_E', '_3_F', '_3_G'],
  _3_C: ['_3_C_1', '_3_C_2', '_3_C_3'],
  _4: ['_4_A', '_4_B', '_4_C', '_4_D', '_4_E', '_4_F'],
  _4_A: [
    '_4_A_1',
    '_4_A_2',
    '_4_A_3',
    '_4_A_4',
    '_4_A_5',
    '_4_A_6',
    '_4_A_7',
    '_4_A_8',
    '_4_A_9',
    '_4_A_10',
    '_4_A_11',
  ],
  _4_B: ['_4_B_1', '_4_B_2', '_4_B_3', '_4_B_4', '_4_B_5'],
  _5: ['_5_A', '_5_B', '_5_C', '_5_D', '_5_E', '_5_F', '_5_G'],
  _6: ['_6_A', '_6_B', '_6_C'],
  _7: ['_7_A', '_7_B'],
  _7_A: ['_7_A_1', '_7_A_2', '_7_A_3'],
  _8: ['_8_A', '_8_B', '_8_C'],
  _8_B: ['_8_B_1', '_8_B_2'],
  _9: ['_9_A', '_9_B', '_9_C', '_9_D', '_9_E'],
};

export const intermediateStepsWithSubIntermediateSteps: activityIntermediateSectionType[] = [
  '_2',
  '_3',
  '_4',
  '_7',
  '_8',
];

export const activityItemNameMap: Record<activityItemType, string> = {
  _1: '1 Energy sector',
  _1_A: '1.(a) Mineral oil and gas refineries',
  _1_B: '1.(b) Installations for gasification and liquefaction',
  _1_C: '1.(c) Thermal power stations and other combustion installations',
  _1_D: '1.(d) Coke Ovens',
  _1_E: '1.(e) Coal rolling mills',
  _1_F: '1.(f) Installations for the manufacture of coal products and solid smokeless fuel',
  _2: '2 Production and processing of metals',
  _2_A: '2.(a) Metal ore (including sulfide ore) roasting or sintering installations',
  _2_B: '2.(b) Installations for the production of pig iron or steel (primary or secondary melting) including continuous casting',
  _2_C: '2.(c) Installations for the processing of ferrous metals',
  _2_C_1: '2.(c).(i) Installations for the processing of ferrous metals: hot-rolling mills',
  _2_C_2: '2.(c).(ii) Installations for the processing of ferrous metals: smitheries with hammers',
  _2_C_3: '2.(c).(iii) Installations for the processing of ferrous metals: application of protective fused metal coats',
  _2_D: '2.(d) Ferrous metal foundries with a production capacity of 20 tonnes per day',
  _2_E: '2.(e) Installations',
  _2_E_1:
    '2.(e).(i) Installations: for the production of non-ferrous crude metals from ore, concentrates or secondary raw materials by metallurgical, chemical, or electrolytic processes',
  _2_E_2:
    '2.(e).(ii) Installations: for the smelting, including the alloying, of non-ferrous metals, including recovered products (refining, foundry casting and so on)',
  _2_F: '2.(f) Installations for surface treatment of metals and plastic materials using an electrolytic or chemical process',
  _3: '3 Mineral industry',
  _3_A: '3.(a) Underground mining and related operations',
  _3_B: '3.(b) Opencast mining and quarrying',
  _3_C: '3.(c) Installations for the production of',
  _3_C_1: '3.(c).(i) Installations for the production of: cement clinker in rotary kilns',
  _3_C_2: '3.(c).(ii) Installations for the production of: lime in rotary kilns',
  _3_C_3: '3.(c).(iii) Installations for the production of: cement clinker or lime in other furnaces',
  _3_D: '3.(d) Installations for the production of asbestos and the manufacture of asbestos-based products',
  _3_E: '3.(e) Installations for the manufacture of glass, including glass fibre',
  _3_F: '3.(f) Installations for melting mineral substances, including the production of mineral fibres',
  _3_G: '3.(g) Installations for the manufacture of ceramic products by firing, in particular roofing tiles, bricks, refractory bricks, tiles, stoneware or porcelain',
  _4: '4 Chemical industry',
  _4_A: '4.(a) Chemical installations for the production on an industrial scale of basic organic chemicals, such as',
  _4_A_1:
    '4.(a).(i) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: simple hydrocarbons',
  _4_A_2:
    '4.(a).(ii) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: oxygen-containing hydrocarbons',
  _4_A_3:
    '4.(a).(iii)  Chemical installations for the production on an industrial scale of basic organic chemicals, such as: sulfurous hydrocarbons',
  _4_A_4:
    '4.(a).(iv) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: nitrogenous hydrocarbons',
  _4_A_5:
    '4.(a).(v) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: phosphorus-containing hydrocarbons',
  _4_A_6:
    '4.(a).(vi) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: halogenic hydrocarbons',
  _4_A_7:
    '4.(a).(vii) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: organometallic compounds',
  _4_A_8:
    '4.(a).(viii) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: basic plastic materials',
  _4_A_9:
    '4.(a).(ix) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: synthetic rubbers',
  _4_A_10:
    '4.(a).(x) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: dyes and pigments',
  _4_A_11:
    '4.(a).(xi) Chemical installations for the production on an industrial scale of basic organic chemicals, such as: surface-active agents and surfactants',
  _4_B: '4.(b) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as',
  _4_B_1:
    '4.(b).(i) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as: gases',
  _4_B_2:
    '4.(b).(ii) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as: acids',
  _4_B_3:
    '4.(b).(iii) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as: bases',
  _4_B_4:
    '4.(b).(iv) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as: salts',
  _4_B_5:
    '4.(b).(v) Chemical installations for the production on an industrial scale of basic inorganic chemicals, such as: non-metals, metal oxides or other inorganic compounds',
  _4_C: '4.(c) Chemical installations for the production on an industrial scale of phosphorous-, nitrogen- or potassium-based fertilisers',
  _4_D: '4.(d) Chemical installations for the production on an industrial scale of basic plant health products and of biocides',
  _4_E: '4.(e) Installations using a chemical or biological process for the production on an industrial scale of basic pharmaceutical products',
  _4_F: '4.(f) Installations for the production on an industrial scale of explosives and pyrotechnic products',
  _5: '5 Waste and wastewater management',
  _5_A: '5.(a) Installations for the recovery or disposal of hazardous waste',
  _5_B: '5.(b) Installations for the incineration of non-hazardous waste',
  _5_C: '5.(c) Installations for the disposal of non-hazardous waste',
  _5_D: '5.(d) Landfills',
  _5_E: '5.(e) Installations for the disposal or recycling of animal carcasses and animal waste',
  _5_F: '5.(f) Urban waste-water treatment plants',
  _5_G: '5.(g) Independently operated industrial waste-water treatment plants which serve one or more activities of this annex',
  _6: '6 Paper and wood production and processing',
  _6_A: '6.(a) Industrial plants for the production of pulp from timber or similar fibrous materials',
  _6_B: '6.(b) Industrial plants for the production of paper and board and other primary wood products (such as chipboard, fibreboard and plywood)',
  _6_C: '6.(c) Industrial plants for the preservation of wood and wood products with chemicals',
  _7: '7 Intensive livestock production and aquaculture',
  _7_A: '7.(a) Installations for the intensive rearing of poultry or pigs',
  _7_A_1: '7.(a).(i) Installations for the intensive rearing of poultry or pigs: with 40,000 places for poultry',
  _7_A_2:
    '7.(a).(ii) Installations for the intensive rearing of poultry or pigs: with 2,000 places for production pigs (over 30 kg)',
  _7_A_3: '7.(a).(iii) Installations for the intensive rearing of poultry or pigs: with 750 places for sows',
  _7_B: '7.(b) Intensive aquaculture',
  _8: '8 Animal and vegetable products from the food and beverage sector',
  _8_A: '8.(a) Slaughterhouses',
  _8_B: '8.(b) Treatment and processing intended for the production of food and beverage products from',
  _8_B_1:
    '8.(b).(i) Treatment and processing intended for the production of food and beverage products from: animal raw materials (other than milk)',
  _8_B_2:
    '8.(b).(ii) Treatment and processing intended for the production of food and beverage products from: vegetable raw materials',
  _8_C: '8.(c) Treatment and processing of milk',
  _9: '9 Other activities',
  _9_A: '9.(a) Plants for the pre-treatment (operations such as washing, bleaching, mercerisation) or dyeing of fibres or textiles',
  _9_B: '9.(b) Plants for the tanning of hides and skins',
  _9_C: '9.(c) Installations for the surface treatment of substances, objects or products using organic solvents, in particular for dressing, printing, coating, degreasing, waterproofing, sizing, painting, cleaning, or impregnating',
  _9_D: '9.(d) Installations for the production of carbon (hard-burnt coal) or electro-graphite by means of incineration or graphitisation',
  _9_E: '9.(e) Installations for the building of and painting or removal of paint from ships',
};

export const activityItemTypeMap: Record<activityFinalStepItemType, PRTRCodes['codes'][0]> = {
  _1_A: '_1_A_MINERAL_OIL_GAS_REFINERIES',
  _1_B: '_1_B_INSTALLATIONS_FOR_GASIFICATION_LIGUEFACTION',
  _1_C: '_1_C_THERMAL_POWER_STATIONS_OTHER_COMBUSTION_INSTALLATIONS',
  _1_D: '_1_D_COKE_OVENS',
  _1_E: '_1_E_COAL_ROLLING_MILLS',
  _1_F: '_1_F_INSTALLATIONS_MANUFACTURE_OF_COAL_PRODUCTS_SOLID_SMOKELESS_FUEL',
  _2_A: '_2_A_METAL_ORE_ROASTING_OR_SINTERING_INSTALLATIONS',
  _2_B: '_2_B_INSTALLATIONS_FOR_PRODUCTION_OF_PIG_IRON_OR_STEEL',
  _2_C_1: '_2_C_1_HOT_ROLLING_MILLS',
  _2_C_2: '_2_C_2_SMITHERIES_WITH_HAMMERS',
  _2_C_3: '_2_C_3_APPLICATION_OF_PROTECTIVE_FUSED_METAL_COATS',
  _2_D: '_2_D_FERROUS_METAL_FOUNDRIES_WITH_PRODUCTION_CAPACITY_OF_20_TONNES_PER_DAY',
  _2_E_1: '_2_E_1_PRODUCTION_OF_NON_FERROUS_CRUDE_METALS',
  _2_E_2: '_2_E_2_SMELTING_OF_NON_FERROUS_METALS',
  _2_F: '_2_F_INSTALLATIONS_FOR_SURFACE_TREATMENT_OF_METALS_AND_PLASTIC_MATERIALS',
  _3_A: '_3_A_UNDERGROUND_MINING_AND_RELATED_OPERATIONS',
  _3_B: '_3_B_OPENCAST_MINING_AND_QUARRYING',
  _3_C_1: '_3_C_1_CEMENT_CLINKER_IN_ROTARY_KILNS',
  _3_C_2: '_3_C_2_LIME_IN_ROTARY_KILNS',
  _3_C_3: '_3_C_3_CEMENT_CLINKER_OR_LIME_IN_OTHER_FURNACES',
  _3_D: '_3_D_INSTALLATIONS_FOR_THE_PRODUCTION_OF_ASBESTOS_AND_MANUFACTURE_OF_ASBESTOS_BASED_PRODUCTS',
  _3_E: '_3_E_INSTALLATIONS_FOR_THE_MANUFACTURE_OF_GLASS_INCLUDING_GLASS_FIBRE',
  _3_F: '_3_F_INSTALLATIONS_FOR_MELTING_MINERAL_SUBSTANCES',
  _3_G: '_3_G_INSTALLATIONS_FOR_THE_MANUFACTURE_OF_CERAMIC_PRODUCTS',
  _4_A_1: '_4_A_1_SIMPLE_HYDROCARBONS',
  _4_A_2: '_4_A_2_OXYGEN_CONTAINING_HYDROCARBONS',
  _4_A_3: '_4_A_3_SULFUROUS_HYDROCARBONS',
  _4_A_4: '_4_A_4_NITROGENOUS_HYDROCARBONS',
  _4_A_5: '_4_A_5_PHOSPHOROUS_HYDROCARBONS',
  _4_A_6: '_4_A_6_HALOGENIC_HYDROCARBONS',
  _4_A_7: '_4_A_7_ORGANOMETALLIC_COMPOUNDS',
  _4_A_8: '_4_A_8_BASIC_PLASTIC_MATERIALS',
  _4_A_9: '_4_A_9_SYNTHETIC_RUBBERS',
  _4_A_10: '_4_A_10_DYES_AND_PIGMENTS',
  _4_A_11: '_4_A_11_SURFACE_ACTIVE_AGENTS_AND_SURFACTANS',
  _4_B_1: '_4_B_1_GASES',
  _4_B_2: '_4_B_2_ACIDS',
  _4_B_3: '_4_B_3_BASES',
  _4_B_4: '_4_B_4_SALTS',
  _4_B_5: '_4_B_5_NON_METALS_METAL_OXIDES',
  _4_C: '_4_C_CHEMICAL_INSTALLATIONS_FOR_PRODUCTION_ON_INDUSTRIAL_SCALE_OF_PHOSPHOROUS',
  _4_D: '_4_D_CHEMICAL_INSTALLATIONS_FOR_PRODUCTION_ON_INDUSTRIAL_SCALE_OF_BASIC_PLANT_HEALTH',
  _4_E: '_4_E_INSTALLATIONS_USING_CHEMICAL_OR_BIOLOGICAL_PROCESS',
  _4_F: '_4_F_INSTALLATIONS_FOR_PRODUCTION_ON_INDUSTRIAL_SCALE_OF_EXPLOSIVES',
  _5_A: '_5_A_INSTALLATIONS_FOR_THE_RECOVERY_OR_DISPOSAL_OF_HAZARDOUS_WASTE',
  _5_B: '_5_B_INSTALLATIONS_FOR_THE_INCINERATION_OF_NON_HAZARDOUS_WASTE',
  _5_C: '_5_C_INSTALLATIONS_FOR_THE_DISPOSAL_OF_NON_HAZARDOUS_WASTE',
  _5_D: '_5_D_LANDFILLS',
  _5_E: '_5_E_INSTALLATIONS_FOR_DISPOSAL_OR_RECYCLING_OF_ANIMAL_CARCASSES',
  _5_F: '_5_F_URBAN_WASTE_WATER_TREATMENT_PLANTS',
  _5_G: '_5_G_INDEPENDENTLY_OPERATED_INDUSTRIAL_WASTE_WATER_TREATMENT',
  _6_A: '_6_A_INDUSTRIAL_PLANTS_FOR_PRODUCTION_OF_PULP_FROM_WOOD_OR_OTHER_FIBROUS',
  _6_B: '_6_B_INDUSTRIAL_PLANTS_FOR_PRODUCTION_OF_PAPER_AND_BOARD',
  _6_C: '_6_C_INDUSTRIAL_PLANTS_FOR_PRESERVATION_OF_WOOD',
  _7_A_1: '_7_A_1_WITH_40000_PLACES_FOR_POULTRY',
  _7_A_2: '_7_A_2_WITH_2000_PLACES_FOR_PRODUCTION_PIGS',
  _7_A_3: '_7_A_3_WITH_750_PLACES_FOR_SOWS',
  _7_B: '_7_B_INTENSIVE_AQUACULTURE',
  _8_A: '_8_A_SLAGHTERHOUSES',
  _8_B_1: '_8_B_1_ANIMAL_RAW_MATERIALS',
  _8_B_2: '_8_B_2_VEGETABLE_RAW_MATERIALS',
  _8_C: '_8_C_TREATMENT_AND_PROCESSING_OF_MILK',
  _9_A: '_9_A_PLANTS_FOR_THE_PRETREATMENT',
  _9_B: '_9_B_PLANTS_FOR_THE_TANNING',
  _9_C: '_9_C_INSTALLATIONS_FOR_THE_SURFACE_TREATMENT',
  _9_D: '_9_D_INSTALLATIONS_FOR_THE_PRODUCTION_OF_CARBON',
  _9_E: '_9_E_INSTALLATIONS_FOR_THE_BUILDING_AND_PAINTING_OR_REMOVAL_OF_PAINT',
};
