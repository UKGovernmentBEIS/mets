import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { MeasurementOfCO2MonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

const getThreeDecimalPlacesValidator = GovukValidators.pattern(
  '-?[0-9]*\\.?[0-9]{0,3}',
  'Estimated tonnes of CO2e must be to 3 decimal places',
);

export const categoryFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    route: ActivatedRoute,
  ) => {
    const state = store.getValue();
    const tiers = (state.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)
      .emissionPointCategoryAppliedTiers;
    const emissionPointCategoryTier = tiers
      ? tiers[Number(route.snapshot.paramMap.get('index'))]?.emissionPointCategory ?? null
      : null;
    const hasTransfer = (state.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)
      ?.hasTransfer;

    return fb.group({
      sourceStreams: [
        {
          value: emissionPointCategoryTier?.sourceStreams
            ? emissionPointCategoryTier.sourceStreams.filter((stream) =>
                store.permit.sourceStreams.some((stateStream) => stateStream.id === stream),
              )
            : null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select a source stream'),
      ],
      emissionSources: [
        {
          value: emissionPointCategoryTier?.emissionSources
            ? emissionPointCategoryTier.emissionSources.filter((source) =>
                store.permit.emissionSources.some((stateSource) => stateSource.id === source),
              )
            : null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select at least one emission source'),
      ],
      emissionPoint: [
        {
          value:
            emissionPointCategoryTier?.emissionPoint &&
            store.permit.emissionPoints.some((point) => point.id === emissionPointCategoryTier.emissionPoint)
              ? emissionPointCategoryTier.emissionPoint
              : null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select at least one emission point'),
      ],
      annualEmittedCO2Tonnes: [
        {
          value: emissionPointCategoryTier?.annualEmittedCO2Tonnes ?? null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required('Select an estimated tonnes of CO2e'),
          GovukValidators.min(-99999999.999, 'Estimated tonnes of CO2e must be greater or equal than -99,999,999.999'),
          GovukValidators.max(99999999.999, 'Estimated tonnes of CO2e must be less or equal than 99,999,999.999'),
          getThreeDecimalPlacesValidator,
        ],
      ],
      categoryType: [
        {
          value: emissionPointCategoryTier?.categoryType,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select a category'),
      ],
      entryAccountingForTransfer: [
        {
          value: emissionPointCategoryTier?.transfer?.entryAccountingForTransfer,
          disabled: !state.isEditable,
        },
        { validators: hasTransfer ? GovukValidators.required('Select a category') : null },
      ],
      transferDirection: [
        {
          value: emissionPointCategoryTier?.transfer?.transferDirection,
          disabled: !state.isEditable,
        },
        { validators: hasTransfer ? GovukValidators.required('Select a category') : null },
      ],
    });
  },
};
