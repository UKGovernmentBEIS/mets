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
import { MonitoringApproachTypeEmissions } from './monitoringApproachTypeEmissions';
import { SiteVisit } from './siteVisit';

export interface OpinionStatement {
  regulatedActivities?: Array<
    | 'COMBUSTION'
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
    | 'STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE'
  >;
  combustionSources?: Array<string>;
  processSources?: Array<string>;
  monitoringApproachDescription: string;
  emissionFactorsDescription: string;
  operatorEmissionsAcceptable: boolean;
  monitoringApproachTypeEmissions?: MonitoringApproachTypeEmissions;
  additionalChangesNotCovered: boolean;
  additionalChangesNotCoveredDetails?: string;
  siteVisit: SiteVisit;
}