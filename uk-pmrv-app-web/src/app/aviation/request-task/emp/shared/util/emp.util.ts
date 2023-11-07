import { extractMonitorinApproachType, isFUMM } from '@aviation/shared/components/emp/emission-sources/isFUMM';
import { EmpIssuanceApplicationSubmitRequestTaskPayload } from '@aviation/shared/types';
import { TaskItem, TaskItemStatus, TaskSection } from '@shared/task-list/task-list.interface';

import {
  EmpIssuanceCorsiaApplicationReviewRequestTaskPayload,
  EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload,
  EmpIssuanceDetermination,
  EmpIssuanceReviewDecision,
  EmpIssuanceUkEtsApplicationReviewRequestTaskPayload,
  EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload,
  EmpVariationCorsiaApplicationReviewRequestTaskPayload,
  EmpVariationCorsiaApplicationSubmitRequestTaskPayload,
  EmpVariationDetermination,
  EmpVariationReviewDecision,
  EmpVariationUkEtsApplicationReviewRequestTaskPayload,
  EmpVariationUkEtsApplicationSubmitRequestTaskPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
  RequestTaskPayload,
} from 'pmrv-api';

import { EmpCorsiaTaskKey, EmpRequestTaskPayloadCorsia, EmpRequestTaskPayloadUkEts, EmpTaskKey } from '../../../store';

export type EmpVariationSubmitRequestTaskPayload =
  | EmpVariationUkEtsApplicationSubmitRequestTaskPayload
  | EmpVariationCorsiaApplicationSubmitRequestTaskPayload;

export type EmpReviewGroup = EmpUkEtsReviewGroup | EmpCorsiaReviewGroup;

export type EmpUkEtsReviewGroup =
  | 'SERVICE_CONTACT_DETAILS'
  | 'OPERATOR_DETAILS'
  | 'FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES'
  | 'MONITORING_APPROACH'
  | 'EMISSION_SOURCES'
  | 'EMISSIONS_REDUCTION_CLAIM'
  | 'MANAGEMENT_PROCEDURES'
  | 'ABBREVIATIONS_AND_DEFINITIONS'
  | 'ADDITIONAL_DOCUMENTS'
  | 'LATE_SUBMISSION'
  | 'METHOD_A_PROCEDURES'
  | 'METHOD_B_PROCEDURES'
  | 'BLOCK_ON_OFF_PROCEDURES'
  | 'FUEL_UPLIFT_PROCEDURES'
  | 'BLOCK_HOUR_PROCEDURES'
  | 'DATA_GAPS';

export type EmpCorsiaReviewGroup =
  | 'SERVICE_CONTACT_DETAILS'
  | 'OPERATOR_DETAILS'
  | 'FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES'
  | 'MONITORING_APPROACH'
  | 'EMISSION_SOURCES'
  | 'MANAGEMENT_PROCEDURES'
  | 'ABBREVIATIONS_AND_DEFINITIONS'
  | 'ADDITIONAL_DOCUMENTS'
  | 'METHOD_A_PROCEDURES'
  | 'METHOD_B_PROCEDURES'
  | 'BLOCK_ON_OFF_PROCEDURES'
  | 'FUEL_UPLIFT_PROCEDURES'
  | 'BLOCK_HOUR_PROCEDURES'
  | 'DATA_GAPS';

export const empReviewGroupMap: Partial<Record<EmpTaskKey, EmpReviewGroup>> = {
  serviceContactDetails: 'SERVICE_CONTACT_DETAILS',
  operatorDetails: 'OPERATOR_DETAILS',

  flightAndAircraftProcedures: 'FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES',
  emissionsMonitoringApproach: 'MONITORING_APPROACH',
  emissionsReductionClaim: 'EMISSIONS_REDUCTION_CLAIM',
  emissionSources: 'EMISSION_SOURCES',
  methodAProcedures: 'METHOD_A_PROCEDURES',
  methodBProcedures: 'METHOD_B_PROCEDURES',
  blockOnBlockOffMethodProcedures: 'BLOCK_ON_OFF_PROCEDURES',
  fuelUpliftMethodProcedures: 'FUEL_UPLIFT_PROCEDURES',
  blockHourMethodProcedures: 'BLOCK_HOUR_PROCEDURES',
  dataGaps: 'DATA_GAPS',

  managementProcedures: 'MANAGEMENT_PROCEDURES',

  abbreviations: 'ABBREVIATIONS_AND_DEFINITIONS',
  additionalDocuments: 'ADDITIONAL_DOCUMENTS',

  applicationTimeframeInfo: 'LATE_SUBMISSION',
};

