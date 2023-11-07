import { AerRequestActionPayload } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { aerVerifyHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { TaskSection } from '@shared/task-list/task-list.interface';

function getAerSubTasks(payload: AerRequestActionPayload): TaskSection<any>[] {
  return [
    {
      title: 'Identification',
      tasks: [
        {
          name: 'serviceContactDetails',
          linkText: aerHeaderTaskMap['serviceContactDetails'],
          link: 'aer/submitted/service-contact-details',
        },
        {
          name: 'operatorDetails',
          linkText: aerHeaderTaskMap['operatorDetails'],
          link: `aer/submitted/operator-details`,
        },
      ],
    },
    {
      title: 'Emissions overview',
      tasks: [
        {
          name: 'aerMonitoringPlanChanges',
          linkText: aerHeaderTaskMap['aerMonitoringPlanChanges'],
          link: 'aer/submitted/monitoring-plan-changes',
        },
        {
          name: 'monitoringApproach',
          linkText: aerHeaderTaskMap['monitoringApproach'],
          link: 'aer/submitted/monitoring-approach',
        },
        {
          name: 'aggregatedEmissionsData',
          linkText: aerHeaderTaskMap['aggregatedEmissionsData'],
          link: 'aer/submitted/aggregated-consumption-and-flight-data',
        },
        {
          name: 'aviationAerAircraftData',
          linkText: aerHeaderTaskMap['aviationAerAircraftData'],
          link: 'aer/submitted/aircraft-types-data',
        },
        {
          name: 'saf',
          linkText: aerHeaderTaskMap['saf'],
          link: 'aer/submitted/emissions-reduction-claim',
        },
        payload.aer?.dataGaps
          ? {
              name: 'dataGaps',
              linkText: aerHeaderTaskMap['dataGaps'],
              link: 'aer/submitted/data-gaps',
            }
          : null,
      ],
    },
    {
      title: 'Additional information',
      tasks: [
        {
          name: 'additionalDocuments',
          linkText: aerHeaderTaskMap['additionalDocuments'],
          link: 'aer/submitted/additional-docs',
        },
      ],
    },
    {
      title: 'Emissions for the scheme year',
      tasks: [
        {
          name: 'aviationAerTotalEmissionsConfidentiality',
          linkText: aerHeaderTaskMap['aviationAerTotalEmissionsConfidentiality'],
          link: 'aer/submitted/total-emissions',
        },
      ],
    },
  ];
}

function getAerVerifySubTasks(): TaskSection<any>[] {
  return [
    {
      title: 'Verifier assessment',
      tasks: [
        {
          name: 'verificationReport',
          linkText: aerVerifyHeaderTaskMap['verificationReport'],
          link: 'aer/verify-submitted/verifier-details',
        },
        {
          name: 'opinionStatement',
          linkText: aerVerifyHeaderTaskMap['opinionStatement'],
          link: 'aer/verify-submitted/opinion-statement',
        },
        {
          name: 'etsComplianceRules',
          linkText: aerVerifyHeaderTaskMap['etsComplianceRules'],
          link: 'aer/verify-submitted/ets-compliance-rules',
        },
        {
          name: 'complianceMonitoringReportingRules',
          linkText: aerVerifyHeaderTaskMap['complianceMonitoringReportingRules'],
          link: 'aer/verify-submitted/compliance-monitoring',
        },
        {
          name: 'emissionsReductionClaimVerification',
          linkText: aerVerifyHeaderTaskMap['emissionsReductionClaimVerification'],
          link: 'aer/verify-submitted/verify-emissions-reduction-claim',
        },
        {
          name: 'overallDecision',
          linkText: aerVerifyHeaderTaskMap['overallDecision'],
          link: 'aer/verify-submitted/overall-decision',
        },
      ],
    },
    {
      title: 'Verifier findings',
      tasks: [
        {
          name: 'uncorrectedMisstatements',
          linkText: aerVerifyHeaderTaskMap['uncorrectedMisstatements'],
          link: 'aer/verify-submitted/uncorrected-misstatements',
        },
        {
          name: 'uncorrectedNonConformities',
          linkText: aerVerifyHeaderTaskMap['uncorrectedNonConformities'],
          link: 'aer/verify-submitted/uncorrected-non-conformities',
        },
        {
          name: 'uncorrectedNonCompliances',
          linkText: aerVerifyHeaderTaskMap['uncorrectedNonCompliances'],
          link: 'aer/verify-submitted/uncorrected-non-compliances',
        },
        {
          name: 'recommendedImprovements',
          linkText: aerVerifyHeaderTaskMap['recommendedImprovements'],
          link: 'aer/verify-submitted/recommended-improvements',
        },
        {
          name: 'dataGapsMethodologies',
          linkText: aerVerifyHeaderTaskMap['dataGapsMethodologies'],
          link: 'aer/verify-submitted/data-gaps-methodologies',
        },
        {
          name: 'materialityLevel',
          linkText: aerVerifyHeaderTaskMap['materialityLevel'],
          link: 'aer/verify-submitted/materiality-level',
        },
      ],
    },
  ];
}

export function getAerApplicationSubmittedTasks(payload: AerRequestActionPayload): TaskSection<any>[] {
  return [
    {
      title: 'Reporting obligation',
      tasks: [
        {
          name: 'reportingObligation',
          linkText: aerHeaderTaskMap['reportingObligation'],
          link: 'aer/submitted/reporting-obligation',
        },
      ],
    },
    ...(payload.reportingRequired ? getAerSubTasks(payload) : []),
  ]
    .filter((section) => !!section)
    .map((section) => ({
      ...section,
      tasks: section.tasks.filter((task) => !!task),
    }));
}

export function getAerVerifyApplicationSubmittedTasks(payload: AerRequestActionPayload): TaskSection<any>[] {
  return [...getAerVerifySubTasks(), ...getAerSubTasks(payload)]
    .filter((section) => !!section)
    .map((section) => ({
      ...section,
      tasks: section.tasks.filter((task) => !!task),
    }));
}
