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
import { ElectricityFlowsDataSource } from './electricityFlowsDataSource';
import { SubInstallationHierarchicalOrder } from './subInstallationHierarchicalOrder';

export interface ElectricityFlows {
  electricityProduced: boolean;
  electricityFlowsDataSources?: Array<ElectricityFlowsDataSource>;
  methodologyAppliedDescription?: string;
  hierarchicalOrder?: SubInstallationHierarchicalOrder;
  supportingFiles?: Array<string>;
}
