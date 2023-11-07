import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfPfcEmissions } from 'pmrv-api';

import { AER_PFC_FORM } from '../pfc';

export const calculationReviewFormProvider = {
  provide: AER_PFC_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();

    const disabled = !state.isEditable;

    const payload: AerApplicationSubmitRequestTaskPayload = state.requestTaskItem.requestTask.payload;

    const sourceStreamEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer.monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions)?.sourceStreamEmissions[
          Number(route.snapshot.paramMap.get('index'))
        ]
      : null;

    return fb.group({
      calculationCorrect: [
        { value: sourceStreamEmission?.calculationCorrect ?? null, disabled },
        {
          validators: GovukValidators.required('Select yes if the calculated emissions are correct'),
        },
      ],
      reasonForProvidingManualEmissions: [
        { value: sourceStreamEmission?.providedEmissions?.reasonForProvidingManualEmissions ?? null, disabled },
        {
          validators: GovukValidators.required('Explain why you are providing your own emission figures'),
        },
      ],
      totalProvidedReportableEmissions: [
        { value: sourceStreamEmission?.providedEmissions?.totalProvidedReportableEmissions ?? null, disabled },
        {
          validators: [
            GovukValidators.required(`Enter the total reportable emissions`),
            GovukValidators.maxDecimalsValidator(5),
          ],
        },
      ],
    });
  },
};
