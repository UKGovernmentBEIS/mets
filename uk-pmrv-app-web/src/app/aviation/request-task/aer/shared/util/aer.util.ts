import { getAerCorsiaReviewSections } from '@aviation/request-task/aer/corsia/aer-review/util/aer-review-corsia.util';
import { TaskItem, TaskItemStatus, TaskSection } from '@shared/task-list/task-list.interface';

import {
  AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload,
  AviationAerCorsiaApplicationReviewRequestTaskPayload,
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerUkEts,
  AviationAerUkEtsApplicationReviewRequestTaskPayload,
  AviationAerUkEtsApplicationSubmitRequestTaskPayload,
  RequestTaskDTO,
  RequestTaskPayload,
} from 'pmrv-api';

import { AerTaskKey, AerVerifyTaskKey } from '../../../store';
import {
  aerVerifyReviewGroupMap,
  getAerVerifyVerifierFindings,
  getVerifierAssessmentTasks,
} from './aer-verify-tasks.util';

export type AerUkEtsReviewGroup =
  | 'REPORTING_OBLIGATION_DETAILS'
  | 'SERVICE_CONTACT_DETAILS'
  | 'OPERATOR_DETAILS'
  | 'MONITORING_PLAN_CHANGES'
  | 'MONITORING_APPROACH'
  | 'AGGREGATED_EMISSIONS_DATA'
  | 'AIRCRAFT_DATA'
  | 'EMISSIONS_REDUCTION_CLAIM'
  | 'CONFIDENTIALITY'
  | 'TOTAL_EMISSIONS'
  | 'ADDITIONAL_DOCUMENTS'
  | 'DATA_GAPS'
  | 'VERIFIER_DETAILS'
  | 'OPINION_STATEMENT'
  | 'ETS_COMPLIANCE_RULES'
  | 'COMPLIANCE_MONITORING_REPORTING'
  | 'OVERALL_DECISION'
  | 'UNCORRECTED_MISSTATEMENTS'
  | 'UNCORRECTED_NON_CONFORMITIES'
  | 'UNCORRECTED_NON_COMPLIANCES'
  | 'RECOMMENDED_IMPROVEMENTS'
  | 'EMISSIONS_REDUCTION_CLAIM_VERIFICATION'
  | 'CLOSE_DATA_GAPS_METHODOLOGIES'
  | 'MATERIALITY_LEVEL';

export const aerHeaderTaskMap: Partial<Record<AerTaskKey, string>> = {
  serviceContactDetails: 'Service contact details',
  reportingObligation: 'Reporting obligation',
  operatorDetails: 'Operator details',
  additionalDocuments: 'Additional documents and information',
  aerMonitoringPlanChanges: 'Monitoring plan changes',
  dataGaps: 'Data gaps',
  monitoringApproach: 'Monitoring approach',
  aviationAerTotalEmissionsConfidentiality: 'Total emissions',
  totalEmissionsCorsia: 'Total emissions',
  aggregatedEmissionsData: 'Aggregated consumption and flight data',
  saf: 'Emissions reduction claim',
  aviationAerAircraftData: 'Aircraft types data',
  sendReport: 'Send report',
  changesRequested: 'Changes requested by the regulator',
  emissionsReductionClaim: 'CORSIA eligible fuels reduction claim',
  confidentiality: 'Request for data not to be published by ICAO',
};

export const aerReviewGroupMap: Partial<Record<AerTaskKey, AerUkEtsReviewGroup>> = {
  serviceContactDetails: 'SERVICE_CONTACT_DETAILS',
  reportingObligation: 'REPORTING_OBLIGATION_DETAILS',
  operatorDetails: 'OPERATOR_DETAILS',
  additionalDocuments: 'ADDITIONAL_DOCUMENTS',
  aerMonitoringPlanChanges: 'MONITORING_PLAN_CHANGES',
  dataGaps: 'DATA_GAPS',
  monitoringApproach: 'MONITORING_APPROACH',
  aviationAerTotalEmissionsConfidentiality: 'TOTAL_EMISSIONS',
  aggregatedEmissionsData: 'AGGREGATED_EMISSIONS_DATA',
  saf: 'EMISSIONS_REDUCTION_CLAIM',
  confidentiality: 'CONFIDENTIALITY',
  aviationAerAircraftData: 'AIRCRAFT_DATA',
};

