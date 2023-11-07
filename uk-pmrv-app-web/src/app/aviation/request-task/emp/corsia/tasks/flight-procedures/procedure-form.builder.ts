import { FormControl, FormGroup, ValidatorFn } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { EmpProcedureFormFormModel } from './flight-procedures.interface';

export class ProcedureFormBuilder {
  static createProcedureForm(validators: ValidatorFn[] = []): FormGroup<EmpProcedureFormFormModel> {
    return new FormGroup<EmpProcedureFormFormModel>(
      {
        procedureDescription: new FormControl(null as string, [
          GovukValidators.required('Enter a description for the procedure'),
          GovukValidators.maxLength(10000, 'The procedure description should not be more than 10000 characters'),
        ]),

        procedureDocumentName: new FormControl(null as string, [
          GovukValidators.required('Enter the name of the procedure document'),
          GovukValidators.maxLength(500, 'The name of the procedure document should not be more than 500 characters'),
        ]),

        procedureReference: new FormControl(null as string, [
          GovukValidators.required('Enter a procedure reference'),
          GovukValidators.maxLength(500, 'The procedure reference should not be more than 500 characters'),
        ]),

        responsibleDepartmentOrRole: new FormControl(null as string, [
          GovukValidators.required('Enter the name of the department or role responsible'),
          GovukValidators.maxLength(
            500,
            'The name of the department or role responsible should not be more than 500 characters',
          ),
        ]),

        locationOfRecords: new FormControl(null as string, [
          GovukValidators.required('Enter the physical location of the records'),
          GovukValidators.maxLength(500, 'The physical location of the records should not be more than 500 characters'),
        ]),

        itSystemUsed: new FormControl(null as string, [
          GovukValidators.maxLength(500, 'The IT system used should not be more than 500 characters'),
        ]),
      },
      { updateOn: 'change', validators },
    );
  }
}
