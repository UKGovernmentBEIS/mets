import { AsyncValidatorFn, FormBuilder, ValidationErrors } from '@angular/forms';

import { first, map, Observable } from 'rxjs';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const regulatedActivitiesFormFactory = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore],
  useFactory: (fb: FormBuilder, store: CommonTasksStore) =>
    fb.group(
      {},
      {
        asyncValidators: [
          missingCapacity(store),
          missingCrfCodes(store),
          missingEnergyCrfCode(store),
          missingIndustrialCrfCode(store),
          missingWaste(store),
          missingWasteCrfCode(store),
        ],
      },
    ),
};

function missingCrfCodes(stateChanges: Observable<CommonTasksState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) =>
        (
          state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
        ).aer.regulatedActivities?.every(
          (activity) => !!activity.energyCrf || !!activity.industrialCrf || !!activity.wasteCrf,
        )
          ? null
          : { missingCrf: 'Select at least one CRF code' },
      ),
    );
}

function missingEnergyCrfCode(stateChanges: Observable<CommonTasksState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) =>
        (
          state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
        ).aer.regulatedActivities?.every(
          (activity) =>
            !activity.hasEnergyCrf ||
            (!activity.energyCrf && !activity.industrialCrf && !activity.wasteCrf) ||
            (activity.hasEnergyCrf && activity.energyCrf),
        )
          ? null
          : { missingEnergyCrf: 'Missing CRF code from energy processes sector' },
      ),
    );
}

function missingIndustrialCrfCode(stateChanges: Observable<CommonTasksState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) =>
        (
          state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
        ).aer.regulatedActivities?.every(
          (activity) =>
            !activity.hasIndustrialCrf ||
            (!activity.energyCrf && !activity.industrialCrf && !activity.wasteCrf) ||
            (activity.hasIndustrialCrf && activity.industrialCrf),
        )
          ? null
          : { missingIndustrialCrf: 'Missing CRF code from industrial processes sector' },
      ),
    );
}

function missingWasteCrfCode(stateChanges: Observable<CommonTasksState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) =>
        (
          state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
        ).aer.regulatedActivities?.every(
          (activity) =>
            !activity.hasWasteCrf ||
            (!activity.energyCrf && !activity.industrialCrf && !activity.wasteCrf) ||
            (activity.hasWasteCrf && activity.wasteCrf),
        )
          ? null
          : { missingWasteCrf: 'Missing CRF code from waste processes sector' },
      ),
    );
}

function missingCapacity(stateChanges: Observable<CommonTasksState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) =>
        (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer.regulatedActivities
          ?.filter((x) => x.type !== 'UPSTREAM_GHG_REMOVAL')
          ?.every((activity) => !!activity.capacity)
          ? null
          : { missingCapacity: 'Enter total capacity' },
      ),
    );
}

function missingWaste(stateChanges: Observable<CommonTasksState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) =>
        (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).permitType === 'WASTE' &&
        (
          state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
        ).aer.regulatedActivities?.some((activity) => activity.type === 'WASTE') === false
          ? { missingWaste: 'You must add waste as a regulated activity' }
          : null,
      ),
    );
}
