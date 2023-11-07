import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { MeasurementOfN2OMonitoringApproach } from 'pmrv-api';

import { AppliedStandardFormComponent } from '../../../../shared/approaches/applied-standard-form.component';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

export const appliedStandardFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    route: ActivatedRoute,
  ) => {
    const state = store.getValue();
    const value =
      (state.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach)
        ?.emissionPointCategoryAppliedTiers?.[Number(route.snapshot.paramMap.get('index'))]?.appliedStandard ?? null;

    return fb.group(AppliedStandardFormComponent.controlsFactory(value, !state.isEditable));
  },
};
