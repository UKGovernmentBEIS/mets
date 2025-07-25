/**
 * METS API Documentation
 * METS API Documentation
 *
 * The version of the OpenAPI document: uk-pmrv-app-api 0.81.0-SNAPSHOT
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

export interface AerRegulatedActivity {
  id: string;
  type:
    | 'WASTE'
    | 'COMBUSTION'
    | 'UPSTREAM_GHG_REMOVAL'
    | 'MINERAL_OIL_REFINING'
    | 'COKE_PRODUCTION'
    | 'ORE_ROASTING_OR_SINTERING'
    | 'PIG_IRON_STEEL_PRODUCTION'
    | 'FERROUS_METALS_PRODUCTION'
    | 'NON_FERROUS_METALS_PRODUCTION'
    | 'PRIMARY_ALUMINIUM_PRODUCTION'
    | 'SECONDARY_ALUMINIUM_PRODUCTION'
    | 'CEMENT_CLINKER_PRODUCTION'
    | 'LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE'
    | 'CERAMICS_MANUFACTURING'
    | 'GYPSUM_OR_PLASTERBOARD_PRODUCTION'
    | 'GLASS_MANUFACTURING'
    | 'MINERAL_WOOL_MANUFACTURING'
    | 'PULP_PRODUCTION'
    | 'PAPER_OR_CARDBOARD_PRODUCTION'
    | 'CARBON_BLACK_PRODUCTION'
    | 'BULK_ORGANIC_CHEMICAL_PRODUCTION'
    | 'GLYOXAL_GLYOXYLIC_ACID_PRODUCTION'
    | 'NITRIC_ACID_PRODUCTION'
    | 'ADIPIC_ACID_PRODUCTION'
    | 'AMMONIA_PRODUCTION'
    | 'SODA_ASH_AND_SODIUM_BICARBONATE_PRODUCTION'
    | 'HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION'
    | 'CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE'
    | 'TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE'
    | 'STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE';
  capacity?: string;
  capacityUnit?:
    | 'MW_TH'
    | 'KW_TH'
    | 'MVA'
    | 'KVA'
    | 'KW'
    | 'MW'
    | 'TONNES_PER_DAY'
    | 'TONNES_PER_HOUR'
    | 'TONNES_PER_ANNUM'
    | 'KG_PER_DAY'
    | 'KG_PER_HOUR';
  hasEnergyCrf: boolean;
  hasIndustrialCrf: boolean;
  energyCrf?:
    | '_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION'
    | '_1_A_1_B_PETROLEUM_REFINING'
    | '_1_A_1_C_MANUFACTURE_OF_SOLID_FUELS_AND_OTHER_ENERGY_INDUSTRIES'
    | '_1_A_2_A_IRON_AND_STEEL'
    | '_1_A_2_B_NON_FERROUS_METALS'
    | '_1_A_2_C_CHEMICALS'
    | '_1_A_2_D_PULP_PAPER_AND_PRINT'
    | '_1_A_2_E_FOOD_PROCESSING_BEVERAGES_AND_TOBACCO'
    | '_1_A_2_F_NON_METALLIC_MINERALS'
    | '_1_A_2_GVII_MOBILE_COMBUSTION_IN_MANUFACTURING_INDUSTRIES_AND_CONSTRUCTION'
    | '_1_A_2_GVIII_STATIONARY_COMBUSTION_IN_MANUFACTURING_AND_CONSTRUCTION_OTHER'
    | '_1_A_3_AI_INTERNATIONAL_AVIATION'
    | '_1_A_3_AII_CIVIL_AVIATION'
    | '_1_A_3_B_ROAD_TRANSPORTATION'
    | '_1_A_3_C_RAILWAYS'
    | '_1_A_3_DI_INTERNATIONAL_NAVIGATION'
    | '_1_A_3_DII_NATIONAL_NAVIGATION'
    | '_1_A_3_E_OTHER'
    | '_1_A_4_A_COMMERCIAL_INSTITUTIONAL_COMBUSTION'
    | '_1_A_4_B_RESIDENTIAL'
    | '_1_A_4_C_AGRICULTURE_FORESTRY_FISHING'
    | '_1_A_5_A_OTHER_STATIONARY_INCLUDING_MILITARY'
    | '_1_A_5_B_OTHER_MOBILE_INCLUDING_MILITARY'
    | '_1_B_1_A_COAL_MINING_AND_HANDLING'
    | '_1_B_1_B_SOLID_FUEL_TRANSFORMATION'
    | '_1_B_1_C_OTHER'
    | '_1_B_2_A_OIL'
    | '_1_B_2_B_NATURAL_GAS'
    | '_1_B_2_C_VENTING_AND_FLARING'
    | '_2_A_1_CEMENT_PRODUCTION'
    | '_2_A_2_LIME_PRODUCTION'
    | '_2_A_3_GLASS_PRODUCTION'
    | '_2_A_4_OTHER_PROCESS_USES_OF_CARBONATES'
    | '_2_B_1_AMMONIA_PRODUCTION'
    | '_2_B_10_OTHER'
    | '_2_B_2_NITRIC_ACID_PRODUCTION'
    | '_2_B_3_ADIPIC_ACID_PRODUCTION'
    | '_2_B_4_CAPROLACTAM_GLYOXAL_AND_GLYOXYLIC_ACID_PRODUCTION'
    | '_2_B_5_CARBIDE_PRODUCTION'
    | '_2_B_6_TITANIUM_DIOXIDE_PRODUCTION'
    | '_2_B_7_SODA_ASH_PRODUCTION'
    | '_2_B_8_PETROCHEMICAL_AND_CARBON_BLACK_PRODUCTION'
    | '_2_B_9_FLUOROCHEMICAL_PRODUCTION'
    | '_2_C_1_IRON_AND_STEEL_PRODUCTION'
    | '_2_C_2_FERROALLOYS_PRODUCTION'
    | '_2_C_3_ALUMINIUM_PRODUCTION'
    | '_2_C_4_MAGNESIUM_PRODUCTION'
    | '_2_C_5_LEAD_PRODUCTION'
    | '_2_C_6_ZINC_PRODUCTION'
    | '_2_C_7_OTHER_METAL_PRODUCTION'
    | '_2_D_1_LUBRICANT_USE'
    | '_2_D_2_PARAFFIN_WAX_USE'
    | '_2_D_3_OTHER'
    | '_2_E_1_INTEGRATED_CIRCUIT_OR_SEMICONDUCTOR'
    | '_2_E_2_TFT_FLAT_PANEL_DISPLAY'
    | '_2_E_3_PHOTOVOLTAICS'
    | '_2_E_4_HEAT_TRANSFER_FLUID'
    | '_2_E_5_OTHER'
    | '_2_F_1_REFRIGERATION_AND_AIR_CONDITIONING_EQUIPMENT'
    | '_2_F_2_FOAM_BLOWING_AGENTS'
    | '_2_F_3_FIRE_EXTINGUISHERS'
    | '_2_F_4_AEROSOLS'
    | '_2_F_5_SOLVENTS'
    | '_2_F_6_OTHER'
    | '_2_G_1_ELECTRICAL_EQUIPMENT'
    | '_2_G_2_SF_6_AND_PFCS_FROM_OTHER_PRODUCT_USE'
    | '_2_G_3_N_2_O_FROM_PRODUCT_USES'
    | '_2_G_4_OTHER'
    | '_2_H_OTHER';
  industrialCrf?:
    | '_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION'
    | '_1_A_1_B_PETROLEUM_REFINING'
    | '_1_A_1_C_MANUFACTURE_OF_SOLID_FUELS_AND_OTHER_ENERGY_INDUSTRIES'
    | '_1_A_2_A_IRON_AND_STEEL'
    | '_1_A_2_B_NON_FERROUS_METALS'
    | '_1_A_2_C_CHEMICALS'
    | '_1_A_2_D_PULP_PAPER_AND_PRINT'
    | '_1_A_2_E_FOOD_PROCESSING_BEVERAGES_AND_TOBACCO'
    | '_1_A_2_F_NON_METALLIC_MINERALS'
    | '_1_A_2_GVII_MOBILE_COMBUSTION_IN_MANUFACTURING_INDUSTRIES_AND_CONSTRUCTION'
    | '_1_A_2_GVIII_STATIONARY_COMBUSTION_IN_MANUFACTURING_AND_CONSTRUCTION_OTHER'
    | '_1_A_3_AI_INTERNATIONAL_AVIATION'
    | '_1_A_3_AII_CIVIL_AVIATION'
    | '_1_A_3_B_ROAD_TRANSPORTATION'
    | '_1_A_3_C_RAILWAYS'
    | '_1_A_3_DI_INTERNATIONAL_NAVIGATION'
    | '_1_A_3_DII_NATIONAL_NAVIGATION'
    | '_1_A_3_E_OTHER'
    | '_1_A_4_A_COMMERCIAL_INSTITUTIONAL_COMBUSTION'
    | '_1_A_4_B_RESIDENTIAL'
    | '_1_A_4_C_AGRICULTURE_FORESTRY_FISHING'
    | '_1_A_5_A_OTHER_STATIONARY_INCLUDING_MILITARY'
    | '_1_A_5_B_OTHER_MOBILE_INCLUDING_MILITARY'
    | '_1_B_1_A_COAL_MINING_AND_HANDLING'
    | '_1_B_1_B_SOLID_FUEL_TRANSFORMATION'
    | '_1_B_1_C_OTHER'
    | '_1_B_2_A_OIL'
    | '_1_B_2_B_NATURAL_GAS'
    | '_1_B_2_C_VENTING_AND_FLARING'
    | '_2_A_1_CEMENT_PRODUCTION'
    | '_2_A_2_LIME_PRODUCTION'
    | '_2_A_3_GLASS_PRODUCTION'
    | '_2_A_4_OTHER_PROCESS_USES_OF_CARBONATES'
    | '_2_B_1_AMMONIA_PRODUCTION'
    | '_2_B_10_OTHER'
    | '_2_B_2_NITRIC_ACID_PRODUCTION'
    | '_2_B_3_ADIPIC_ACID_PRODUCTION'
    | '_2_B_4_CAPROLACTAM_GLYOXAL_AND_GLYOXYLIC_ACID_PRODUCTION'
    | '_2_B_5_CARBIDE_PRODUCTION'
    | '_2_B_6_TITANIUM_DIOXIDE_PRODUCTION'
    | '_2_B_7_SODA_ASH_PRODUCTION'
    | '_2_B_8_PETROCHEMICAL_AND_CARBON_BLACK_PRODUCTION'
    | '_2_B_9_FLUOROCHEMICAL_PRODUCTION'
    | '_2_C_1_IRON_AND_STEEL_PRODUCTION'
    | '_2_C_2_FERROALLOYS_PRODUCTION'
    | '_2_C_3_ALUMINIUM_PRODUCTION'
    | '_2_C_4_MAGNESIUM_PRODUCTION'
    | '_2_C_5_LEAD_PRODUCTION'
    | '_2_C_6_ZINC_PRODUCTION'
    | '_2_C_7_OTHER_METAL_PRODUCTION'
    | '_2_D_1_LUBRICANT_USE'
    | '_2_D_2_PARAFFIN_WAX_USE'
    | '_2_D_3_OTHER'
    | '_2_E_1_INTEGRATED_CIRCUIT_OR_SEMICONDUCTOR'
    | '_2_E_2_TFT_FLAT_PANEL_DISPLAY'
    | '_2_E_3_PHOTOVOLTAICS'
    | '_2_E_4_HEAT_TRANSFER_FLUID'
    | '_2_E_5_OTHER'
    | '_2_F_1_REFRIGERATION_AND_AIR_CONDITIONING_EQUIPMENT'
    | '_2_F_2_FOAM_BLOWING_AGENTS'
    | '_2_F_3_FIRE_EXTINGUISHERS'
    | '_2_F_4_AEROSOLS'
    | '_2_F_5_SOLVENTS'
    | '_2_F_6_OTHER'
    | '_2_G_1_ELECTRICAL_EQUIPMENT'
    | '_2_G_2_SF_6_AND_PFCS_FROM_OTHER_PRODUCT_USE'
    | '_2_G_3_N_2_O_FROM_PRODUCT_USES'
    | '_2_G_4_OTHER'
    | '_2_H_OTHER';
}
