import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { MeasurementOfCO2MonitoringApproach, MeasurementOfN2OMonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

export const EMISSIONS_FORM = new InjectionToken<UntypedFormGroup>('Measured emisisons form');

export const emissionsFormProvider = {
  provide: EMISSIONS_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    route: ActivatedRoute,
  ) => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const { taskKey } = route.snapshot.data;

    const state = store.getValue();
    const measuredEmissions =
      (
        state.permit.monitoringApproaches[taskKey] as
          | MeasurementOfN2OMonitoringApproach
          | MeasurementOfCO2MonitoringApproach
      ).emissionPointCategoryAppliedTiers?.[index]?.measuredEmissions ?? null;

    return fb.group({
      measurementDevicesOrMethods: [
        {
          value: measuredEmissions?.measurementDevicesOrMethods
            ? measuredEmissions?.measurementDevicesOrMethods.filter((item) =>
                state.permit.measurementDevicesOrMethods.some((stateItem) => stateItem.id === item),
              )
            : null,
          disabled: !state.isEditable,
        },
        [GovukValidators.required('Select at least one measurement device')],
      ],
      samplingFrequency: [
        { value: measuredEmissions?.samplingFrequency ?? null, disabled: !state.isEditable },
        GovukValidators.required('Select a frequency'),
      ],
      otherSamplingFrequency: [
        {
          value: measuredEmissions?.samplingFrequency === 'OTHER' ? measuredEmissions?.otherSamplingFrequency : null,
          disabled: !state.isEditable,
        },
        [GovukValidators.required('Enter details'), GovukValidators.maxLength(50, 'Enter up to 50 characters')],
      ],
      tier: [
        { value: measuredEmissions?.tier, disabled: !state.isEditable } ?? null,
        GovukValidators.required('Select a tier'),
      ],
      isHighestRequiredTierT2: [
        {
          value: measuredEmissions?.tier === 'TIER_2' ? measuredEmissions.isHighestRequiredTier : null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select yes or no'),
      ],
      isHighestRequiredTierT1: [
        {
          value: measuredEmissions?.tier === 'TIER_1' ? measuredEmissions.isHighestRequiredTier : null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select yes or no'),
      ],
      ...(taskKey === 'MEASUREMENT_CO2'
        ? {
            isHighestRequiredTierT3: [
              {
                value: measuredEmissions?.tier === 'TIER_3' ? measuredEmissions.isHighestRequiredTier : null,
                disabled: !state.isEditable,
              },
              GovukValidators.required('Select yes or no'),
            ],
          }
        : {}),
    });
  },
};
