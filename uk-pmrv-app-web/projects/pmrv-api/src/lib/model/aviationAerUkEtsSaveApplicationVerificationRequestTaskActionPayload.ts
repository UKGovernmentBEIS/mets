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
import { AviationAerComplianceMonitoringReportingRules } from './aviationAerComplianceMonitoringReportingRules';
import { AviationAerDataGapsMethodologies } from './aviationAerDataGapsMethodologies';
import { AviationAerEmissionsReductionClaimVerification } from './aviationAerEmissionsReductionClaimVerification';
import { AviationAerEtsComplianceRules } from './aviationAerEtsComplianceRules';
import { AviationAerMaterialityLevel } from './aviationAerMaterialityLevel';
import { AviationAerOpinionStatement } from './aviationAerOpinionStatement';
import { AviationAerRecommendedImprovements } from './aviationAerRecommendedImprovements';
import { AviationAerUncorrectedMisstatements } from './aviationAerUncorrectedMisstatements';
import { AviationAerUncorrectedNonCompliances } from './aviationAerUncorrectedNonCompliances';
import { AviationAerUncorrectedNonConformities } from './aviationAerUncorrectedNonConformities';
import { AviationAerVerificationDecision } from './aviationAerVerificationDecision';
import { AviationAerVerificationTeamDetails } from './aviationAerVerificationTeamDetails';
import { AviationAerVerifierContact } from './aviationAerVerifierContact';
import { RequestTaskActionPayload } from './requestTaskActionPayload';

export interface AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload extends RequestTaskActionPayload {
  verifierContact: AviationAerVerifierContact;
  verificationTeamDetails: AviationAerVerificationTeamDetails;
  overallDecision: AviationAerVerificationDecision;
  etsComplianceRules: AviationAerEtsComplianceRules;
  complianceMonitoringReportingRules: AviationAerComplianceMonitoringReportingRules;
  opinionStatement: AviationAerOpinionStatement;
  uncorrectedMisstatements: AviationAerUncorrectedMisstatements;
  uncorrectedNonCompliances: AviationAerUncorrectedNonCompliances;
  uncorrectedNonConformities: AviationAerUncorrectedNonConformities;
  recommendedImprovements: AviationAerRecommendedImprovements;
  emissionsReductionClaimVerification?: AviationAerEmissionsReductionClaimVerification;
  dataGapsMethodologies: AviationAerDataGapsMethodologies;
  materialityLevel: AviationAerMaterialityLevel;
  verificationSectionsCompleted?: { [key: string]: Array<boolean> };
}