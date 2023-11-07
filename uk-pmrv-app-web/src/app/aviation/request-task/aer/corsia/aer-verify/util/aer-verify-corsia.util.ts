import { filterOutTaskSections, getAerSections } from '@aviation/request-task/aer/shared/util/aer.util';
import {
  getAerVerifyCorsiaVerifiedEmissions,
  getAerVerifyCorsiaVerifierSummary,
  getAerVerifySendReportSection,
  getAerVerifyVerifierFindings,
  getVerifierAssessmentTasks,
} from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { AerVerifyCorsiaTaskKey } from '@aviation/request-task/store';
import { TaskItemStatus, TaskSection } from '@shared/task-list/task-list.interface';

import { AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export function getAerVerifyCorsiaSections(
  payload: AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
): TaskSection<any>[] {
  const sectionsToReturn = getVerifierSections(payload).map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(task.name as AerVerifyCorsiaTaskKey, payload);
        return { ...task, status };
      }),
    };
  });

  const filteredAerSections = filterOutTaskSections(
    getAerSections(payload, false, true),
    ['Send report', 'Reporting obligation'],
    [],
  );

  const sendReportSection = getAerVerifySendReportSection(true).map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(task.name as AerVerifyCorsiaTaskKey, payload);
        const link = status === 'cannot start yet' ? null : task.link;
        return { ...task, status, link };
      }),
    };
  });

  return [...sectionsToReturn, ...filteredAerSections, ...sendReportSection];
}

export function getTaskStatusByTaskCompletionState(
  taskName: AerVerifyCorsiaTaskKey,
  payload: AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
): TaskItemStatus {
  const completionState = payload.verificationSectionsCompleted[taskName];

  switch (taskName) {
    case 'sendReport': {
      const sections = getVerifierSections(payload);
      const availableSubTasks = getAvailableSubTasks(sections);
      return resolveSubmissionTaskStatus(payload.verificationSectionsCompleted, availableSubTasks);
    }
    default:
      return completionState != null ? (completionState[0] ? 'complete' : 'in progress') : 'not started';
  }
}

function getVerifierSections(
  payload: AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
): TaskSection<any>[] {
  return [
    ...getVerifierAssessmentTasks(true),
    ...getAerVerifyCorsiaVerifiedEmissions(payload.aer),
    ...getAerVerifyVerifierFindings(true),
    ...getAerVerifyCorsiaVerifierSummary(),
  ]
    .filter((section) => !!section)
    .map((section) => ({
      ...section,
      tasks: section.tasks.filter((task) => !!task),
    }));
}

function getAvailableSubTasks(taskSections: TaskSection<any>[]): AerVerifyCorsiaTaskKey[] {
  return taskSections
    .map((t) => t.tasks)
    .reduce((acc, tasks) => acc.concat(tasks), [])
    .map((task) => task.name as AerVerifyCorsiaTaskKey);
}

function resolveSubmissionTaskStatus(
  tasksCompleted: { [key: string]: boolean[] },
  availableTasks: string[],
): TaskItemStatus {
  return Object.keys(tasksCompleted).length >= availableTasks.length &&
    Object.values(tasksCompleted).every((tc) => tc[0] === true)
    ? 'not started'
    : 'cannot start yet';
}
