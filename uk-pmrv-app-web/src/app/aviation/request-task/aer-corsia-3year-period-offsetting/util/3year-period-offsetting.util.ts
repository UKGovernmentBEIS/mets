import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TaskItemStatus, TaskSection } from '@shared/task-list/task-list.interface';

import {
  AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

import { ThreeYearOffsettingRequirementsFormProvider } from '../aer-corsia-3year-period-offsetting-form.provider';

export const sectionName = 'offsettingRequirements';

export const getCorsia3YearOffsettingSections = (
  payload: AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload,
): TaskSection<any>[] => {
  const years = payload.aviationAerCorsia3YearPeriodOffsetting.schemeYears;

  return [
    {
      title: 'Details',
      tasks: [
        {
          link: '3year-offsetting-requirements',
          linkText: `${years[0]} - ${years[years.length - 1]} offsetting requirements`,
          status: getTaskStatusByTaskCompletionState(sectionName, payload),
        },
      ],
    },
  ];
};

export const getTaskStatusByTaskCompletionState = (
  sectionName: string,
  payload: AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload,
): TaskItemStatus => {
  const sectionsCompleted = payload.aviationAerCorsia3YearPeriodOffsettingSectionsCompleted || {};
  const completionState = sectionsCompleted[sectionName];

  return completionState != null ? (completionState ? 'complete' : 'in progress') : 'not started';
};

export const areTasksCompletedForNotify3YearPeriodOffsetting = (
  payload: AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload,
) => {
  return getTaskStatusByTaskCompletionState(sectionName, payload) === 'complete';
};

export const backlinkResolver: ResolveFn<string> = () => {
  const formProvider = inject<ThreeYearOffsettingRequirementsFormProvider>(TASK_FORM_PROVIDER);
  const isFormValid = formProvider.form.get('offsettingRequirements').valid;

  return isFormValid ? 'summary' : '../';
};

export function threeYearPeriodOffsettingDocumentPreviewRequestTaskActionTypesMap(
  requestTaskType: RequestTaskDTO['type'],
): RequestTaskActionProcessDTO['requestTaskActionType'] {
  switch (requestTaskType) {
    case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW':
      return 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_PEER_REVIEW_DECISION';
  }
}
