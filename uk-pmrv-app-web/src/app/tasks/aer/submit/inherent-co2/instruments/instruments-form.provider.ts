import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { noOption } from '@tasks/aer/submit/inherent-co2/errors/inherent-co2-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload, InherentCO2Emissions } from 'pmrv-api';

export const instrumentsFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const aer = (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer;
    const installations = (
      aer.monitoringApproachEmissions?.INHERENT_CO2 as InherentCO2Emissions
    )?.inherentReceivingTransferringInstallations?.map(
      (aerInherent) => aerInherent?.inherentReceivingTransferringInstallation,
    );

    const measurementInstrumentOwnerTypes = installations
      ? installations[Number(route.snapshot.paramMap.get('index'))]?.measurementInstrumentOwnerTypes ?? null
      : null;

    return fb.group({
      measurementInstrumentOwnerTypes: [
        {
          value: measurementInstrumentOwnerTypes ?? null,
          disabled,
        },
        { validators: GovukValidators.required(noOption) },
      ],
    });
  },
};
