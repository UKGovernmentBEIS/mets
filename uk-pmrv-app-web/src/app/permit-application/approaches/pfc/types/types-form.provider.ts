import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { CalculationOfPFCMonitoringApproach, CellAndAnodeType } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const typesFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const value = store.permit.monitoringApproaches?.CALCULATION_PFC as CalculationOfPFCMonitoringApproach;

    return fb.group({
      cellAndAnodeTypes: fb.array(
        value?.cellAndAnodeTypes?.length > 0
          ? value?.cellAndAnodeTypes?.map((val) => createCellAndAnodeTypes(val, !store.getValue().isEditable))
          : [createCellAndAnodeTypes(null, !store.getValue().isEditable)],
      ),
    });
  },
};

export function createCellAndAnodeTypes(value?: CellAndAnodeType, disabled = false): UntypedFormGroup {
  return new UntypedFormGroup({
    cellType: new UntypedFormControl({ value: value?.cellType ?? null, disabled }, [
      GovukValidators.required('Enter a cell type'),
      GovukValidators.maxLength(100, 'Enter up to 100 characters'),
    ]),
    anodeType: new UntypedFormControl({ value: value?.anodeType ?? null, disabled }, [
      GovukValidators.required('Enter an anode type'),
      GovukValidators.maxLength(100, 'Enter up to 100 characters'),
    ]),
  });
}
