import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { CalculationOfCO2MonitoringApproach } from 'pmrv-api';

import { ProcedureFormComponent } from '../../../shared/procedure-form/procedure-form.component';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const SAMPLING_PLAN_FORM = new InjectionToken<UntypedFormGroup>('Sampling plan task form');

export const samplingPlanFormProvider = {
  provide: SAMPLING_PLAN_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const state = store.getValue();
    const value = (state.permit.monitoringApproaches?.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
      .samplingPlan;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      analysis: fb.group(ProcedureFormComponent.controlsFactory(value?.details?.analysis)),
    });
  },
};
