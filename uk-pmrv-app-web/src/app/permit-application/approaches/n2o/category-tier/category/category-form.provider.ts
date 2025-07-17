import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { MeasurementOfN2OMonitoringApproach } from 'pmrv-api';

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
    const tiers = (state.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach)
      .emissionPointCategoryAppliedTiers;
    const emissionPointCategory = tiers
      ? (tiers[Number(route.snapshot.paramMap.get('index'))]?.emissionPointCategory ?? null)
      : null;
    const hasTransfer = (state.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach)
      ?.hasTransfer;

    return fb.group({
      sourceStreams: [
        {
          value: emissionPointCategory?.sourceStreams
            ? emissionPointCategory.sourceStreams.filter((stream) =>
                store.permit.sourceStreams.some((stateStream) => stateStream.id === stream),
              )
            : null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select a source stream'),
      ],
      emissionSources: [
        {
          value: emissionPointCategory?.emissionSources
            ? emissionPointCategory.emissionSources.filter((source) =>
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
            emissionPointCategory?.emissionPoint &&
            store.permit.emissionPoints.some((point) => point.id === emissionPointCategory.emissionPoint)
              ? emissionPointCategory.emissionPoint
              : null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select at least one emission point'),
      ],
      emissionType: [
        {
          value: emissionPointCategory?.emissionType,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select a type'),
      ],
      monitoringApproachType: [
        {
          value: emissionPointCategory?.monitoringApproachType,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select an approach'),
      ],
      annualEmittedCO2Tonnes: [
        {
          value: emissionPointCategory?.annualEmittedCO2Tonnes ?? null,
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
          value: emissionPointCategory?.categoryType,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select a category'),
      ],
      entryAccountingForTransfer: [
        {
          value: emissionPointCategory?.transfer?.entryAccountingForTransfer,
          disabled: !state.isEditable,
        },
        { validators: hasTransfer ? GovukValidators.required('Select a category') : null },
      ],
      transferDirection: [
        {
          value: emissionPointCategory?.transfer?.transferDirection,
          disabled: !state.isEditable,
        },
        { validators: hasTransfer ? GovukValidators.required('Select a category') : null },
      ],
    });
  },
};