export function getAerSections(
  payload:
    | AviationAerUkEtsApplicationSubmitRequestTaskPayload
    | AviationAerCorsiaApplicationSubmitRequestTaskPayload
    | AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload
    | AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload,
  isAmendsTask?: boolean,
  isCorsia?: boolean,
): TaskSection<any>[] {
  const availableSections = getAvailableSections(payload, isAmendsTask, isCorsia);

  return availableSections.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(task.name as AerTaskKey, payload, isAmendsTask, isCorsia);
        const link = getLinkByTaskAndStatus(
          status,
          task,
          payload?.aerSectionsCompleted,
          payload?.payloadType,
          isCorsia,
          (payload as AviationAerCorsiaApplicationSubmitRequestTaskPayload)?.verificationPerformed ||
            !(payload as AviationAerCorsiaApplicationSubmitRequestTaskPayload)?.reportingRequired,
        );

        return {
          ...task,
          status,
          link,
        };
      }),
    };
  });
}

export function getAerReviewSections(payload: AviationAerUkEtsApplicationReviewRequestTaskPayload): TaskSection<any>[] {
  const availableSections = payload.reportingRequired
    ? filterOutTaskSections(
        getAvailableSections(payload, false),
        ['Send report', 'Reporting obligation'],
        ['sendReport', 'reportingObligation'],
      )
    : filterOutTaskSections(getAvailableSections(payload, false), ['Send report'], ['sendReport']);

  const availableSubTasksVerifier = [
    ...getVerifierAssessmentTasks(false, payload.verificationReport?.safExists),
    ...getAerVerifyVerifierFindings(false),
  ]
    .filter((section) => !!section)
    .map((section) => ({
      ...section,
      tasks: section.tasks.filter((task) => !!task),
    }));

  const sections =
    payload?.verificationReport && Object.keys(payload?.verificationReport)?.length !== 0
      ? [...availableSubTasksVerifier, ...availableSections]
      : availableSections;

  return sections.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        if (aerReviewGroupMap[task.name]) {
          const submitStatus = getTaskStatusByTaskCompletionState(task.name as AerTaskKey, payload);

          const link =
            submitStatus === 'complete'
              ? (task.name as AerTaskKey) === 'serviceContactDetails'
                ? `aer/review/${task.link}`
                : `aer/review/${task.link}/summary`
              : `aer/review/${task.link}`;

          const reviewKey = aerReviewGroupMap[task.name as AerTaskKey];

          const status = getAerReviewTaskStatusByTaskCompletionState(
            payload.reviewGroupDecisions[reviewKey],
            payload.reviewSectionsCompleted[reviewKey],
          );

          return {
            ...task,
            status,
            link,
          };
        } else {
          const completionState = payload.verificationSectionsCompleted[task.name];
          const submitStatus =
            completionState != null ? (completionState[0] ? 'complete' : 'in progress') : 'not started';

          const link = submitStatus === 'complete' ? `aer/review/${task.link}/summary` : 'aer/review/' + task.link;

          const reviewKey = aerVerifyReviewGroupMap[task.name as AerVerifyTaskKey];

          const status = getAerReviewTaskStatusByTaskCompletionState(
            payload.reviewGroupDecisions[reviewKey],
            payload.reviewSectionsCompleted[reviewKey],
          );
          return {
            ...task,
            status,
            link,
          };
        }
      }),
    };
  });
}

