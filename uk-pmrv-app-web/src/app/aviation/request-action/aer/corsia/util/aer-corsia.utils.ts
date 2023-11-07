import { AerCorsiaRequestActionPayload } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import {
  aerVerifyCorsiaHeaderTaskMap,
  aerVerifyHeaderTaskMap,
} from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { TaskSection } from '@shared/task-list/task-list.interface';

function getAerSubTasks(): TaskSection<any>[] {
  return [
    {
      title: 'Identification',
      tasks: [
        {
          name: 'serviceContactDetails',
          linkText: aerHeaderTaskMap['serviceContactDetails'],
          link: 'aer-corsia/submitted/service-contact-details',
        },
        {
          name: 'operatorDetails',
          linkText: aerHeaderTaskMap['operatorDetails'],
          link: 'aer-corsia/submitted/operator-details',
        },
      ],
    },
    {
      title: 'Emissions overview',
      tasks: [
        {
          name: 'aerMonitoringPlanChanges',
          linkText: aerHeaderTaskMap['aerMonitoringPlanChanges'],
          link: 'aer-corsia/submitted/monitoring-plan-changes',
        },
        {
          name: 'monitoringApproach',
          linkText: aerHeaderTaskMap['monitoringApproach'],
          link: 'aer-corsia/submitted/monitoring-approach',
        },
        {
          name: 'aggregatedEmissionsData',
          linkText: aerHeaderTaskMap['aggregatedEmissionsData'],
          link: 'aer-corsia/submitted/aggregated-consumption-and-flight-data',
        },
        {
          name: 'aviationAerAircraftData',
          linkText: aerHeaderTaskMap['aviationAerAircraftData'],
          link: 'aer-corsia/submitted/aircraft-types-data',
        },
        {
          name: 'emissionsReductionClaim',
          linkText: aerHeaderTaskMap['emissionsReductionClaim'],
          link: 'aer-corsia/submitted/emissions-reduction-claim',
        },
        {
          name: 'dataGaps',
          linkText: aerHeaderTaskMap['dataGaps'],
          link: 'aer-corsia/submitted/data-gaps',
        },
      ],
    },
    {
      title: 'Additional information',
      tasks: [
        {
          name: 'additionalDocuments',
          linkText: aerHeaderTaskMap['additionalDocuments'],
          link: 'aer-corsia/submitted/additional-docs',
        },
        {
          name: 'confidentiality',
          linkText: aerHeaderTaskMap['confidentiality'],
          link: 'aer-corsia/submitted/confidentiality',
        },
      ],
    },
    {
      title: 'Emissions for the scheme year',
      tasks: [
        {
          name: 'aviationAerTotalEmissionsConfidentiality',
          linkText: aerHeaderTaskMap['aviationAerTotalEmissionsConfidentiality'],
          link: 'aer-corsia/submitted/total-emissions',
        },
      ],
    },
  ];
}

function getAerVerifySubTasks(payload: AerCorsiaRequestActionPayload): TaskSection<any>[] {
  return [
    {
      title: 'Verifier assessment',
      tasks: [
        {
          name: 'verifierDetails',
          linkText: aerVerifyCorsiaHeaderTaskMap['verifierDetails'],
          link: 'aer-corsia/submitted/verifier-details',
        },
        {
          name: 'timeAllocationScope',
          linkText: aerVerifyCorsiaHeaderTaskMap['timeAllocationScope'],
          link: 'aer-corsia/submitted/time-allocation',
        },
        {
          name: 'generalInformation',
          linkText: aerVerifyCorsiaHeaderTaskMap['generalInformation'],
          link: 'aer-corsia/submitted/general-information',
        },
        {
          name: 'analysisDetails',
          linkText: 'Process and analysis details',
          link: 'aer-corsia/submitted/process-analysis',
        },
      ],
    },
    {
      title: 'Verified emissions',
      tasks: [
        {
          name: 'opinionStatement',
          linkText: aerVerifyCorsiaHeaderTaskMap['opinionStatement'],
          link: 'aer-corsia/submitted/verify-monitoring-approach',
        },
        payload.aer.emissionsReductionClaim.emissionsReductionClaimDetails
          ? {
              name: 'emissionsReductionClaimVerification',
              linkText: aerVerifyCorsiaHeaderTaskMap['emissionsReductionClaimVerification'],
              link: 'aer-corsia/submitted/verify-emissions-reduction-claim',
            }
          : null,
      ],
    },
    {
      title: 'Verifier findings',
      tasks: [
        {
          name: 'uncorrectedMisstatements',
          linkText: aerVerifyHeaderTaskMap['uncorrectedMisstatements'],
          link: 'aer-corsia/submitted/uncorrected-misstatements',
        },
        {
          name: 'uncorrectedNonConformities',
          linkText: aerVerifyHeaderTaskMap['uncorrectedNonConformities'],
          link: 'aer-corsia/submitted/uncorrected-non-conformities',
        },
        {
          name: 'uncorrectedNonCompliances',
          linkText: aerVerifyHeaderTaskMap['uncorrectedNonCompliances'],
          link: 'aer-corsia/submitted/uncorrected-non-compliances',
        },
        {
          name: 'recommendedImprovements',
          linkText: aerVerifyHeaderTaskMap['recommendedImprovements'],
          link: 'aer-corsia/submitted/recommended-improvements',
        },
      ],
    },
    {
      title: 'Verifier Summary',
      tasks: [
        {
          name: 'verifiersConclusions',
          linkText: aerVerifyCorsiaHeaderTaskMap['verifiersConclusions'],
          link: 'aer-corsia/submitted/verifiers-conclusions',
        },
        {
          name: 'overallDecision',
          linkText: aerVerifyCorsiaHeaderTaskMap['overallDecision'],
          link: 'aer-corsia/submitted/overall-decision',
        },
        {
          name: 'independentReview',
          linkText: aerVerifyCorsiaHeaderTaskMap['independentReview'],
          link: 'aer-corsia/submitted/independent-review',
        },
      ],
    },
  ];
}

export function getAerCorsiaApplicationSubmittedTasks(payload: AerCorsiaRequestActionPayload): TaskSection<any>[] {
  return [
    {
      title: 'Reporting obligation',
      tasks: [
        {
          name: 'reportingObligation',
          linkText: aerHeaderTaskMap['reportingObligation'],
          link: 'aer-corsia/submitted/reporting-obligation',
        },
      ],
    },
    ...(payload.reportingRequired ? getAerSubTasks() : []),
  ]
    .filter((section) => !!section)
    .map((section) => ({
      ...section,
      tasks: section.tasks.filter((task) => !!task),
    }));
}

export function getAerCorsiaVerifyApplicationSubmittedTasks(
  payload: AerCorsiaRequestActionPayload,
): TaskSection<any>[] {
  return [...getAerVerifySubTasks(payload), ...getAerSubTasks()]
    .filter((section) => !!section)
    .map((section) => ({
      ...section,
      tasks: section.tasks.filter((task) => !!task),
    }));
}