export const empHeaderTaskMap: Partial<Record<EmpTaskKey, string>> = {
  serviceContactDetails: 'Service contact details',
  operatorDetails: 'Operator details',

  flightAndAircraftProcedures: 'Flights and aircraft monitoring procedures',
  emissionsMonitoringApproach: 'Monitoring approach',
  emissionsReductionClaim: 'Emissions reduction claim',
  emissionSources: 'Emission sources',
  methodAProcedures: 'Method A procedures',
  methodBProcedures: 'Method B procedures',
  blockOnBlockOffMethodProcedures: 'Block-off / Block-on procedures',
  fuelUpliftMethodProcedures: 'Fuel uplift procedures',
  blockHourMethodProcedures: 'Block-hour procedures',
  dataGaps: 'Data gaps when monitoring fuel use',

  managementProcedures: 'Management procedures',

  abbreviations: 'Abbreviations and definitions',
  additionalDocuments: 'Additional documents and information',

  applicationTimeframeInfo: 'When you need to apply',

  submission: 'Send application to regulator',

  decision: 'Overall decision',

  empVariationDetails: 'Describe the changes',

  changesRequested: 'Changes requested by the regulator',
};

export const addedMethodTasks: string[] = [
  'dataGaps',
  'methodAProcedures',
  'methodBProcedures',
  'blockOnBlockOffMethodProcedures',
  'fuelUpliftMethodProcedures',
  'blockHourMethodProcedures',
];

const variationSubmissionTaskStatusRequestTaskTypes: RequestTaskDTO['type'][] = [
  'EMP_VARIATION_UKETS_APPLICATION_SUBMIT',
  'EMP_VARIATION_CORSIA_APPLICATION_SUBMIT',
  'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT',
  'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT',
];

export const variationSubmitRegulatorLedRequestTaskTypes: RequestTaskDTO['type'][] = [
  'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT',
  'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT',
];

export const variationOperatorLedReviewRequestTaskTypes: RequestTaskDTO['type'][] = [
  'EMP_VARIATION_UKETS_APPLICATION_REVIEW',
  'EMP_VARIATION_CORSIA_APPLICATION_REVIEW',
];

export function notifyOperatorRequestTaskActionTypesMap(
  requestTaskType: RequestTaskDTO['type'],
): RequestTaskActionProcessDTO['requestTaskActionType'] {
  switch (requestTaskType) {
    case 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW':
      return 'EMP_ISSUANCE_UKETS_NOTIFY_OPERATOR_FOR_DECISION';

    case 'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW':
      return 'EMP_ISSUANCE_CORSIA_NOTIFY_OPERATOR_FOR_DECISION';

    case 'EMP_VARIATION_UKETS_APPLICATION_REVIEW':
      return 'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION';

    case 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW':
      return 'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION';

    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT':
      return 'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED';

    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT':
      return 'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED';
  }
}

export function getTaskStatusByTaskCompletionState(
  taskName: EmpTaskKey,
  payload: EmpIssuanceApplicationSubmitRequestTaskPayload &
    EmpVariationUkEtsApplicationSubmitRequestTaskPayload &
    EmpVariationCorsiaApplicationSubmitRequestTaskPayload,
): TaskItemStatus {
  const completionState = payload.empSectionsCompleted[taskName];
  const completionVariation = payload.empVariationDetailsCompleted;

  const isCorsia = payloadIsCorsia(payload);
  switch (taskName) {
    case 'serviceContactDetails':
      return completionState != null ? (completionState[0] ? 'complete' : 'in progress') : 'in progress';

    case 'empVariationDetails':
      return completionVariation != null ? (completionVariation ? 'complete' : 'in progress') : 'not started';

    case 'dataGaps':
      if (isCorsia) return extractCorsiaDataGapsStatus(payload, completionState);
    // eslint-disable-next-line no-fallthrough
    case 'emissionSources':
      if (completionState && !completionState[0]) return extractEmissionSourcesStatus(payload);
    // eslint-disable-next-line no-fallthrough
    default:
      return completionState != null ? (completionState[0] ? 'complete' : 'in progress') : 'not started';
  }
}

export function areTasksCompletedForNotifyVariationRegLed(
  taskType: RequestTaskDTO['type'],
  payload: RequestTaskPayload,
  isCorsia: boolean,
): boolean {
  const availableTasks = getAvailableSubTasks(allEmpApplicationTasks(taskType, false, isCorsia), payload);
  const allEmpTasksCompleted = empTasksCompleted(payload, availableTasks);

  return allEmpTasksCompleted && (payload as EmpVariationSubmitRequestTaskPayload).empVariationDetailsCompleted;
}