export function getTaskStatusByTaskCompletionState(
  taskName: AerTaskKey,
  payload:
    | AviationAerUkEtsApplicationSubmitRequestTaskPayload
    | AviationAerCorsiaApplicationSubmitRequestTaskPayload
    | AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload,
  isAmendsTask?: boolean,
  isCorsia?: boolean,
): TaskItemStatus {
  const completionState = payload.aerSectionsCompleted[taskName];
  const prefilledOperatorDetails = payload?.empOriginatedData?.operatorDetails;

  switch (taskName) {
    case 'serviceContactDetails':
      return completionState != null ? (completionState[0] ? 'complete' : 'in progress') : 'in progress';
    case 'operatorDetails':
      return completionState != null
        ? completionState[0]
          ? 'complete'
          : 'in progress'
        : !prefilledOperatorDetails
          ? 'not started'
          : 'in progress';
    case 'aviationAerTotalEmissionsConfidentiality':
      return resolveTotalEmissionsStatus(payload as AviationAerUkEtsApplicationSubmitRequestTaskPayload);
    case 'totalEmissionsCorsia':
      return resolveTotalEmissionsCorsiaStatus(payload as AviationAerCorsiaApplicationSubmitRequestTaskPayload);
    case 'dataGaps':
      if (!isCorsia) {
        return completionState != null
          ? completionState[0]
            ? 'complete'
            : isSectionComplete(payload, 'aggregatedEmissionsData')
              ? 'in progress'
              : 'cannot start yet'
          : !isSectionComplete(payload, 'aggregatedEmissionsData')
            ? 'cannot start yet'
            : 'not started';
      } else {
        return completionState != null
          ? completionState[0]
            ? 'complete'
            : 'in progress'
          : !isSectionComplete(payload, 'totalEmissionsCorsia')
            ? 'cannot start yet'
            : 'not started';
      }
    case 'sendReport': {
      const availableSubTasks = getAvailableSubTasks(getAvailableSections(payload, isAmendsTask, isCorsia));

      return resolveSubmissionTaskStatus(payload.aerSectionsCompleted, availableSubTasks);
    }
    default:
      return completionState != null ? (completionState[0] ? 'complete' : 'in progress') : 'not started';
  }
}

export function getAerReviewTaskStatusByTaskCompletionState(decision: any, completionState: boolean): TaskItemStatus {
  return completionState ? (decision.type === 'ACCEPTED' ? 'accepted' : 'operator to amend') : 'undecided';
}

export const isAllSectionsApproved = (
  payload: AviationAerUkEtsApplicationReviewRequestTaskPayload | AviationAerCorsiaApplicationReviewRequestTaskPayload,
  type: RequestTaskDTO['type'],
) => {
  if (['AVIATION_AER_UKETS_APPLICATION_REVIEW', 'AVIATION_AER_CORSIA_APPLICATION_REVIEW'].includes(type)) {
    const sections =
      type === 'AVIATION_AER_CORSIA_APPLICATION_REVIEW'
        ? getAerCorsiaReviewSections(payload as AviationAerCorsiaApplicationReviewRequestTaskPayload)
        : getAerReviewSections(payload as AviationAerUkEtsApplicationReviewRequestTaskPayload);
    const notApprovedSections = sections
      .map((section) => section.tasks)
      .filter((task) => task.some((item) => item.status !== 'accepted' && item.name !== 'decision'));

    return notApprovedSections.length === 0;
  } else return false;
};

function getAvailableSections(
  payload:
    | AviationAerUkEtsApplicationSubmitRequestTaskPayload
    | AviationAerCorsiaApplicationSubmitRequestTaskPayload
    | AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload,
  isAmendsTask?: boolean,
  isCorsia?: boolean,
): TaskSection<any>[] {
  let filteredSections = AER_APPLICATION_TASKS;

  if (!isCorsia) {
    const aerUkEts = payload.aer as AviationAerUkEts;
    filteredSections = filterOutTaskSections(filteredSections, [], ['totalEmissionsCorsia', 'confidentiality']);
    if (
      !isSectionComplete(payload, 'monitoringApproach') ||
      aerUkEts?.monitoringApproach?.monitoringApproachType !== 'FUEL_USE_MONITORING'
    ) {
      filteredSections = filterOutTaskSections(filteredSections, [], ['dataGaps']);
    }
  } else {
    filteredSections = filterOutTaskSections(filteredSections, [], ['aviationAerTotalEmissionsConfidentiality']);
    const sectionIndex = filteredSections.findIndex((section) => section.title === 'Emissions overview');
    if (sectionIndex >= 0) {
      const taskIndex = filteredSections[sectionIndex].tasks.findIndex((task) => task.name === 'saf');
      if (taskIndex >= 0) {
        filteredSections[sectionIndex].tasks[taskIndex] = {
          name: 'emissionsReductionClaim',
          linkText: aerHeaderTaskMap['emissionsReductionClaim'],
          link: 'emissions-reduction-claim',
        };
      }
    }
  }

  if (!isSectionComplete(payload, 'reportingObligation') || payload.reportingRequired === false) {
    filteredSections = filterOutTaskSections(filteredSections, [
      'Identification',
      'Emissions overview',
      'Additional information',
      'Emissions for the scheme year',
    ]);
  }

  if (isAmendsTask) {
    if (filteredSections.filter((section) => section.title === 'Regulator comments').length === 0) {
      filteredSections.unshift({
        title: 'Regulator comments',
        tasks: [
          {
            name: 'changesRequested',
            status: getTaskStatusByTaskCompletionState('changesRequested', payload),
            linkText: aerHeaderTaskMap['changesRequested'],
            link: 'changes-requested',
          },
        ],
      });
    }
  }
  return filteredSections;
}

