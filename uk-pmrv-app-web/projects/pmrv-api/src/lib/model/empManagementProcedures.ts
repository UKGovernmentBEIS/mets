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
import { EmpDataFlowActivities } from './empDataFlowActivities';
import { EmpEnvironmentalManagementSystem } from './empEnvironmentalManagementSystem';
import { EmpMonitoringReportingRoles } from './empMonitoringReportingRoles';
import { EmpProcedureForm } from './empProcedureForm';

export interface EmpManagementProcedures {
  monitoringReportingRoles?: EmpMonitoringReportingRoles;
  recordKeepingAndDocumentation?: EmpProcedureForm;
  assignmentOfResponsibilities?: EmpProcedureForm;
  monitoringPlanAppropriateness?: EmpProcedureForm;
  qaMeteringAndMeasuringEquipment?: EmpProcedureForm;
  dataValidation?: EmpProcedureForm;
  correctionsAndCorrectiveActions?: EmpProcedureForm;
  controlOfOutsourcedActivities?: EmpProcedureForm;
  assessAndControlRisks?: EmpProcedureForm;
  dataFlowActivities?: EmpDataFlowActivities;
  environmentalManagementSystem?: EmpEnvironmentalManagementSystem;
  riskAssessmentFile?: string;
  upliftQuantityCrossChecks?: EmpProcedureForm;
}