export function resolveSubmissionTaskStatus(
  taskType: RequestTaskDTO['type'],
  payload: EmpVariationUkEtsApplicationSubmitRequestTaskPayload | EmpIssuanceApplicationSubmitRequestTaskPayload,
  availableTasks: string[],
): TaskItemStatus {
  const allEmpTasksCompleted = empTasksCompleted(payload, availableTasks);

  return variationSubmissionTaskStatusRequestTaskTypes.includes(taskType)
    ? allEmpTasksCompleted && payload.empVariationDetailsCompleted
      ? 'not started'
      : 'cannot start yet'
    : allEmpTasksCompleted
    ? 'not started'
    : 'cannot start yet';
}

function resolveSubmissionTaskLink(
  taskType: RequestTaskDTO['type'],
  payload: EmpVariationUkEtsApplicationSubmitRequestTaskPayload &
    EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload &
    EmpVariationCorsiaApplicationSubmitRequestTaskPayload &
    EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload,
  availableTasks: string[],
  isCorsia?: boolean,
): string {
  const allEmpTasksCompleted = empTasksCompleted(payload, availableTasks);
  const prefix = isCorsia ? 'emp-corsia' : 'emp';

  return variationSubmissionTaskStatusRequestTaskTypes.includes(taskType)
    ? allEmpTasksCompleted && payload.empVariationDetailsCompleted
      ? `${prefix}/variation/submission`
      : null
    : allEmpTasksCompleted
    ? `${prefix}/submit/submission`
    : null;
}

function resolveCorsiaDataGapsStatus(payload: EmpRequestTaskPayloadCorsia): TaskItemStatus {
  if (!monitoringApproachCorsiaCompleted(payload)) return 'cannot start yet';
  if (payload.empSectionsCompleted['dataGaps'] && payload.empSectionsCompleted['dataGaps'][0]) {
    return 'complete';
  } else {
    const dataGapsStatus = payload.empSectionsCompleted['dataGaps'];
    if (!dataGapsStatus) return 'not started';
    return dataGapsStatus[0] ? 'complete' : 'in progress';
  }
}

function resolveCorsiaDataGapsTaskLink(
  payload: EmpRequestTaskPayloadCorsia,
  taskType: RequestTaskDTO['type'],
): string | null {
  const prefix =
    taskType === 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT' ? 'variation-regulator-led' : 'submit';
  return monitoringApproachCorsiaCompleted(payload) ? `emp-corsia/${prefix}/data-gaps` : null;
}

function empTasksCompleted(
  payload: EmpVariationUkEtsApplicationSubmitRequestTaskPayload &
    EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload &
    EmpVariationCorsiaApplicationSubmitRequestTaskPayload &
    EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload,
  availableTasks: string[],
): boolean {
  const empSectionsCompleted = payload.empSectionsCompleted;

  return (
    Object.keys(empSectionsCompleted).length >= availableTasks.length &&
    Object.values(empSectionsCompleted).every((tc) => tc[0] === true)
  );
}

const resolveDeterminationTaskStatus = (determination: EmpIssuanceDetermination, determinationCompleted: boolean) => {
  return determinationCompleted
    ? determination?.type === 'APPROVED'
      ? 'approved'
      : determination?.type === 'DEEMED_WITHDRAWN'
      ? 'withdrawn'
      : 'undecided'
    : 'undecided';
};

const resolveReviewTaskStatus = (decision: EmpVariationDetermination, determinationCompleted: boolean) => {
  if (determinationCompleted) {
    switch (decision?.type) {
      case 'APPROVED':
        return 'approved';

      case 'REJECTED':
        return 'rejected';

      case 'DEEMED_WITHDRAWN':
        return 'withdrawn';

      default:
        return 'undecided';
    }
  } else {
    return 'undecided';
  }
};

function getEmpReviewTaskStatusByTaskCompletionState(
  decision: EmpIssuanceReviewDecision,
  completionState: boolean,
  submitStatus: TaskItemStatus,
): TaskItemStatus {
  if (submitStatus === 'needs review' || submitStatus === 'in progress') return submitStatus;
  else if (decision && completionState) {
    if (decision.type === 'ACCEPTED') return 'accepted';
    else return 'operator to amend';
  } else return 'undecided';
}

function getEmpVariationReviewTaskStatusByTaskCompletionState(
  decision: EmpVariationReviewDecision,
  completionState: boolean,
  submitStatus: TaskItemStatus,
): TaskItemStatus {
  if (submitStatus === 'needs review' || submitStatus === 'in progress') return submitStatus;

  if (decision && completionState) {
    switch (decision.type) {
      case 'ACCEPTED':
        return 'accepted';

      case 'REJECTED':
        return 'rejected';

      case 'OPERATOR_AMENDS_NEEDED':
        return 'operator to amend';

      default:
        return 'undecided';
    }
  } else {
    return 'undecided';
  }
}

