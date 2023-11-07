import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { AER_MEASUREMENT_FORM } from '../measurement-status';

export const emissionNetworkFormProvider = {
  provide: AER_MEASUREMENT_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const taskKey = route.snapshot.data.taskKey;
    const state = store.getValue();

    const disabled = !state.isEditable;

    const payload: AerApplicationSubmitRequestTaskPayload = state.requestTaskItem.requestTask.payload;

    const emissionPointEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer?.monitoringApproachEmissions[taskKey] as any)?.emissionPointEmissions?.[
          Number(route.snapshot.paramMap.get('index'))
        ]
      : null;

    return fb.group({
      emissionPoint: [
        {
          value:
            emissionPointEmission?.emissionPoint &&
            payload.aer.emissionPoints.some((stream) => stream.id === emissionPointEmission.emissionPoint)
              ? emissionPointEmission.emissionPoint
              : null,
          disabled,
        },
        { validators: GovukValidators.required('Select an emission point') },
      ],
      sourceStreams: [
        {
          value: emissionPointEmission?.sourceStreams
            ? emissionPointEmission.sourceStreams.filter((source) =>
                payload.aer.sourceStreams.some((stateSource) => stateSource.id === source),
              )
            : null,
          disabled,
        },
        { validators: GovukValidators.required('Select at least one source stream') },
      ],
      emissionSources: [
        {
          value: emissionPointEmission?.emissionSources
            ? emissionPointEmission.emissionSources.filter((source) =>
                payload.aer.emissionSources.some((stateSource) => stateSource.id === source),
              )
            : null,
          disabled,
        },
        { validators: GovukValidators.required('Select at least one emission source') },
      ],
      containsBiomass: [
        { value: emissionPointEmission?.biomassPercentages?.contains ?? null, disabled },
        { validators: GovukValidators.required('Select Yes or No') },
      ],
    });
  },
};