function getLinkByTaskAndStatus(
  status: TaskItemStatus,
  task: TaskItem<any>,
  aerSectionsCompleted: { [key: string]: Array<boolean> },
  payloadType: RequestTaskPayload['payloadType'],
  isCorsia?: boolean,
  sendToRegulatorCorsia?: boolean,
): string {
  let prefixedLink;
  switch (payloadType) {
    case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD':
    case 'AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD':
      prefixedLink = `aer/${task.link}`;
      break;
    case 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD':
    case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD':
      prefixedLink = `aer-corsia/${task.link}`;
      break;
    case 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD':
      prefixedLink = `aer-verify-corsia/${task.link}`;
      break;
  }

  switch (task.name as AerTaskKey) {
    case 'dataGaps':
    case 'aviationAerTotalEmissionsConfidentiality':
    case 'totalEmissionsCorsia': {
      if (status === 'cannot start yet') {
        prefixedLink = null;
      }
      break;
    }
    case 'sendReport': {
      if (status === 'cannot start yet') {
        prefixedLink = null;
      } else if (isCorsia) {
        const sendReportPath = sendToRegulatorCorsia ? 'regulator' : 'verifier';
        prefixedLink = `${prefixedLink}/${sendReportPath}`;
      }
      break;
    }
  }

  return prefixedLink;
}

function resolveSubmissionTaskStatus(
  aerSectionsCompleted: { [key: string]: boolean[] },
  availableTasks: string[],
): TaskItemStatus {
  return Object.keys(aerSectionsCompleted).length >= availableTasks.length &&
    Object.values(aerSectionsCompleted).every((tc) => tc[0] === true)
    ? 'not started'
    : 'cannot start yet';
}

function resolveTotalEmissionsStatus(payload: AviationAerUkEtsApplicationSubmitRequestTaskPayload): TaskItemStatus {
  const totalEmissionsState = payload.aerSectionsCompleted['aviationAerTotalEmissionsConfidentiality'];
  const aggregatedEmissionsDataState = payload.aerSectionsCompleted['aggregatedEmissionsData'];
  const safState = payload.aerSectionsCompleted['saf'];

  if (!aggregatedEmissionsDataState || !safState) {
    return 'cannot start yet';
  } else if (!aggregatedEmissionsDataState[0] || !safState[0]) {
    return 'cannot start yet';
  } else if (totalEmissionsState === undefined || totalEmissionsState === null) {
    return 'not started';
  } else if (totalEmissionsState[0]) {
    return 'complete';
  } else {
    return 'in progress';
  }
}

export function resolveTotalEmissionsCorsiaStatus(
  payload: AviationAerCorsiaApplicationSubmitRequestTaskPayload,
): TaskItemStatus {
  const totalEmissionsState = payload.aerSectionsCompleted['totalEmissionsCorsia'];
  return getTaskStatusByTaskCompletionState('aggregatedEmissionsData', payload) === 'complete' &&
    getTaskStatusByTaskCompletionState('emissionsReductionClaim', payload) === 'complete'
    ? totalEmissionsState === undefined || totalEmissionsState === null
      ? 'not started'
      : totalEmissionsState[0]
        ? 'complete'
        : 'in progress'
    : 'cannot start yet';
}