export function getAvailableSubTasks(
  taskSections: TaskSection<any>[],
  payload: EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload | EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload,
): EmpTaskKey[] {
  return taskSections
    .map((t) => t.tasks)
    .reduce((acc, tasks) => acc.concat(tasks), [])
    .filter((task) => showTask(task, payload))
    .map((task) => task.name as EmpTaskKey);
}
/**
 * Emission sources status depends on Monitoring Approach. If the approach selected is FUMM and the user has not populated
 * the emission sources for properly status should show 'needs review'
 */
function extractEmissionSourcesStatus(payload: EmpIssuanceApplicationSubmitRequestTaskPayload): TaskItemStatus {
  const monitoringApproachType = extractMonitorinApproachType(payload?.emissionsMonitoringPlan);
  if (!monitoringApproachType || monitoringApproachType !== 'FUEL_USE_MONITORING') return 'in progress';

  const aircraftTypes = payload.emissionsMonitoringPlan?.emissionSources?.aircraftTypes;
  if (aircraftTypes?.some((at) => !at.fuelConsumptionMeasuringMethod)) return 'needs review';
  return 'in progress';
}

function extractCorsiaDataGapsStatus(payload: EmpRequestTaskPayloadCorsia, completionState: [boolean]): TaskItemStatus {
  const monitoringApproachStatus = extractMonitorinApproachType(payload.emissionsMonitoringPlan);
  if (!monitoringApproachStatus || !monitoringApproachStatus[0]) {
    return 'cannot start yet';
  } else {
    return completionState != null ? (completionState[0] ? 'complete' : 'in progress') : 'not started';
  }
}

export function getEmpSections(
  payload: EmpIssuanceApplicationSubmitRequestTaskPayload,
  taskType: RequestTaskDTO['type'],
  isAmendsTask?: boolean,
  relatedActions?: RequestTaskItemDTO['allowedRequestTaskActions'],
  isCorsia?: boolean,
): TaskSection<any>[] {
  const availableSubTasks = getAvailableSubTasks(allEmpApplicationTasks(taskType, isAmendsTask, isCorsia), payload);

  let sections: TaskSection<any>[];

  switch (taskType) {
    case 'EMP_VARIATION_UKETS_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_CORSIA_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT':
    case 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT':
      sections = empVariationSubmitTasks(isAmendsTask, isCorsia);
      break;

    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT':
    case 'EMP_VARIATION_UKETS_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW':
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW':
      sections = empVariationSubmitRegulatorLedTasks(isAmendsTask, isCorsia);
      break;

    default:
      sections = empApplicationSubmitTasks(isAmendsTask, isCorsia);
  }

  return sections.map((section) => {
    return {
      ...section,
      tasks: section.tasks
        .filter((task) => showTask(task, payload))
        .map((task) => {
          let status = getTaskStatusByTaskCompletionState(task.name as EmpTaskKey, payload);

          const isVariationApplicationSubmit = [
            'EMP_VARIATION_UKETS_APPLICATION_SUBMIT',
            'EMP_VARIATION_CORSIA_APPLICATION_SUBMIT',
          ].includes(taskType);

          if (isVariationApplicationSubmit && addedMethodTasks.includes(task.name)) {
            status = 'complete';
          }
          let link: string;

          if (status !== 'complete') {
            link = task.link;
          } else {
            // if a task is completed always redirect to summary, except serviceContactDetails and changesRequested
            link = ['serviceContactDetails', 'changesRequested'].includes(task.name)
              ? task.link
              : `${task.link}/summary`;
          }

          if (task.name === 'submission') {
            status = resolveSubmissionTaskStatus(taskType, payload, availableSubTasks);
            link = relatedActions?.length > 0 ? resolveSubmissionTaskLink(taskType, payload, availableSubTasks) : null;
          }
          if (task.name === 'dataGaps' && isCorsia) {
            status = resolveCorsiaDataGapsStatus(payload);
            link = resolveCorsiaDataGapsTaskLink(payload, taskType);
          }
          return { ...task, status, link };
        }),
    };
  });
}

export function getEmpUkEtsReviewSections(
  payload: EmpIssuanceUkEtsApplicationReviewRequestTaskPayload,
): TaskSection<any>[] {
  return EMP_APPLICATION_REVIEW_TASKS.map((section) => {
    return {
      ...section,
      tasks: section.tasks
        .filter((task) => showTask(task, payload))
        .map((task) => {
          const submitStatus = getTaskStatusByTaskCompletionState(task.name as EmpTaskKey, payload);

          const link =
            submitStatus === 'complete'
              ? (task.name as EmpTaskKey) === 'serviceContactDetails'
                ? task.link
                : `${task.link}/summary`
              : task.link;

          const reviewKey = empReviewGroupMap[task.name as EmpTaskKey];

          const status = getEmpReviewTaskStatusByTaskCompletionState(
            payload.reviewGroupDecisions[reviewKey],
            payload.reviewSectionsCompleted[reviewKey],
            submitStatus,
          );

          return !task.status
            ? { ...task, status, link }
            : {
                ...task,
                status: resolveDeterminationTaskStatus(
                  payload.determination,
                  payload.reviewSectionsCompleted['decision'],
                ) as any,
                link: 'emp/review/decision/summary',
              };
        }),
    };
  });
}

