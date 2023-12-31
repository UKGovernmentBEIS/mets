import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { CalculationOfPFCMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

export const activityDataFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    route: ActivatedRoute,
  ) => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const state = store.getValue();

    const activityData =
      (state.permit.monitoringApproaches.CALCULATION_PFC as CalculationOfPFCMonitoringApproach)
        ?.sourceStreamCategoryAppliedTiers?.[index]?.activityData ?? null;

    return fb.group({
      massBalanceApproachUsed: [
        { value: activityData?.massBalanceApproachUsed ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      tier: [
        { value: activityData?.tier ?? null, disabled: !state.isEditable },
        GovukValidators.required('Select a tier'),
      ],
      isHighestRequiredTier_TIER_3: [
        {
          value: activityData?.tier === 'TIER_3' ? activityData.isHighestRequiredTier : null,
          disabled: !state.isEditable || activityData?.tier !== 'TIER_3',
        },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      isHighestRequiredTier_TIER_2: [
        {
          value: activityData?.tier === 'TIER_2' ? activityData.isHighestRequiredTier : null,
          disabled: !state.isEditable || !activityData?.massBalanceApproachUsed || activityData?.tier !== 'TIER_2',
        },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      isHighestRequiredTier_TIER_1: [
        {
          value: activityData?.tier === 'TIER_1' ? activityData.isHighestRequiredTier : null,
          disabled: !state.isEditable || activityData?.tier !== 'TIER_1',
        },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
    });
  },
};
