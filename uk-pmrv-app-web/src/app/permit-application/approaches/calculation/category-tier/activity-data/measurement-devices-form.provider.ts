import { AsyncValidatorFn, UntypedFormBuilder, ValidationErrors } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { areActivityDataValid } from '../../calculation-status';

export const measurementDevicesFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    route: ActivatedRoute,
  ) => {
    const index = Number(route.snapshot.paramMap.get('index'));
    return fb.group(
      {},
      {
        asyncValidators: [validateMeasurementDevices(store, index)],
      },
    );
  },
};

function validateMeasurementDevices(
  store: PermitApplicationStore<PermitApplicationState>,
  index: number,
): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    store.pipe(
      first(),
      map((state) => {
        return areActivityDataValid(state, index)
          ? null
          : { validMeasurementDevicesOrMethods: 'Select at least one measurement device' };
      }),
    );
}
