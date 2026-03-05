import { Component } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { AppliedStandard } from 'pmrv-api';

import { existingControlContainer } from '../../../shared/providers/control-container.factory';
import { GroupBuilderConfig } from '../../../shared/types';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-applied-standard-form',
  templateUrl: './applied-standard-form.component.html',
  providers: [existingControlContainer],
  viewProviders: [existingControlContainer],
})
export class AppliedStandardFormComponent {
  static controlsFactory(appliedStandard: AppliedStandard, disabled = false): GroupBuilderConfig<AppliedStandard> {
    return {
      parameter: new UntypedFormControl({ value: appliedStandard?.parameter ?? null, disabled }, [
        GovukValidators.required('Enter a parameter'),
        GovukValidators.maxLength(250, 'Enter up to 250 characters'),
      ]),
      appliedStandard: new UntypedFormControl({ value: appliedStandard?.appliedStandard ?? null, disabled }, [
        GovukValidators.required('Enter an applied standard'),
        GovukValidators.maxLength(50, 'Enter up to 50 characters'),
      ]),
      deviationFromAppliedStandardExist: new UntypedFormControl(
        { value: appliedStandard?.deviationFromAppliedStandardExist ?? null, disabled },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ),
      deviationFromAppliedStandardDetails: new UntypedFormControl(
        {
          value: appliedStandard?.deviationFromAppliedStandardDetails ?? null,
          disabled: !appliedStandard?.deviationFromAppliedStandardExist,
        },
        [GovukValidators.required('Enter details'), GovukValidators.maxLength(50, 'Enter up to 50 characters')],
      ),
      laboratoryName: new UntypedFormControl({ value: appliedStandard?.laboratoryName ?? null, disabled }, [
        GovukValidators.required('Enter a laboratory name'),
        GovukValidators.maxLength(250, 'Enter up to 250 characters'),
      ]),
      laboratoryAccredited: new UntypedFormControl(
        { value: appliedStandard?.laboratoryAccredited ?? null, disabled },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ),
      laboratoryAccreditationEvidence: new UntypedFormControl(
        {
          value: appliedStandard?.laboratoryAccreditationEvidence ?? null,
          disabled: !!appliedStandard?.laboratoryAccredited,
        },
        [GovukValidators.required('Enter evidence'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ),
    };
  }
}
