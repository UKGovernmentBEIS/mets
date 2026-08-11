import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { customReportFormControls } from '../core/custom-report';

export const ADD_CUSTOM_REPORT_FORM = new InjectionToken<UntypedFormGroup>('Add custom report form');

export const addCustomReportFormProvider = {
  provide: ADD_CUSTOM_REPORT_FORM,
  deps: [UntypedFormBuilder],
  useFactory: (fb: UntypedFormBuilder) => fb.group(customReportFormControls(), { updateOn: 'change' }),
};
