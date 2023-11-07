import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { CalculationOfCO2MonitoringApproach } from 'pmrv-api';

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
    const disabled = !state.isEditable;
    const tiers = (state.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
      .sourceStreamCategoryAppliedTiers;
    const sourceStreamCategoryTier = tiers
      ? tiers[Number(route.snapshot.paramMap.get('index'))]?.sourceStreamCategory ?? null
      : null;
    const hasTransfer = (state.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
      ?.hasTransfer;

    return fb.group({
      sourceStream: [
        {
          value:
            sourceStreamCategoryTier?.sourceStream &&
            store.permit.sourceStreams.some((stream) => stream.id === sourceStreamCategoryTier.sourceStream)
              ? sourceStreamCategoryTier.sourceStream
              : null,
          disabled,
        },
        GovukValidators.required('Select a source stream'),
      ],
      emissionSources: [
        {
          value: sourceStreamCategoryTier?.emissionSources
            ? sourceStreamCategoryTier.emissionSources.filter((source) =>
                store.permit.emissionSources.some((stateSource) => stateSource.id === source),
              )
            : null,
          disabled,
        },
        GovukValidators.required('Select at least one emission source'),
      ],
      annualEmittedCO2Tonnes: [
        {
          value: sourceStreamCategoryTier?.annualEmittedCO2Tonnes ?? null,
          disabled,
        },
        [
          GovukValidators.required('Select an estimated tonnes of CO2e'),
          GovukValidators.min(-99999999.999, 'Estimated tonnes of CO2e must be greater or equal than -99,999,999.999'),
          GovukValidators.max(99999999.999, 'Estimated tonnes of CO2e must be less or equal than 99,999,999.999'),
          getThreeDecimalPlacesValidator,
        ],
      ],
      calculationMethod: [
        {
          value: sourceStreamCategoryTier?.calculationMethod,
          disabled,
        },
        GovukValidators.required('Select a calculation method'),
      ],
      categoryType: [
        {
          value: sourceStreamCategoryTier?.categoryType,
          disabled,
        },
        GovukValidators.required('Select a category'),
      ],
      entryAccountingForTransfer: [
        {
          value: sourceStreamCategoryTier?.transfer?.entryAccountingForTransfer,
          disabled,
        },
        { validators: hasTransfer ? GovukValidators.required('Select a category') : null },
      ],
      transferDirection: [
        {
          value: sourceStreamCategoryTier?.transfer?.transferDirection,
          disabled,
        },
        { validators: hasTransfer ? GovukValidators.required('Select a category') : null },
      ],
    });
  },
};
