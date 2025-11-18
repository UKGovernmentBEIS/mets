import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const regulatedActivityBacklinkResolver: ResolveFn<string> = (route: ActivatedRouteSnapshot) => {
  const payload = inject(CommonTasksStore).getValue().requestTaskItem.requestTask
    .payload as AerApplicationSubmitRequestTaskPayload;
  const id = route.paramMap?.get('activityId');

  const isUpstream =
    payload?.aer?.regulatedActivities?.find((activity) => activity.id === id)?.type === 'UPSTREAM_GHG_REMOVAL';
  return isUpstream ? '../' : '../capacity';
};