export function getEmpCorsiaReviewSections(
  payload: EmpIssuanceCorsiaApplicationReviewRequestTaskPayload,
): TaskSection<any>[] {
  return EMP_CORSIA_APPLICATION_REVIEW_TASKS.map((section) => {
    return {
      ...section,
      tasks: section.tasks
        .filter((task) => showTask(task, payload))
        .map((task) => {
          const submitStatus = getTaskStatusByTaskCompletionState(task.name as EmpCorsiaTaskKey, payload);

          const link =
            submitStatus === 'complete'
              ? (task.name as EmpCorsiaTaskKey) === 'serviceContactDetails'
                ? task.link
                : `${task.link}/summary`
              : task.link;

          const reviewKey = empReviewGroupMap[task.name as EmpCorsiaTaskKey];

          const status = getEmpReviewTaskStatusByTaskCompletionState(
            payload.reviewGroupDecisions[reviewKey],
            payload.reviewSectionsCompleted[reviewKey],
            submitStatus,
          );

          return !task.status
            ? { ...task, status, link }
            : {
                ...task,
                status: resolveDeterminationTaskStatus(
                  payload.determination,
                  payload.reviewSectionsCompleted['decision'],
                ) as any,
                link: 'emp-corsia/review/decision/summary',
              };
        }),
    };
  });
}

export function getEmpVariationReviewSections(
  payload: (
    | EmpVariationUkEtsApplicationReviewRequestTaskPayload
    | EmpVariationCorsiaApplicationReviewRequestTaskPayload
  ) &
    EmpIssuanceApplicationSubmitRequestTaskPayload,
  isCorsia?: boolean,
): TaskSection<any>[] {
  const prefix = isCorsia ? 'emp-corsia' : 'emp';

  return empVariationReviewTask(isCorsia).map((section) => {
    return {
      ...section,
      tasks: section.tasks
        .filter((task) => showTask(task, payload))
        .map((task) => {
          const submitStatus = getTaskStatusByTaskCompletionState(task.name as EmpTaskKey, payload);

          const link =
            submitStatus === 'complete'
              ? (task.name as EmpTaskKey) === 'serviceContactDetails'
                ? task.link
                : `${task.link}/summary`
              : task.link;

          const reviewKey = empReviewGroupMap[task.name as EmpTaskKey];

          let status: TaskItemStatus;
          status = getEmpVariationReviewTaskStatusByTaskCompletionState(
            payload.reviewGroupDecisions[reviewKey],
            payload.reviewSectionsCompleted[reviewKey],
            submitStatus,
          );

          if (task.name === 'empVariationDetails') {
            if (payload.empVariationDetailsReviewDecision && payload.empVariationDetailsReviewCompleted) {
              switch (payload.empVariationDetailsReviewDecision.type) {
                case 'ACCEPTED':
                  status = 'accepted';
                  break;

                case 'REJECTED':
                  status = 'rejected';
                  break;

                case 'OPERATOR_AMENDS_NEEDED':
                  status = 'operator to amend';
                  break;
              }
            } else {
              status = 'undecided';
            }
          }

          return !task.status
            ? { ...task, status, link }
            : {
                ...task,
                status: resolveReviewTaskStatus(
                  payload.determination,
                  payload.reviewSectionsCompleted['decision'],
                ) as any,
                link: `${prefix}/variation/review/decision/summary`,
              };
        }),
    };
  });
}

