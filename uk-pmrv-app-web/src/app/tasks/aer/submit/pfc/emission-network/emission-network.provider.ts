import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfPfcEmissions } from 'pmrv-api';

import { AER_PFC_FORM } from '../pfc';

export const emissionNetworkFormProvider = {
  provide: AER_PFC_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();

    const disabled = !state.isEditable;

    const payload: AerApplicationSubmitRequestTaskPayload = state.requestTaskItem.requestTask.payload;

    const sourceStreamEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer?.monitoringApproachEmissions?.CALCULATION_PFC as CalculationOfPfcEmissions)
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
      calculationMethod: [
        {
          value: sourceStreamEmission?.pfcSourceStreamEmissionCalculationMethodData?.calculationMethod ?? null,
          disabled,
        },
        { validators: GovukValidators.required('Select a calculation method') },
      ],
      massBalanceApproachUsed: [
        {
          value: sourceStreamEmission?.massBalanceApproachUsed ?? null,
          disabled,
        },
        { validators: GovukValidators.required('Select Yes or No') },
      ],
    });
  },
};
