import {
  aerVerifyCorsiaHeaderTaskMap,
  aerVerifyHeaderTaskMap,
} from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { AerReviewCorsiaTaskKey } from '@aviation/request-task/store';
import { TaskSection } from '@shared/task-list/task-list.interface';

export type AerCorsiaReviewGroup =
  // Verifier assessment
  | 'VERIFIER_DETAILS'
  | 'TIME_ALLOCATION_AND_SCOPE'
  | 'VERIFICATION_CRITERIA'
  | 'PROCESS_AND_ANALYSIS_DETAILS'
  // Verified emissions
  | 'MONITORING_APPROACH_EMISSIONS'
  | 'ELIGIBLE_FUELS_REDUCTION_CLAIM'
  // Verifier findings
  | 'UNCORRECTED_MISSTATEMENTS'
  | 'UNCORRECTED_NON_CONFORMITIES'
  | 'UNCORRECTED_NON_COMPLIANCES'
  | 'RECOMMENDED_IMPROVEMENTS'
  // Verifier summary
  | 'CONCLUSIONS_DATA_QUALITY'
  | 'VERIFICATION_STATEMENT_CONCLUSIONS'
  | 'INDEPENDENT_REVIEW'
  // Operator identification
  | 'SERVICE_CONTACT_DETAILS'
  | 'OPERATOR_DETAILS'
  // Emissions overview
  | 'MONITORING_PLAN_CHANGES'
  | 'MONITORING_APPROACH'
  | 'AGGREGATED_EMISSIONS_DATA'
  | 'AIRCRAFT_DATA'
  | 'EMISSIONS_REDUCTION_CLAIM'
  | 'DATA_GAPS'
  // Emissions for the scheme year
  | 'TOTAL_EMISSIONS'
  // Additional information
  | 'ADDITIONAL_DOCUMENTS'
  | 'CONFIDENTIALITY'
  // Reporting obligation
  | 'REPORTING_OBLIGATION_DETAILS';

export const aerReviewCorsiaHeaderTaskMap: Partial<Record<AerReviewCorsiaTaskKey, string>> = {
  // Verifier assessment
  verifierDetails: aerVerifyCorsiaHeaderTaskMap.verifierDetails,
  timeAllocationScope: aerVerifyCorsiaHeaderTaskMap.timeAllocationScope,
  generalInformation: aerVerifyCorsiaHeaderTaskMap.generalInformation,
  processAnalysis: aerVerifyCorsiaHeaderTaskMap.processAnalysis,
  // Verified emissions
  opinionStatement: aerVerifyCorsiaHeaderTaskMap.opinionStatement,
  emissionsReductionClaimVerification: aerVerifyCorsiaHeaderTaskMap.emissionsReductionClaimVerification,
  // Verifier findings
  uncorrectedMisstatements: aerVerifyHeaderTaskMap.uncorrectedMisstatements,
  uncorrectedNonConformities: aerVerifyHeaderTaskMap.uncorrectedNonConformities,
  uncorrectedNonCompliances: aerVerifyHeaderTaskMap.uncorrectedNonCompliances,
  recommendedImprovements: aerVerifyHeaderTaskMap.recommendedImprovements,
  // Verifier summary
  verifiersConclusions: aerVerifyCorsiaHeaderTaskMap.verifiersConclusions,
  overallDecision: aerVerifyCorsiaHeaderTaskMap.overallDecision,
  independentReview: aerVerifyCorsiaHeaderTaskMap.independentReview,
  // Reporting obligation
  reportingObligation: 'Reporting obligation',
  // Operator identification
  serviceContactDetails: 'Service contact details',
  operatorDetails: 'Operator details',
  aerMonitoringPlanChanges: 'Monitoring plan changes',
  monitoringApproach: 'Monitoring approach',
  // Emissions overview
  aggregatedEmissionsData: 'Aggregated consumption and flight data',
  aviationAerAircraftData: 'Aircraft types data',
  emissionsReductionClaim: 'CORSIA eligible fuels reduction claim',
  dataGaps: 'Data gaps',
  // Emissions for the scheme year
  totalEmissionsCorsia: 'Total emissions',
  // Additional information
  additionalDocuments: 'Additional documents and information',
  confidentiality: 'Request for data not to be published by ICAO',
};

