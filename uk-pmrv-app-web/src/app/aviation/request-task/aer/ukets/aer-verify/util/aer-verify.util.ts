import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import {
  getAerVerifySendReportSection,
  getAerVerifyVerifierFindings,
  getVerifierAssessmentTasks,
} from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { AerVerifyTaskKey } from '@aviation/request-task/store';
import { TaskItem, TaskItemStatus, TaskSection } from '@shared/task-list/task-list.interface';

import { AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export function getAerVerifySections(
  payload?: AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload,
): TaskSection<any>[] {
  const assesmentSections = getVerifierAssessmentTasks(false, payload?.verificationReport?.safExists);
  const findingsSections = getAerVerifyVerifierFindings(false);
  const sendReportSections = getAerVerifySendReportSection(false);
  const allSections = [...assesmentSections, ...findingsSections, ...sendReportSections];

  const availableSubTasks = getAvailableSubTasks(allSections);

  const allAerVerifyTasksCompleted = availableSubTasks.every(
    (subtask) => payload.verificationSectionsCompleted[subtask]?.[0] === true,
  );

  const sectionsToReturn = allSections.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(task.name as AerVerifyTaskKey, payload);
        const link = getLinkByTaskAndStatus(status, task);

        return !task.status
          ? { ...task, status, link }
          : {
              ...task,
              status: resolveSubmissionTaskStatus(payload.verificationSectionsCompleted, availableSubTasks),
              link: allAerVerifyTasksCompleted ? 'aer-verify/send-report' : null,
            };
      }),
    };
  });

  // If exists, put report section at the end of the sections array
  const reportSection = sectionsToReturn.splice(
    sectionsToReturn.findIndex((s) => s.title === 'Complete report'),
    1,
  );

  return [...sectionsToReturn, ...AER_APPLICATION_TASK_SECTIONS_SUMMARIES, ...(reportSection ?? null)];
}

export function getAvailableSubTasks(taskSections: TaskSection<any>[]): AerVerifyTaskKey[] {
  return taskSections
    .map((t) => t.tasks)
    .reduce((acc, tasks) => acc.concat(tasks), [])
    .filter((task) => task.name !== 'sendReport')
    .map((task) => task.name as AerVerifyTaskKey);
}

export function getTaskStatusByTaskCompletionState(
  taskName: AerVerifyTaskKey,
  payload: AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload,
): TaskItemStatus {
  const completionState = payload.verificationSectionsCompleted[taskName];

  const sections = [
    ...getVerifierAssessmentTasks(false, payload.verificationReport.safExists),
    ...getAerVerifyVerifierFindings(false),
  ];

  switch (taskName) {
    case 'sendReport': {
      const availableSubTasks = getAvailableSubTasks(sections);

      return resolveSubmissionTaskStatus(payload.verificationSectionsCompleted, availableSubTasks);
    }

    default:
      return completionState != null ? (completionState[0] ? 'complete' : 'in progress') : 'not started';
  }
}

export function getLinkByTaskAndStatus(status: TaskItemStatus, task: TaskItem<any>): string {
  switch (task.name as AerVerifyTaskKey) {
    case 'sendReport': {
      if (status === 'cannot start yet') {
        return (task.link = null);
      } else {
        return task.link;
      }
    }

    default:
      return task.link;
  }
}

export function resolveSubmissionTaskStatus(
  verificationSectionsCompleted: { [key: string]: boolean[] },
  availableSubTasks: AerVerifyTaskKey[],
): TaskItemStatus {
  return availableSubTasks.every((subTask) => verificationSectionsCompleted[subTask]?.[0] === true)
    ? 'not started'
    : 'cannot start yet';
}

export const AER_APPLICATION_TASK_SECTIONS_SUMMARIES: TaskSection<any>[] = [
  {
    title: 'Identification',
    tasks: [
      {
        name: 'serviceContactDetails',
        linkText: aerHeaderTaskMap['serviceContactDetails'],
        link: 'aer-verify/service-contact-details-summary',
        status: 'complete',
      },
      {
        name: 'operatorDetails',
        linkText: aerHeaderTaskMap['operatorDetails'],
        link: `aer-verify/operator-details-summary`,
        status: 'complete',
      },
    ],
  },
  {
    title: 'Emissions overview',
    tasks: [
      {
        name: 'aerMonitoringPlanChanges',
        linkText: aerHeaderTaskMap['aerMonitoringPlanChanges'],
        link: 'aer-verify/monitoring-plan-changes-summary',
        status: 'complete',
      },
      {
        name: 'monitoringApproach',
        linkText: aerHeaderTaskMap['monitoringApproach'],
        link: 'aer-verify/monitoring-approach-summary',
        status: 'complete',
      },
      {
        name: 'aggregatedEmissionsData',
        linkText: aerHeaderTaskMap['aggregatedEmissionsData'],
        link: 'aer-verify/aggregated-consumption-flight-data-summary',
        status: 'complete',
      },
      {
        name: 'aviationAerAircraftData',
        linkText: aerHeaderTaskMap['aviationAerAircraftData'],
        link: 'aer-verify/aircraft-types-data-summary',
        status: 'complete',
      },
      {
        name: 'saf',
        linkText: aerHeaderTaskMap['saf'],
        link: 'aer-verify/saf-summary',
        status: 'complete',
      },
      {
        name: 'dataGaps',
        linkText: aerHeaderTaskMap['dataGaps'],
        link: 'aer-verify/data-gaps-summary',
        status: 'complete',
      },
    ],
  },
  {
    title: 'Additional information',
    tasks: [
      {
        name: 'additionalDocuments',
        linkText: aerHeaderTaskMap['additionalDocuments'],
        link: 'aer-verify/additional-documents-summary',
        status: 'complete',
      },
    ],
  },
  {
    title: 'Emissions for the scheme year',
    tasks: [
      {
        name: 'aviationAerTotalEmissionsConfidentiality',
        linkText: aerHeaderTaskMap['aviationAerTotalEmissionsConfidentiality'],
        link: 'aer-verify/total-emissions-summary',
        status: 'complete',
      },
    ],
  },
];
