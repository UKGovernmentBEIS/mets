import { Component } from '@angular/core';
import { AbstractControl, UntypedFormControl, UntypedFormGroup } from '@angular/forms';

import { existingControlContainer } from '@shared/providers/control-container.factory';

import { GovukValidators } from 'govuk-components';

import { HoldingCompanyDTO } from 'pmrv-api';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-holding-company-form',
  templateUrl: './holding-company-form.component.html',
  styles: [],
  viewProviders: [existingControlContainer],
})
export class HoldingCompanyFormComponent {
  static controlsFactory(holdingCompany?: HoldingCompanyDTO): Record<keyof HoldingCompanyDTO, AbstractControl> {
    return {
      name: new UntypedFormControl(holdingCompany?.name ?? null, [
        GovukValidators.required('Enter the holding company name'),
        GovukValidators.maxLength(256, 'The holding company name should not be more than 256 characters'),
      ]),
      registrationNumber: new UntypedFormControl(
        holdingCompany?.registrationNumber ?? null,
        GovukValidators.maxLength(50, 'Holding company registration number must be less than 50 characters'),
      ),
      address: new UntypedFormGroup({
        city: new UntypedFormControl(holdingCompany?.address?.city ?? null, [
          GovukValidators.required('Enter a town or city'),
          GovukValidators.maxLength(256, 'The city should not be more than 256 characters'),
        ]),
        line1: new UntypedFormControl(holdingCompany?.address?.line1 ?? null, [
          GovukValidators.required('Enter an address'),
          GovukValidators.maxLength(256, 'The address should not be more than 256 characters'),
        ]),
        line2: new UntypedFormControl(
          holdingCompany?.address?.line2 ?? null,
          GovukValidators.maxLength(256, 'The address should not be more than 256 characters'),
        ),
        postcode: new UntypedFormControl(holdingCompany?.address?.postcode ?? null, [
          GovukValidators.required('Enter a postcode'),
          GovukValidators.maxLength(64, 'The postcode should not be more than 64 characters'),
        ]),
      }),
    };
  }
}
