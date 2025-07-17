import {
  AER_CORSIA_REVIEW_APPLICATION_TASKS,
  aerCorsiaReviewGroupMap,
} from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import {
  filterOutTaskSections,
  getAerReviewTaskStatusByTaskCompletionState,
} from '@aviation/request-task/aer/shared/util/aer.util';
import {
  getAerVerifyCorsiaVerifiedEmissions,
  getAerVerifyCorsiaVerifierSummary,
  getAerVerifyVerifierFindings,
  getVerifierAssessmentTasks,
} from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { AerTaskKey } from '@aviation/request-task/store';
import { TaskSection } from '@shared/task-list/task-list.interface';

import { AviationAerCorsiaApplicationReviewRequestTaskPayload } from 'pmrv-api';

export function getAerCorsiaReviewSections(
  payload: AviationAerCorsiaApplicationReviewRequestTaskPayload,
): TaskSection<any>[] {
  let sections: TaskSection<any>[];
  if (payload?.reportingRequired) {
    sections = [...getVerifierSections(payload), ...getAvailableAerReviewSections(payload)];
  } else {
    sections = getAvailableAerReviewSections(payload);
  }

  return sections.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const reviewKey = aerCorsiaReviewGroupMap[task.name as AerTaskKey];
        const status = getAerReviewTaskStatusByTaskCompletionState(
          payload.reviewGroupDecisions[reviewKey],
          payload.reviewSectionsCompleted[reviewKey],
        );
        return { ...task, status };
      }),
    };
  });
}

function getAvailableAerReviewSections(
  payload: AviationAerCorsiaApplicationReviewRequestTaskPayload,
): TaskSection<any>[] {
  if (payload?.reportingRequired) {
    return filterOutTaskSections(AER_CORSIA_REVIEW_APPLICATION_TASKS, ['Reporting obligation']);
  } else {
    return filterOutTaskSections(AER_CORSIA_REVIEW_APPLICATION_TASKS, [
      'Identification',
      'Emissions overview',
      'Additional information',
      'Emissions for the scheme year',
    ]);
  }
}

function getVerifierSections(payload: AviationAerCorsiaApplicationReviewRequestTaskPayload): TaskSection<any>[] {
  return [
    ...getVerifierAssessmentTasks(true, false, true),
    ...getAerVerifyCorsiaVerifiedEmissions(payload.aer, true),
    ...getAerVerifyVerifierFindings(true, true),
    ...getAerVerifyCorsiaVerifierSummary(true),
  ];
}
