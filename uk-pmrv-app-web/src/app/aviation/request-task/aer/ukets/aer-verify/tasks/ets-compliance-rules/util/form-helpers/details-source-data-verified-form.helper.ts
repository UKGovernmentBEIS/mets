import { FormControl } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

const detailSourceDataNotVerifiedReasonValidator = [
  GovukValidators.required(
    'Please provide information to support your reason you cannot verify the detail and source of data',
  ),
];

const partOfSiteVerificationValidator = [GovukValidators.required('State if this was part of the site verification')];

export function createDetailsSourceDataVerifiedForm(destroy$: Subject<void>) {
  const field = {
    detailSourceDataVerified: new FormControl<AviationAerEtsComplianceRules['detailSourceDataVerified'] | null>(null, {
      updateOn: 'change',
      validators: [GovukValidators.required('Select if you can verify the detail and source of data')],
    }),

    detailSourceDataNotVerifiedReason: new FormControl<
      AviationAerEtsComplianceRules['detailSourceDataNotVerifiedReason'] | null
    >(null, detailSourceDataNotVerifiedReasonValidator),

    partOfSiteVerification: new FormControl<AviationAerEtsComplianceRules['partOfSiteVerification'] | null>(
      null,
      partOfSiteVerificationValidator,
    ),
  };

  field.detailSourceDataVerified.valueChanges.pipe(takeUntil(destroy$)).subscribe((value) => {
    if (value) {
      field.detailSourceDataNotVerifiedReason.reset();
      field.detailSourceDataNotVerifiedReason.clearValidators();

      field.partOfSiteVerification.setValidators(partOfSiteVerificationValidator);
      field.partOfSiteVerification.updateValueAndValidity();
    } else {
      field.partOfSiteVerification.reset();
      field.partOfSiteVerification.clearValidators();

      field.detailSourceDataNotVerifiedReason.setValidators(detailSourceDataNotVerifiedReasonValidator);
      field.detailSourceDataNotVerifiedReason.updateValueAndValidity();
    }
  });

  return field;
}