export function allEmpApplicationTasks(
  parentLink: string,
  isAmendsTask?: boolean,
  isCorsia?: boolean,
): TaskSection<any>[] {
  const prefix = isCorsia ? 'emp-corsia' : 'emp';
  const tasks: TaskSection<any>[] = [
    {
      title: 'Account details',
      tasks: [
        {
          name: 'serviceContactDetails',
          linkText: empHeaderTaskMap['serviceContactDetails'],
          link: `${prefix}/${parentLink}/service-contact-details`,
        },
        {
          name: 'operatorDetails',
          linkText: empHeaderTaskMap['operatorDetails'],
          link: `${prefix}/${parentLink}/operator-details`,
        },
      ],
    },
    {
      title: 'Emissions monitoring',
      tasks: [
        {
          name: 'flightAndAircraftProcedures',
          linkText: empHeaderTaskMap['flightAndAircraftProcedures'],
          link: `${prefix}/${parentLink}/flight-procedures`,
        },
        {
          name: 'emissionsMonitoringApproach',
          linkText: empHeaderTaskMap['emissionsMonitoringApproach'],
          link: `${prefix}/${parentLink}/monitoring-approach`,
        },
        ...(!isCorsia
          ? [
              {
                name: 'emissionsReductionClaim',
                linkText: empHeaderTaskMap['emissionsReductionClaim'],
                link: `${prefix}/${parentLink}/emissions-reduction-claim`,
              },
            ]
          : []),
        {
          name: 'emissionSources',
          linkText: empHeaderTaskMap['emissionSources'],
          link: `${prefix}/${parentLink}/emission-sources`,
        },
        {
          name: 'methodAProcedures',
          linkText: empHeaderTaskMap['methodAProcedures'],
          link: `${prefix}/${parentLink}/method-a-procedures`,
          type: 'METHOD_A',
        },
        {
          name: 'methodBProcedures',
          linkText: empHeaderTaskMap['methodBProcedures'],
          link: `${prefix}/${parentLink}/method-b-procedures`,
          type: 'METHOD_B',
        },
        {
          name: 'blockOnBlockOffMethodProcedures',
          linkText: empHeaderTaskMap['blockOnBlockOffMethodProcedures'],
          link: `${prefix}/${parentLink}/block-on-off-procedures`,
          type: 'BLOCK_ON_BLOCK_OFF',
        },
        {
          name: 'fuelUpliftMethodProcedures',
          linkText: empHeaderTaskMap['fuelUpliftMethodProcedures'],
          link: `${prefix}/${parentLink}/fuel-uplift-procedures`,
          type: 'FUEL_UPLIFT',
        },
        {
          name: 'blockHourMethodProcedures',
          linkText: empHeaderTaskMap['blockHourMethodProcedures'],
          link: `${prefix}/${parentLink}/block-hour-procedures`,
          type: 'BLOCK_HOUR',
        },
        {
          name: 'dataGaps',
          linkText: isCorsia ? 'Data Gaps' : empHeaderTaskMap['dataGaps'],
          link: `${prefix}/${parentLink}/data-gaps`,
        },
      ],
    },
    {
      title: 'Management procedures',
      tasks: [
        {
          name: 'managementProcedures',
          linkText: empHeaderTaskMap['managementProcedures'],
          link: `${prefix}/${parentLink}/management-procedures`,
        },
      ],
    },
    {
      title: 'Additional information',
      tasks: [
        {
          name: 'abbreviations',
          linkText: empHeaderTaskMap['abbreviations'],
          link: `${prefix}/${parentLink}/abbreviations`,
        },
        {
          name: 'additionalDocuments',
          linkText: empHeaderTaskMap['additionalDocuments'],
          link: `${prefix}/${parentLink}/additional-docs`,
        },
      ],
    },
    ...(!isCorsia
      ? [
          {
            title: 'Application timeframe',
            tasks: [
              {
                name: 'applicationTimeframeInfo',
                linkText: empHeaderTaskMap['applicationTimeframeInfo'],
                link: `${prefix}/${parentLink}/application-timeframe-apply`,
              },
            ],
          },
        ]
      : []),
  ];

  if (isAmendsTask) {
    tasks.unshift({
      title: 'Regulator comments',
      tasks: [
        {
          name: 'changesRequested',
          linkText: empHeaderTaskMap['changesRequested'],
          link: `${prefix}/submit/changes-requested`,
        },
      ],
    });
  }

  return tasks;
}

export function showTask(task: TaskItem<any>, payload: EmpIssuanceApplicationSubmitRequestTaskPayload): boolean {
  const isCorsia = payloadIsCorsia(payload);
  switch (task.name) {
    case 'dataGaps':
      return isCorsia || isFUMM(payload);
    default:
      // task.type shows a dependency between tasks. If value is falsy task should be always visible
      return !task.type || isFuelConsumptionMeasuringMethod(task, payload);
  }
}
function isFuelConsumptionMeasuringMethod(
  task: TaskItem<unknown>,
  payload: EmpIssuanceApplicationSubmitRequestTaskPayload,
): boolean {
  return (
    task.type &&
    payload.emissionsMonitoringPlan.emissionsMonitoringApproach?.monitoringApproachType === 'FUEL_USE_MONITORING' &&
    payload.emissionsMonitoringPlan.emissionSources?.aircraftTypes?.some(
      (type) => type.fuelConsumptionMeasuringMethod === task.type,
    )
  );
}

