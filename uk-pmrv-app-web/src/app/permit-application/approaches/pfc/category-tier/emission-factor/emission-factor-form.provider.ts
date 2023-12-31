import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { CalculationOfPFCMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

export const emissionFactorFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    route: ActivatedRoute,
  ) => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const state = store.getValue();

    const emissionFactor =
      (state.permit.monitoringApproaches.CALCULATION_PFC as CalculationOfPFCMonitoringApproach)
        ?.sourceStreamCategoryAppliedTiers?.[index]?.emissionFactor ?? null;
    const disabled = !state.isEditable;

    return fb.group({
      tier: [{ value: emissionFactor?.tier ?? null, disabled }, GovukValidators.required('Select a tier')],
      isHighestRequiredTierT1: [
        {
          value: emissionFactor?.tier === 'TIER_1' ? emissionFactor.isHighestRequiredTier : null,
          disabled: disabled || emissionFactor?.tier !== 'TIER_2',
        },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
    });
  },
};
