import { inject } from '@angular/core';
import { CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { combineLatest, map, take } from 'rxjs';

import { requestTaskQuery, RequestTaskStore, SectionsCompletedMap } from '../../../store';
import { empQuery } from '../../shared/emp.selectors';
import { resolveSubmissionTaskStatus } from '../../shared/util/emp.util';

export const canActivateSubmission: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  return combineLatest([
    store.pipe(empQuery.selectEmissionsMonitoringPlan),
    store.pipe(requestTaskQuery.selectTasksCompleted),
    store.pipe(requestTaskQuery.selectRequestTaskType),
    store.pipe(requestTaskQuery.selectRequestTaskPayload),
  ]).pipe(
    take(1),
    map(([emp, tasksCompleted, taskType, payload]) => {
      const status = resolveSubmissionTaskStatus(taskType, payload, Object.keys(emp));
      return status !== 'cannot start yet' && (tasksCompleted as SectionsCompletedMap).submission?.[0] !== true;
    }),
  );
};

export const canActivateSubmissionSuccess: CanActivateFn = (route) => {
  return inject(RequestTaskStore).pipe(
    requestTaskQuery.selectTasksCompleted,
    take(1),
    map((tasksCompleted) => {
      return (
        (tasksCompleted as SectionsCompletedMap).submission?.[0] === true ||
        createUrlTreeFromSnapshot(route, ['../../..'])
      );
    }),
  );
};
