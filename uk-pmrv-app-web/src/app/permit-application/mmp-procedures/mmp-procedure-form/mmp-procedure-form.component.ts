import { Component } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { Procedure } from 'pmrv-api';

import { existingControlContainer } from '../../../shared/providers/control-container.factory';
import { GroupBuilderConfig } from '../../../shared/types';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-mmp-procedure-form',
  templateUrl: './mmp-procedure-form.component.html',
  providers: [existingControlContainer],
  viewProviders: [existingControlContainer],
})
export class MmpProcedureFormComponent {
  static controlsFactory(procedure: Procedure, disabled = false): GroupBuilderConfig<Procedure> {
    return {
      procedureName: new UntypedFormControl({ value: procedure?.procedureName ?? null, disabled }, [
        GovukValidators.required('Enter the name of the procedure'),
        GovukValidators.maxLength(1000, 'The procedure name should not be more than 1000 characters'),
      ]),
      procedureReference: new UntypedFormControl({ value: procedure?.procedureReference ?? null, disabled }, [
        GovukValidators.required('Enter the procedure reference'),
        GovukValidators.maxLength(500, 'The procedure reference should not be more than 500 characters'),
      ]),
      diagramReference: new UntypedFormControl({ value: procedure?.diagramReference ?? null, disabled }, [
        GovukValidators.maxLength(500, 'The diagram reference should not be more than 500 characters'),
      ]),
      procedureDescription: new UntypedFormControl({ value: procedure?.procedureDescription ?? null, disabled }, [
        GovukValidators.maxLength(10000, 'The procedure description should not be more than 10000 characters'),
      ]),
      dataMaintenanceResponsibleEntity: new UntypedFormControl(
        { value: procedure?.dataMaintenanceResponsibleEntity ?? null, disabled },
        [
          GovukValidators.maxLength(
            1000,
            'The name of the department or role responsible for data maintenance should not be more than 1000 characters',
          ),
        ],
      ),
      locationOfRecords: new UntypedFormControl({ value: procedure?.locationOfRecords ?? null, disabled }, [
        GovukValidators.maxLength(2000, 'The location of the records should not be more than 2000 characters'),
      ]),
      itSystemUsed: new UntypedFormControl({ value: procedure?.itSystemUsed ?? null, disabled }, [
        GovukValidators.maxLength(500, 'The IT system used should not be more than 500 characters'),
      ]),
      standardsAppliedList: new UntypedFormControl({ value: procedure?.standardsAppliedList ?? null, disabled }, [
        GovukValidators.maxLength(
          2000,
          'The list of EN or other standards applied should not be more than 2000 characters',
        ),
      ]),
    };
  }
}