export const empApplicationSubmitTasks = (isAmendsTask: boolean, isCorsia: boolean): TaskSection<any>[] => {
  const prefix = isCorsia ? 'emp-corsia' : 'emp';

  const tasks = [
    ...allEmpApplicationTasks('submit', false, isCorsia),
    {
      title: 'Send application',
      tasks: [
        {
          name: 'submission',
          link: null,
          linkText: empHeaderTaskMap['submission'],
          status: 'cannot start yet',
        },
      ],
    },
  ] as TaskSection<any>[];

  if (isAmendsTask) {
    tasks.unshift({
      title: 'Regulator comments',
      tasks: [
        {
          link: `${prefix}/submit/changes-requested`,
          linkText: empHeaderTaskMap['changesRequested'],
          name: 'changesRequested',
        },
      ],
    });
  }

  return tasks;
};

export const empVariationSubmitTasks = (isAmendsTask: boolean, isCorsia: boolean): TaskSection<any>[] => {
  const prefix = isCorsia ? 'emp-corsia' : 'emp';

  let tasks: TaskSection<any>[];

  if (isAmendsTask) {
    tasks = [
      {
        title: 'Regulator comments',
        tasks: [
          {
            link: `${prefix}/variation/changes-requested`,
            linkText: empHeaderTaskMap['changesRequested'],
            name: 'changesRequested',
          },
        ],
      },
      {
        title: 'Variation details',
        tasks: [
          {
            name: 'empVariationDetails',
            link: `${prefix}/variation/variation-details`,
            linkText: empHeaderTaskMap['empVariationDetails'],
          },
        ],
      },
      ...allEmpApplicationTasks('variation', false, isCorsia),
      {
        title: 'Send application',
        tasks: [
          {
            name: `submission`,
            link: null,
            linkText: empHeaderTaskMap['submission'],
            status: 'cannot start yet',
          },
        ],
      },
    ];
  } else {
    tasks = [
      {
        title: 'Variation details',
        tasks: [
          {
            name: 'empVariationDetails',
            link: `${prefix}/variation/variation-details`,
            linkText: empHeaderTaskMap['empVariationDetails'],
          },
        ],
      },
      ...allEmpApplicationTasks('variation', false, isCorsia),
      {
        title: 'Send application',
        tasks: [
          {
            name: `submission`,
            link: null,
            linkText: empHeaderTaskMap['submission'],
            status: 'cannot start yet',
          },
        ],
      },
    ];
  }

  return tasks;
};

export const empVariationSubmitRegulatorLedTasks = (isAmendsTask: boolean, isCorsia: boolean): TaskSection<any>[] => {
  const prefix = isCorsia ? 'emp-corsia' : 'emp';
  return [
    {
      title: 'Variation details',
      tasks: [
        {
          name: 'empVariationDetails',
          link: `${prefix}/variation-regulator-led/variation-details`,
          linkText: empHeaderTaskMap['empVariationDetails'],
        },
      ],
    },
    ...allEmpApplicationTasks('variation-regulator-led', isAmendsTask, isCorsia),
  ];
};
export const EMP_APPLICATION_REVIEW_TASKS: TaskSection<any>[] = [
  ...allEmpApplicationTasks('review'),
  {
    title: 'Decision',
    tasks: [
      {
        name: 'decision',
        link: null,
        linkText: empHeaderTaskMap['decision'],
        status: 'undecided',
      },
    ],
  },
];

export const EMP_CORSIA_APPLICATION_REVIEW_TASKS: TaskSection<any>[] = [
  ...allEmpApplicationTasks('review', null, true),
  {
    title: 'Decision',
    tasks: [
      {
        name: 'decision',
        link: null,
        linkText: empHeaderTaskMap['decision'],
        status: 'undecided',
      },
    ],
  },
];

export const EMP_VARIATION_REVIEW_TASKS: TaskSection<any>[] = [
  {
    title: 'Variation details',
    tasks: [
      {
        name: 'empVariationDetails',
        link: `emp/variation/review/variation-details`,
        linkText: empHeaderTaskMap['empVariationDetails'],
      },
    ],
  },
  ...allEmpApplicationTasks('variation/review'),
  {
    title: 'Decision',
    tasks: [
      {
        name: 'decision',
        link: null,
        linkText: empHeaderTaskMap['decision'],
        status: 'undecided',
      },
    ],
  },
];

const empVariationReviewTask = (isCorsia: boolean): TaskSection<any>[] => {
  const prefix = isCorsia ? 'emp-corsia' : 'emp';

  return [
    {
      title: 'Variation details',
      tasks: [
        {
          name: 'empVariationDetails',
          link: `${prefix}/variation/review/variation-details`,
          linkText: empHeaderTaskMap['empVariationDetails'],
        },
      ],
    },
    ...allEmpApplicationTasks('variation/review', null, isCorsia),
    {
      title: 'Decision',
      tasks: [
        {
          name: 'decision',
          link: null,
          linkText: empHeaderTaskMap['decision'],
          status: 'undecided',
        },
      ],
    },
  ];
};

