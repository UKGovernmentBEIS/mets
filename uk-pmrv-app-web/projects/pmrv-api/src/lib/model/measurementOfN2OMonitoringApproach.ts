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
import { MeasurementOfN2OEmissionPointCategoryAppliedTier } from './measurementOfN2OEmissionPointCategoryAppliedTier';
import { PermitMonitoringApproachSection } from './permitMonitoringApproachSection';
import { ProcedureForm } from './procedureForm';
import { ProcedureOptionalForm } from './procedureOptionalForm';

export interface MeasurementOfN2OMonitoringApproach extends PermitMonitoringApproachSection {
  hasTransfer?: boolean;
  approachDescription: string;
  emissionDetermination: ProcedureForm;
  referenceDetermination: ProcedureForm;
  operationalManagement: ProcedureForm;
  nitrousOxideEmissionsDetermination: ProcedureForm;
  nitrousOxideConcentrationDetermination: ProcedureForm;
  quantityProductDetermination: ProcedureForm;
  quantityMaterials: ProcedureForm;
  gasFlowCalculation: ProcedureOptionalForm;
  emissionPointCategoryAppliedTiers: Array<MeasurementOfN2OEmissionPointCategoryAppliedTier>;
}
