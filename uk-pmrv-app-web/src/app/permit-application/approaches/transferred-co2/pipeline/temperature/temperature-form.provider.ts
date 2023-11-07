import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { MeasurementDevice, TransferredCO2AndN2OMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

export const temperatureFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const state = store.getValue();
    const value = (state.permit.monitoringApproaches?.TRANSFERRED_CO2_N2O as TransferredCO2AndN2OMonitoringApproach)
      ?.transportCO2AndN2OPipelineSystems?.temperaturePressure;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      measurementDevices: fb.array(
        value?.measurementDevices?.map(createAnotherMeasurementDevice) ?? [createAnotherMeasurementDevice()],
      ),
    });
  },
};

export function createAnotherMeasurementDevice(value?: MeasurementDevice): UntypedFormGroup {
  return new UntypedFormGroup({
    reference: new UntypedFormControl(value?.reference ?? null, [
      GovukValidators.required('Provide a reference'),
      GovukValidators.maxLength(
        10000,
        'The reference of the measurement device should not be more than 10000 characters',
      ),
    ]),
    type: new UntypedFormControl(value?.type ?? null, [
      GovukValidators.required('Select a type of measurement device'),
    ]),
    otherTypeName: new UntypedFormControl({ value: value?.otherTypeName ?? null, disabled: value?.type !== 'OTHER' }, [
      GovukValidators.required('Enter a short name'),
      GovukValidators.maxLength(10000, 'The short name should not be more than 10000 characters'),
    ]),
    location: new UntypedFormControl(value?.location ?? null, [
      GovukValidators.required('Enter a location'),
      GovukValidators.maxLength(
        10000,
        'The location of the measurement device should not be more than 10000 characters',
      ),
    ]),
  });
}
