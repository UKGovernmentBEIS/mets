import { UntypedFormArray, UntypedFormControl } from '@angular/forms';

import { todayOrPastDateValidator } from '@tasks/aer/verification-submit/opinion-statement/validators/today-or-past-date.validator';

import { InPersonSiteVisit, VirtualSiteVisit } from 'pmrv-api';

export function createVisitDatesArray(
  isEditable: boolean,
  siteVisit?: InPersonSiteVisit | VirtualSiteVisit,
): UntypedFormArray {
  if (siteVisit?.visitDates) {
    return new UntypedFormArray(
      siteVisit?.visitDates.map((visitDate) => createVisitDateControl(isEditable, visitDate)),
    );
  }
  return new UntypedFormArray([createVisitDateControl(isEditable, null)]);
}

export function createVisitDateControl(isEditable: boolean, value?: string): UntypedFormControl {
  return new UntypedFormControl(
    {
      value: value ? new Date(value) : null,
      disabled: !isEditable,
    },
    { validators: [todayOrPastDateValidator()] },
  );
}
