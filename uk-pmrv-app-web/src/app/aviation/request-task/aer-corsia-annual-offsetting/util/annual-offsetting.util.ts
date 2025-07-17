import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { AerCorsiaAnnualOffsettingPayload } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TaskItemStatus, TaskSection } from '@shared/task-list/task-list.interface';

import { RequestTaskActionProcessDTO, RequestTaskDTO } from 'pmrv-api';

import { AnnualOffsettingRequirementsFormProvider } from '../aer-corsia-annual-offsetting-form.provider';

export const sectionName = 'offsettingRequirements';

export const getCorsiaAnnualOffsettingSections = (payload: AerCorsiaAnnualOffsettingPayload): TaskSection<any>[] => {
  const year = payload.aviationAerCorsiaAnnualOffsetting.schemeYear;

  return [
    {
      title: 'Details',
      tasks: [
        {
          link: 'annual-offsetting-requirements',
          linkText: `${year} offsetting requirements`,
          status: getTaskStatusByTaskCompletionState(sectionName, payload),
        },
      ],
    },
  ];
};

export const getTaskStatusByTaskCompletionState = (
  sectionName: string,
  payload: AerCorsiaAnnualOffsettingPayload,
): TaskItemStatus => {
  const sectionsCompleted = payload.aviationAerCorsiaAnnualOffsettingSectionsCompleted || {};
  const completionState = sectionsCompleted[sectionName];

  return completionState != null ? (completionState ? 'complete' : 'in progress') : 'not started';
};

export const areTasksCompletedForNotifyAnnualOffsetting = (payload: AerCorsiaAnnualOffsettingPayload): boolean => {
  return getTaskStatusByTaskCompletionState(sectionName, payload) === 'complete';
};

export const backlinkResolver: ResolveFn<string> = () => {
  const formProvider = inject<AnnualOffsettingRequirementsFormProvider>(TASK_FORM_PROVIDER);
  const isFormValid = formProvider.form.get('offsettingRequirements').valid;

  return isFormValid ? 'summary' : '../';
};

export function annualOffsettingDocumentPreviewRequestTaskActionTypesMap(
  requestTaskType: RequestTaskDTO['type'],
): RequestTaskActionProcessDTO['requestTaskActionType'] {
  switch (requestTaskType) {
    case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW':
      return 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_PEER_REVIEW_DECISION';
  }
}