export const aerCorsiaReviewGroupMap: Partial<Record<AerReviewCorsiaTaskKey, AerCorsiaReviewGroup>> = {
  // Verifier assessment
  verifierDetails: 'VERIFIER_DETAILS',
  timeAllocationScope: 'TIME_ALLOCATION_AND_SCOPE',
  generalInformation: 'VERIFICATION_CRITERIA',
  processAnalysis: 'PROCESS_AND_ANALYSIS_DETAILS',
  // Verified emissions
  opinionStatement: 'MONITORING_APPROACH_EMISSIONS',
  emissionsReductionClaimVerification: 'ELIGIBLE_FUELS_REDUCTION_CLAIM',
  // Verifier findings
  uncorrectedMisstatements: 'UNCORRECTED_MISSTATEMENTS',
  uncorrectedNonConformities: 'UNCORRECTED_NON_CONFORMITIES',
  uncorrectedNonCompliances: 'UNCORRECTED_NON_COMPLIANCES',
  recommendedImprovements: 'RECOMMENDED_IMPROVEMENTS',
  // Verifier summary
  verifiersConclusions: 'CONCLUSIONS_DATA_QUALITY',
  overallDecision: 'VERIFICATION_STATEMENT_CONCLUSIONS',
  independentReview: 'INDEPENDENT_REVIEW',
  // Reporting obligation
  reportingObligation: 'REPORTING_OBLIGATION_DETAILS',
  // Operator identification
  serviceContactDetails: 'SERVICE_CONTACT_DETAILS',
  operatorDetails: 'OPERATOR_DETAILS',
  // Emissions overview
  aerMonitoringPlanChanges: 'MONITORING_PLAN_CHANGES',
  monitoringApproach: 'MONITORING_APPROACH',
  aggregatedEmissionsData: 'AGGREGATED_EMISSIONS_DATA',
  aviationAerAircraftData: 'AIRCRAFT_DATA',
  emissionsReductionClaim: 'EMISSIONS_REDUCTION_CLAIM',
  dataGaps: 'DATA_GAPS',
  // Emissions for the scheme year
  totalEmissionsCorsia: 'TOTAL_EMISSIONS',
  // Additional information
  additionalDocuments: 'ADDITIONAL_DOCUMENTS',
  confidentiality: 'CONFIDENTIALITY',
};

export const AER_CORSIA_REVIEW_APPLICATION_TASKS: TaskSection<any>[] = [
  {
    title: 'Reporting obligation',
    tasks: [
      {
        name: 'reportingObligation',
        linkText: aerReviewCorsiaHeaderTaskMap.reportingObligation,
        link: 'aer-review-corsia/reporting-obligation',
      },
    ],
  },
  {
    title: 'Identification',
    tasks: [
      {
        name: 'serviceContactDetails',
        linkText: aerReviewCorsiaHeaderTaskMap.serviceContactDetails,
        link: 'aer-review-corsia/service-contact-details',
      },
      {
        name: 'operatorDetails',
        linkText: aerReviewCorsiaHeaderTaskMap.operatorDetails,
        link: 'aer-review-corsia/operator-details',
      },
    ],
  },
  {
    title: 'Emissions overview',
    tasks: [
      {
        name: 'aerMonitoringPlanChanges',
        linkText: aerReviewCorsiaHeaderTaskMap.aerMonitoringPlanChanges,
        link: 'aer-review-corsia/monitoring-plan-changes',
      },
      {
        name: 'monitoringApproach',
        linkText: aerReviewCorsiaHeaderTaskMap.monitoringApproach,
        link: 'aer-review-corsia/monitoring-approach',
      },
      {
        name: 'aggregatedEmissionsData',
        linkText: aerReviewCorsiaHeaderTaskMap.aggregatedEmissionsData,
        link: 'aer-review-corsia/aggregated-consumption-and-flight-data',
      },
      {
        name: 'aviationAerAircraftData',
        linkText: aerReviewCorsiaHeaderTaskMap.aviationAerAircraftData,
        link: 'aer-review-corsia/aircraft-types-data',
      },
      {
        name: 'emissionsReductionClaim',
        linkText: aerReviewCorsiaHeaderTaskMap.emissionsReductionClaim,
        link: 'aer-review-corsia/emissions-reduction-claim',
      },
      {
        name: 'dataGaps',
        linkText: aerReviewCorsiaHeaderTaskMap.dataGaps,
        link: 'aer-review-corsia/data-gaps',
      },
    ],
  },
  {
    title: 'Emissions for the scheme year',
    tasks: [
      {
        name: 'totalEmissionsCorsia',
        linkText: aerReviewCorsiaHeaderTaskMap.totalEmissionsCorsia,
        link: 'aer-review-corsia/total-emissions',
      },
    ],
  },
  {
    title: 'Additional information',
    tasks: [
      {
        name: 'additionalDocuments',
        linkText: aerReviewCorsiaHeaderTaskMap.additionalDocuments,
        link: 'aer-review-corsia/additional-docs',
      },
      {
        name: 'confidentiality',
        linkText: aerReviewCorsiaHeaderTaskMap.confidentiality,
        link: 'aer-review-corsia/confidentiality',
      },
    ],
  },
];
