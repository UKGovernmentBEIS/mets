import { AerVerifyCorsiaTaskKey, AerVerifyTaskKey } from '@aviation/request-task/store';
import { TaskSection } from '@shared/task-list/task-list.interface';

import { AviationAerCorsia } from 'pmrv-api';

import { AerUkEtsReviewGroup } from './aer.util';

export const aerVerifyHeaderTaskMap: Partial<Record<AerVerifyTaskKey, string>> = {
  verificationReport: 'Verifier details',
  opinionStatement: 'Opinion statement',
  etsComplianceRules: 'Compliance with ETS rules',
  complianceMonitoringReportingRules: 'Compliance with monitoring and reporting principles',
  emissionsReductionClaimVerification: 'Verify the emissions reduction claim',
  overallDecision: 'Overall decision',
  uncorrectedMisstatements: 'Uncorrected misstatements',
  uncorrectedNonConformities: 'Uncorrected non-conformities',
  uncorrectedNonCompliances: 'Uncorrected non-compliances',
  recommendedImprovements: 'Recommended improvements',
  dataGapsMethodologies: 'Methodologies to close data gaps',
  materialityLevel: 'Materiality level and reference documents held',
  sendReport: 'Send report to operator',
};

export const aerVerifyCorsiaHeaderTaskMap: Partial<Record<AerVerifyCorsiaTaskKey, string>> = {
  verifierDetails: 'Verifier details and impartiality',
  timeAllocationScope: 'Time allocation and scope',
  generalInformation: 'Verification criteria and operator data',
  processAnalysis: 'Process and analysis details',
  opinionStatement: 'Monitoring approach and emissions',
  emissionsReductionClaimVerification: 'Verify the emissions reduction claim',
  verifiersConclusions: 'Conclusions on data quality and materiality',
  overallDecision: 'Concluding verification statement',
  independentReview: 'Independent review',
};

export function getVerifierAssessmentTasks(
  isCorsia: boolean,
  safExistInVerificationReport?: boolean,
): TaskSection<any>[] {
  return [
    {
      title: 'Verifier assessment',
      tasks: isCorsia
        ? [
            {
              name: 'verifierDetails',
              linkText: aerVerifyCorsiaHeaderTaskMap['verifierDetails'],
              link: 'aer-verify-corsia/verifier-details',
            },
            {
              name: 'timeAllocationScope',
              linkText: aerVerifyCorsiaHeaderTaskMap['timeAllocationScope'],
              link: 'aer-verify-corsia/time-allocation',
            },
            {
              name: 'generalInformation',
              linkText: aerVerifyCorsiaHeaderTaskMap['generalInformation'],
              link: 'aer-verify-corsia/general-information',
            },
            {
              name: 'processAnalysis',
              linkText: aerVerifyCorsiaHeaderTaskMap['processAnalysis'],
              link: 'aer-verify-corsia/process-analysis',
            },
          ]
        : [
            {
              name: 'verificationReport',
              linkText: aerVerifyHeaderTaskMap['verificationReport'],
              link: 'aer-verify/verifier-details',
            },
            {
              name: 'opinionStatement',
              linkText: aerVerifyHeaderTaskMap['opinionStatement'],
              link: 'aer-verify/opinion-statement',
            },
            {
              name: 'etsComplianceRules',
              linkText: aerVerifyHeaderTaskMap['etsComplianceRules'],
              link: 'aer-verify/ets-compliance-rules',
            },
            {
              name: 'complianceMonitoringReportingRules',
              linkText: aerVerifyHeaderTaskMap['complianceMonitoringReportingRules'],
              link: 'aer-verify/compliance-monitoring',
            },
            safExistInVerificationReport
              ? {
                  name: 'emissionsReductionClaimVerification',
                  linkText: aerVerifyHeaderTaskMap['emissionsReductionClaimVerification'],
                  link: 'aer-verify/verify-emissions-reduction-claim',
                }
              : null,
            {
              name: 'overallDecision',
              linkText: aerVerifyHeaderTaskMap['overallDecision'],
              link: 'aer-verify/overall-decision',
            },
          ],
    },
  ];
}

