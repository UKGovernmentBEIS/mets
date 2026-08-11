import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { MiReportUserDefinedDTO } from 'pmrv-api';

import { customReportFormControls } from '../core/custom-report';

export const EDIT_CUSTOM_REPORT_FORM = new InjectionToken<UntypedFormGroup>('Edit custom report form');

export const editCustomReportFormProvider = {
  provide: EDIT_CUSTOM_REPORT_FORM,
  deps: [UntypedFormBuilder, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, route: ActivatedRoute) => {
    const report: MiReportUserDefinedDTO = route.snapshot.data.report;

    return fb.group(
      {
        ...customReportFormControls(report),
        reasonForChange: [
          null,
          [
            GovukValidators.required('Enter a reason for change'),
            GovukValidators.maxLength(2000, 'The reason for change should not be more than 2000 characters'),
          ],
        ],
      },
      { updateOn: 'change' },
    );
  },
};
