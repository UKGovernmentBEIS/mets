import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfCO2Emissions } from 'pmrv-api';

import { AER_CALCULATION_EMISSIONS_FORM } from '../calculation-emissions';

export const emissionNetworkFormProvider = {
  provide: AER_CALCULATION_EMISSIONS_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();

    const disabled = !state.isEditable;

    const payload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;

    const sourceStreamEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer?.monitoringApproachEmissions?.CALCULATION_CO2 as CalculationOfCO2Emissions)
          ?.sourceStreamEmissions?.[Number(route.snapshot.paramMap.get('index'))]
      : null;

    return fb.group({
      sourceStream: [
        {
          value:
            sourceStreamEmission?.sourceStream &&
            payload.aer.sourceStreams.some((stream) => stream.id === sourceStreamEmission.sourceStream)
              ? sourceStreamEmission.sourceStream
              : null,
          disabled,
        },
        { validators: GovukValidators.required('Select a source stream') },
      ],
      emissionSources: [
        {
          value: sourceStreamEmission?.emissionSources
            ? sourceStreamEmission.emissionSources.filter((source) =>
                payload.aer.emissionSources.some((stateSource) => stateSource.id === source),
              )
            : null,
          disabled,
        },
        { validators: GovukValidators.required('Select at least one emission source') },
      ],
      containsBiomass: [
        { value: sourceStreamEmission?.biomassPercentages?.contains ?? null, disabled },
        { validators: GovukValidators.required('Select Yes or No') },
      ],
    });
  },
};
