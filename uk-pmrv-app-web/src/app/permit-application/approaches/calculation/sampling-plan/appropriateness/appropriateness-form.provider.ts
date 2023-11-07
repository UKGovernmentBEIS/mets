import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { CalculationOfCO2MonitoringApproach } from 'pmrv-api';

import { ProcedureFormComponent } from '../../../../shared/procedure-form/procedure-form.component';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

export const APPROPRIATENESS_FORM = new InjectionToken<UntypedFormGroup>('Appropriateness task form');

export const appropriatenessFormProvider = {
  provide: APPROPRIATENESS_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const value = (store.permit.monitoringApproaches?.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
      .samplingPlan?.details?.appropriateness;

    return fb.group(ProcedureFormComponent.controlsFactory(value));
  },
};
