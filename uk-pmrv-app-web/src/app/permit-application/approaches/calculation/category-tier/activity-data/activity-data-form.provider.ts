import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { CalculationOfCO2MonitoringApproach } from 'pmrv-api';

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

    const tiers = (state.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
      .sourceStreamCategoryAppliedTiers;
    const activityData = tiers?.[index]?.activityData ?? null;
    const disabled = !state.isEditable;

    return fb.group({
      measurementDevicesOrMethods: [
        {
          value: activityData?.measurementDevicesOrMethods
            ? activityData.measurementDevicesOrMethods.filter((device) =>
                store.permit.measurementDevicesOrMethods.some((stateDevice) => stateDevice.id === device),
              )
            : null,
          disabled,
        },
        GovukValidators.required('Select at least one measurement device'),
      ],
      uncertainty: [
        {
          value: activityData?.uncertainty,
          disabled,
        },
        GovukValidators.required('Select an overall metering uncertainty'),
      ],
      tier: [
        { value: activityData?.tier ?? null, disabled: !state.isEditable },
        GovukValidators.required('Select a tier'),
      ],
      isHighestRequiredTierT3: [
        {
          value: activityData?.tier === 'TIER_3' ? activityData?.isHighestRequiredTier : null,
          disabled: disabled || activityData?.tier !== 'TIER_3',
        },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      isHighestRequiredTierT2: [
        {
          value: activityData?.tier === 'TIER_2' ? activityData?.isHighestRequiredTier : null,
          disabled: disabled || activityData?.tier !== 'TIER_2',
        },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      isHighestRequiredTierT1: [
        {
          value: activityData?.tier === 'TIER_1' ? activityData?.isHighestRequiredTier : null,
          disabled: disabled || activityData?.tier !== 'TIER_1',
        },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      isHighestRequiredTierT0: [
        {
          value: activityData?.tier === 'NO_TIER' ? activityData?.isHighestRequiredTier : null,
          disabled: disabled || activityData?.tier !== 'NO_TIER',
        },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
    });
  },
};