export function getAerVerifyCorsiaVerifiedEmissions(aer: AviationAerCorsia): TaskSection<any>[] {
  return [
    {
      title: 'Verified emissions',
      tasks: [
        {
          name: 'opinionStatement',
          linkText: aerVerifyCorsiaHeaderTaskMap['opinionStatement'],
          link: 'aer-verify-corsia/verify-monitoring-approach',
        },
        aer.emissionsReductionClaim.emissionsReductionClaimDetails
          ? {
              name: 'emissionsReductionClaimVerification',
              linkText: aerVerifyCorsiaHeaderTaskMap['emissionsReductionClaimVerification'],
              link: 'aer-verify-corsia/verify-emissions-reduction-claim',
            }
          : null,
      ],
    },
  ];
}

export function getAerVerifyVerifierFindings(isCorsia: boolean): TaskSection<any>[] {
  const prefixedLink = isCorsia ? 'aer-verify-corsia' : 'aer-verify';

  return [
    {
      title: 'Verifier findings',
      tasks: [
        {
          name: 'uncorrectedMisstatements',
          linkText: aerVerifyHeaderTaskMap['uncorrectedMisstatements'],
          link: `${prefixedLink}/uncorrected-misstatements`,
        },
        {
          name: 'uncorrectedNonConformities',
          linkText: aerVerifyHeaderTaskMap['uncorrectedNonConformities'],
          link: `${prefixedLink}/uncorrected-non-conformities`,
        },
        {
          name: 'uncorrectedNonCompliances',
          linkText: aerVerifyHeaderTaskMap['uncorrectedNonCompliances'],
          link: `${prefixedLink}/uncorrected-non-compliances`,
        },
        {
          name: 'recommendedImprovements',
          linkText: aerVerifyHeaderTaskMap['recommendedImprovements'],
          link: `${prefixedLink}/recommended-improvements`,
        },
        ...(isCorsia
          ? []
          : [
              {
                name: 'dataGapsMethodologies',
                linkText: aerVerifyHeaderTaskMap['dataGapsMethodologies'],
                link: `${prefixedLink}/data-gaps-methodologies`,
              },
              {
                name: 'materialityLevel',
                linkText: aerVerifyHeaderTaskMap['materialityLevel'],
                link: `${prefixedLink}/materiality-level`,
              },
            ]),
      ],
    },
  ];
}

export function getAerVerifyCorsiaVerifierSummary(): TaskSection<any>[] {
  return [
    {
      title: 'Verifier Summary',
      tasks: [
        {
          name: 'verifiersConclusions',
          linkText: aerVerifyCorsiaHeaderTaskMap['verifiersConclusions'],
          link: 'aer-verify-corsia/verifiers-conclusions',
        },
        {
          name: 'overallDecision',
          linkText: aerVerifyCorsiaHeaderTaskMap['overallDecision'],
          link: 'aer-verify-corsia/overall-decision',
        },
        {
          name: 'independentReview',
          linkText: aerVerifyCorsiaHeaderTaskMap['independentReview'],
          link: 'aer-verify-corsia/independent-review',
        },
      ],
    },
  ];
}

export function getAerVerifySendReportSection(isCorsia: boolean): TaskSection<any>[] {
  const prefixedLink = isCorsia ? 'aer-verify-corsia' : 'aer-verify';

  return [
    {
      title: 'Complete report',
      tasks: [
        {
          name: 'sendReport',
          linkText: aerVerifyHeaderTaskMap['sendReport'],
          link: `${prefixedLink}/send-report`,
          status: 'cannot start yet',
        },
      ],
    },
  ];
}

export const aerVerifyReviewGroupMap: Partial<Record<AerVerifyTaskKey, AerUkEtsReviewGroup>> = {
  verificationReport: 'VERIFIER_DETAILS',
  opinionStatement: 'OPINION_STATEMENT',
  etsComplianceRules: 'ETS_COMPLIANCE_RULES',
  complianceMonitoringReportingRules: 'COMPLIANCE_MONITORING_REPORTING',
  overallDecision: 'OVERALL_DECISION',
  uncorrectedMisstatements: 'UNCORRECTED_MISSTATEMENTS',
  uncorrectedNonConformities: 'UNCORRECTED_NON_CONFORMITIES',
  uncorrectedNonCompliances: 'UNCORRECTED_NON_COMPLIANCES',
  recommendedImprovements: 'RECOMMENDED_IMPROVEMENTS',
  emissionsReductionClaimVerification: 'EMISSIONS_REDUCTION_CLAIM_VERIFICATION',
  dataGapsMethodologies: 'CLOSE_DATA_GAPS_METHODOLOGIES',
  materialityLevel: 'MATERIALITY_LEVEL',
};
