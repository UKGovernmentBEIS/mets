import { inject } from '@angular/core';
import { CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { combineLatest, map, take } from 'rxjs';

import { getClosestParentEmptyRoute } from '@aviation/request-task/guards';

import { requestTaskQuery, RequestTaskStore } from '../../../../store';
import { aerQuery } from '../../../shared/aer.selectors';

export const canActivateTotalEmissions: CanActivateFn = () => {
  const store = inject(RequestTaskStore);

  return store.pipe(
    aerQuery.selectStatusForTask('totalEmissionsCorsia'),
    take(1),
    map((status) => status !== 'cannot start yet'),
  );
};

/**
 * Substitutes canActivateTaskForm, since no form, we check aerSectionsCompleted
 */
export const canActivateTotalEmissionsPage: CanActivateFn = (route) => {
  const store = inject(RequestTaskStore);
  const isEditable$ = store.pipe(requestTaskQuery.selectIsEditable);
  const aerSectionsCompleted$ = store.pipe(aerQuery.selectAerSectionsCompleted);
  const change = route.queryParamMap.get('change') === 'true';

  return combineLatest([isEditable$, aerSectionsCompleted$]).pipe(
    take(1),
    map(([isEditable, sectionsCompleted]) => {
      const statusInvalid = sectionsCompleted['totalEmissionsCorsia']?.[0] !== true;
      return (
        (isEditable && (statusInvalid || change)) ||
        createUrlTreeFromSnapshot(getClosestParentEmptyRoute(route), ['summary'])
      );
    }),
  );
};

export const canActivateTotalEmissionsSummaryPage: CanActivateFn = (route) => {
  const store = inject(RequestTaskStore);
  const isEditable$ = store.pipe(requestTaskQuery.selectIsEditable);
  const aerSectionsCompleted$ = store.pipe(aerQuery.selectAerSectionsCompleted);

  return combineLatest([isEditable$, aerSectionsCompleted$]).pipe(
    take(1),
    map(([isEditable, sectionsCompleted]) => {
      return (
        (isEditable && sectionsCompleted['totalEmissionsCorsia'] !== undefined) ||
        !isEditable ||
        createUrlTreeFromSnapshot(route, ['../'])
      );
    }),
  );
};
