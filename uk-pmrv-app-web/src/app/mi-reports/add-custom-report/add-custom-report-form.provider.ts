import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

export const ADD_CUSTOM_REPORT_FORM = new InjectionToken<UntypedFormGroup>('Add custom report form');

export const addCustomReportFormProvider = {
  provide: ADD_CUSTOM_REPORT_FORM,
  deps: [UntypedFormBuilder],
  useFactory: (fb: UntypedFormBuilder) =>
    fb.group(
      {
        reportName: [
          null,
          [
            GovukValidators.required('Enter report name'),
            GovukValidators.maxLength(255, 'The report name should not be more than 255 characters'),
          ],
        ],
        categories: [[], GovukValidators.required('Select at least one category')],
        description: [
          null,
          [
            GovukValidators.required('Enter description'),
            GovukValidators.maxLength(2000, 'The description should not be more than 2000 characters'),
          ],
        ],
        queryDefinition: [null, GovukValidators.required('Query must not be empty')],
      },
      { updateOn: 'change' },
    ),
};
