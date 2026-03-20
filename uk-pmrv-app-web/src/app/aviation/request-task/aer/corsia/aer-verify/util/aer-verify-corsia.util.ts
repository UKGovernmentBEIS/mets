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
  const sectionsToReturn = getSections(payload).map((section) => {
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
      const availableSubTasks = getAvailableSubTasks(payload);
      return resolveSubmissionTaskStatus(payload.verificationSectionsCompleted, availableSubTasks);
    }
    default:
      return completionState != null ? (completionState[0] ? 'complete' : 'in progress') : 'not started';
  }
}

function getSections(payload: AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload): TaskSection<any>[] {
  return [
    ...getVerifierAssessmentTasks(true),
    ...getAerVerifyCorsiaVerifiedEmissions(payload.aer),
    ...getAerVerifyVerifierFindings(true),
    ...getAerVerifyCorsiaVerifierSummary(),
  ];
}

function getAvailableSubTasks(
  payload: AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
): AerVerifyCorsiaTaskKey[] {
  const sections = getSections(payload);
  return sections
    .map((t) => t.tasks)
    .reduce((acc, tasks) => acc.concat(tasks), [])
    .map((task) => task.name as AerVerifyCorsiaTaskKey);
}

function resolveSubmissionTaskStatus(
  verificationSectionsCompleted: { [key: string]: boolean[] },
  availableSubTasks: AerVerifyCorsiaTaskKey[],
): TaskItemStatus {
  return availableSubTasks.every((subTask) => verificationSectionsCompleted[subTask]?.[0] === true)
    ? 'not started'
    : 'cannot start yet';
}