function getAvailableSubTasks(taskSections: TaskSection<any>[]): AerTaskKey[] {
  return taskSections
    .map((t) => t.tasks)
    .reduce((acc, tasks) => acc.concat(tasks), [])
    .filter((task) => task.name !== 'sendReport')
    .map((task) => task.name as AerTaskKey);
}

function isSectionComplete(
  payload: AviationAerUkEtsApplicationSubmitRequestTaskPayload | AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  section: string,
): boolean {
  return payload.aerSectionsCompleted?.[section]?.[0] === true;
}

/**
 * Utility function that filters out TaskSections by title, or Subtasks by AerTaskKey
 */
export function filterOutTaskSections(
  tasks: TaskSection<any>[],
  sectionTitles: string[] = [],
  taskNames: AerTaskKey[] = [],
) {
  let filteredSections = sectionTitles ? tasks.filter((t) => !sectionTitles.includes(t.title)) : tasks;

  if (taskNames) {
    filteredSections = filteredSections.map((section) => {
      return {
        title: section.title,
        tasks: section.tasks.filter((task) => !taskNames.includes(task.name as AerTaskKey)),
      };
    });
  }

  return filteredSections;
}

/**
 * Define TaskSections without flow prefix (i.e. 'aer')
 * It will get prefixed in getLinkByTaskAndStatus()
 * @see getLinkByTaskAndStatus
 */
export const AER_APPLICATION_TASKS: TaskSection<any>[] = [
  {
    title: 'Reporting obligation',
    tasks: [
      {
        name: 'reportingObligation',
        linkText: aerHeaderTaskMap['reportingObligation'],
        link: 'reporting-obligation',
      },
    ],
  },
  {
    title: 'Identification',
    tasks: [
      {
        name: 'serviceContactDetails',
        linkText: aerHeaderTaskMap['serviceContactDetails'],
        link: 'service-contact-details',
      },
      {
        name: 'operatorDetails',
        linkText: aerHeaderTaskMap['operatorDetails'],
        link: 'operator-details',
      },
    ],
  },
  {
    title: 'Emissions overview',
    tasks: [
      {
        name: 'aerMonitoringPlanChanges',
        linkText: aerHeaderTaskMap['aerMonitoringPlanChanges'],
        link: 'monitoring-plan-changes',
      },
      {
        name: 'monitoringApproach',
        linkText: aerHeaderTaskMap['monitoringApproach'],
        link: 'monitoring-approach',
      },
      {
        name: 'aggregatedEmissionsData',
        linkText: aerHeaderTaskMap['aggregatedEmissionsData'],
        link: 'aggregated-consumption-and-flight-data',
      },
      {
        name: 'aviationAerAircraftData',
        linkText: aerHeaderTaskMap['aviationAerAircraftData'],
        link: 'aircraft-types-data',
      },
      {
        name: 'saf',
        linkText: aerHeaderTaskMap['saf'],
        link: 'emissions-reduction-claim',
      },
      {
        name: 'dataGaps',
        linkText: aerHeaderTaskMap['dataGaps'],
        link: 'data-gaps',
      },
    ],
  },
  {
    title: 'Additional information',
    tasks: [
      {
        name: 'additionalDocuments',
        linkText: aerHeaderTaskMap['additionalDocuments'],
        link: 'additional-docs',
      },
      {
        name: 'confidentiality',
        linkText: aerHeaderTaskMap['confidentiality'],
        link: 'confidentiality',
      },
    ],
  },
  {
    title: 'Emissions for the scheme year',
    tasks: [
      {
        name: 'aviationAerTotalEmissionsConfidentiality',
        linkText: aerHeaderTaskMap['aviationAerTotalEmissionsConfidentiality'],
        link: 'total-emissions',
      },
      {
        name: 'totalEmissionsCorsia',
        linkText: aerHeaderTaskMap['totalEmissionsCorsia'],
        link: 'total-emissions',
      },
    ],
  },
  {
    title: 'Send report',
    tasks: [
      {
        name: 'sendReport',
        linkText: aerHeaderTaskMap['sendReport'],
        link: 'send-report',
        status: 'cannot start yet',
      },
    ],
  },
];
