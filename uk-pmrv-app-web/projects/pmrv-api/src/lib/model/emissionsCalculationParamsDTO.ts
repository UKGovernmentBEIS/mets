/**
 * PMRV API Documentation
 * PMRV API Documentation
 *
 * The version of the OpenAPI document: uk-pmrv-app-api 0.81.0-SNAPSHOT
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

/**
 * The parameters needed to calculate the emissions
 */
export interface EmissionsCalculationParamsDTO {
  emissionFactor?: string;
  netCalorificValue?: string;
  oxidationFactor?: string;
  ncvMeasurementUnit?: 'GJ_PER_TONNE' | 'GJ_PER_NM3';
  efMeasurementUnit?: 'TONNES_OF_CO2_PER_TJ' | 'TONNES_OF_CO2_PER_NM3' | 'TONNES_OF_CO2_PER_TONNE';
  activityData: string;
  activityDataMeasurementUnit: 'TONNES' | 'NM3';
  carbonContent?: string;
  carbonContentMeasurementUnit?:
    | 'TONNES_OF_CO2_PER_NM3'
    | 'TONNES_OF_CO2_PER_TONNE'
    | 'TONNES_OF_CARBON_PER_NM3'
    | 'TONNES_OF_CARBON_PER_TONNE';
  containsBiomass?: boolean;
  biomassPercentage?: string;
  conversionFactor?: string;
  sourceStreamType:
    | 'COMBUSTION_COMMERCIAL_STANDARD_FUELS'
    | 'COMBUSTION_OTHER_GASEOUS_LIQUID_FUELS'
    | 'COMBUSTION_SOLID_FUELS'
    | 'COMBUSTION_FLARES'
    | 'OTHER'
    | 'COMBUSTION_GAS_PROCESSING_TERMINALS'
    | 'REFINERIES_MASS_BALANCE'
    | 'COKE_MASS_BALANCE'
    | 'METAL_ORE_MASS_BALANCE'
    | 'IRON_STEEL_MASS_BALANCE'
    | 'CARBON_BLACK_MASS_BALANCE_METHODOLOGY'
    | 'HYDROGEN_AND_SYNTHESIS_GAS_MASS_BALANCE_METHODOLOGY'
    | 'BULK_ORGANIC_CHEMICALS_MASS_BALANCE_METHODOLOGY'
    | 'NON_FERROUS_SEC_ALUMINIUM_MASS_BALANCE_METHODOLOGY'
    | 'SODA_ASH_SODIUM_BICARBONATE_MASS_BALANCE_METHODOLOGY'
    | 'PRIMARY_ALUMINIUM_MASS_BALANCE_METHODOLOGY'
    | 'REFINERIES_HYDROGEN_PRODUCTION'
    | 'CEMENT_CLINKER_CKD'
    | 'CERAMICS_SCRUBBING'
    | 'REFINERIES_CATALYTIC_CRACKER_REGENERATION'
    | 'COKE_FUEL_AS_PROCESS_INPUT'
    | 'IRON_STEEL_FUEL_AS_PROCESS_INPUT'
    | 'AMMONIA_FUEL_AS_PROCESS_INPUT'
    | 'HYDROGEN_AND_SYNTHESIS_GAS_FUEL_AS_PROCESS_INPUT'
    | 'COKE_CARBONATE_INPUT_METHOD_A'
    | 'COKE_OXIDE_OUTPUT_METHOD_B'
    | 'METAL_ORE_CARBONATE_INPUT'
    | 'IRON_STEEL_CARBONATE_INPUT'
    | 'CEMENT_CLINKER_KILN_INPUT_BASED_METHOD_A'
    | 'CEMENT_CLINKER_CLINKER_OUTPUT_METHOD_B'
    | 'CEMENT_CLINKER_NON_CARBONATE_CARBON'
    | 'LIME_DOLOMITE_MAGNESITE_CARBONATES_METHOD_A'
    | 'LIME_DOLOMITE_MAGNESITE_KILN_DUST_METHOD_B'
    | 'LIME_DOLOMITE_MAGNESITE_ALKALI_EARTH_OXIDE_METHOD_B'
    | 'GLASS_AND_MINERAL_WOOL_CARBONATES_INPUT'
    | 'CERAMICS_CARBON_INPUTS_METHOD_A'
    | 'CERAMICS_ALKALI_OXIDE_METHOD_B'
    | 'PULP_PAPER_MAKE_UP_CHEMICALS'
    | 'NON_FERROUS_SEC_ALUMINIUM_PROCESS_EMISSIONS'
    | 'COMBUSTION_SCRUBBING_CARBONATE'
    | 'COMBUSTION_SCRUBBING_UREA'
    | 'COMBUSTION_SCRUBBING_GYPSUM'
    | 'PRIMARY_ALUMINIUM_PFC';
}