export const isAllSectionsApproved = (
  payload: EmpIssuanceUkEtsApplicationReviewRequestTaskPayload & EmpVariationUkEtsApplicationReviewRequestTaskPayload,
  type: RequestTaskDTO['type'],
) => {
  const sections =
    type === 'EMP_VARIATION_UKETS_APPLICATION_REVIEW'
      ? getEmpVariationReviewSections(payload)
      : getEmpUkEtsReviewSections(payload);
  const notApprovedSections = sections
    .map((section) => section.tasks)
    .filter((task) => task.some((item) => item.status !== 'accepted' && item.name !== 'decision'));

  return notApprovedSections.length === 0;
};

export const isAllCorsiaSectionsApproved = (
  payload: EmpIssuanceCorsiaApplicationReviewRequestTaskPayload & EmpVariationCorsiaApplicationReviewRequestTaskPayload,
  type: RequestTaskDTO['type'],
) => {
  const sections =
    type === 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW'
      ? getEmpVariationReviewSections(payload, true)
      : getEmpCorsiaReviewSections(payload);

  const notApprovedSections = sections
    .map((section) => section.tasks)
    .filter((task) => task.some((item) => item.status !== 'accepted' && item.name !== 'decision'));

  return notApprovedSections.length === 0;
};

export const isEvenOneSectionRejected = (payload: EmpVariationUkEtsApplicationReviewRequestTaskPayload) => {
  const rejectedSections = getEmpVariationReviewSections(payload)
    .map((section) => section.tasks)
    .filter((task) => task.some((item) => item.status === 'rejected' && item.name !== 'decision'));

  const toAmendOrUndecidedSections = getEmpVariationReviewSections(payload)
    .map((section) => section.tasks)
    .filter((task) =>
      task.some(
        (item) => (item.status === 'operator to amend' || item.status === 'undecided') && item.name !== 'decision',
      ),
    );

  return rejectedSections.length > 0 && toAmendOrUndecidedSections.length === 0;
};

export const isEvenOneCorsiaSectionRejected = (payload: EmpVariationCorsiaApplicationReviewRequestTaskPayload) => {
  const rejectedSections = getEmpVariationReviewSections(payload, true)
    .map((section) => section.tasks)
    .filter((task) => task.some((item) => item.status === 'rejected' && item.name !== 'decision'));
  const toAmendOrUndecidedSections = getEmpVariationReviewSections(payload, true)
    .map((section) => section.tasks)
    .filter((task) =>
      task.some(
        (item) => (item.status === 'operator to amend' || item.status === 'undecided') && item.name !== 'decision',
      ),
    );

  return rejectedSections.length > 0 && toAmendOrUndecidedSections.length === 0;
};

export function getVariationScheduleItems(
  reviewDecision: { [key: string]: EmpVariationReviewDecision },
  varDetailsReviewDecision: EmpVariationReviewDecision,
): string[] {
  const aboutVariationScheduleItems: string[] =
    (reviewDecision as any)?.details?.variationScheduleItems ||
    (varDetailsReviewDecision as any)?.details?.variationScheduleItems ||
    [];

  const applicableReviewGroups = Object.values(empReviewGroupMap).filter((reviewGroup) =>
    Object.keys(reviewDecision).includes(reviewGroup),
  );
  const reviewGroupVariationScheduleItems = applicableReviewGroups.reduce(
    (result, reviewGroup) => [
      ...result,
      ...(((reviewDecision as any)[reviewGroup]?.details?.variationScheduleItems ||
        (varDetailsReviewDecision as any)?.details?.variationScheduleItems) ??
        []),
    ],
    [],
  ) as string[];

  return [...aboutVariationScheduleItems, ...reviewGroupVariationScheduleItems];
}

function payloadIsCorsia(
  payload: EmpRequestTaskPayloadCorsia | EmpRequestTaskPayloadUkEts,
): payload is EmpRequestTaskPayloadCorsia {
  // model-wise, corsia payloads do not have a crco code
  return (
    payload.emissionsMonitoringPlan.operatorDetails && !('crcoCode' in payload.emissionsMonitoringPlan.operatorDetails)
  );
}

export function monitoringApproachCorsiaCompleted(payload: EmpRequestTaskPayloadCorsia): boolean {
  return (
    payload?.empSectionsCompleted?.['emissionsMonitoringApproach'] &&
    payload?.empSectionsCompleted?.['emissionsMonitoringApproach'][0]
  );
}
