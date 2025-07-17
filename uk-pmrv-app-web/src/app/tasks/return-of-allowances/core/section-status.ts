import { format } from 'date-fns';

import {
  ReturnOfAllowancesApplicationSubmitRequestTaskPayload,
  ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload,
} from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';

export function resolveSectionStatus(payload: ReturnOfAllowancesApplicationSubmitRequestTaskPayload): TaskItemStatus {
  return !isDateToBeReturnedValid(payload)
    ? 'needs review'
    : payload?.sectionsCompleted['PROVIDE_DETAILS']
      ? 'complete'
      : !payload?.returnOfAllowances?.numberOfAllowancesToBeReturned
        ? 'not started'
        : 'in progress';
}

export type StatusKey = 'PROVIDE_DETAILS' | 'PROVIDE_RETURNED_DETAILS';

export function isDateToBeReturnedValid(payload: ReturnOfAllowancesApplicationSubmitRequestTaskPayload): boolean {
  const dateToBeReturned = format(
    payload?.returnOfAllowances?.dateToBeReturned
      ? new Date(payload?.returnOfAllowances?.dateToBeReturned)
      : new Date(),
    'yyyy-MM-dd',
  );

  const currentDate = format(new Date(), 'yyyy-MM-dd');

  return dateToBeReturned >= currentDate;
}

export function resolveReturnedSectionStatus(
  payload: ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload,
): TaskItemStatus {
  return payload?.sectionsCompleted['PROVIDE_RETURNED_DETAILS']
    ? 'complete'
    : payload?.returnOfAllowancesReturned?.isAllowancesReturned === undefined
      ? 'not started'
      : 'in progress';
}
