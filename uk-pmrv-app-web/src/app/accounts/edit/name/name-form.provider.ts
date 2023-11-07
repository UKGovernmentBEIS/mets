import { InjectionToken } from '@angular/core';
import {
  AbstractControl,
  AsyncValidatorFn,
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidationErrors,
} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { first, map, Observable, of } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { InstallationAccountsService } from 'pmrv-api';

export const EDIT_ACCOUNT_FORM = new InjectionToken<UntypedFormGroup>('Edit account form');

export const nameFormProvider = {
  provide: EDIT_ACCOUNT_FORM,
  deps: [UntypedFormBuilder, ActivatedRoute, InstallationAccountsService],
  useFactory: (fb: UntypedFormBuilder, route: ActivatedRoute, accountsService: InstallationAccountsService) => {
    const installationName = route.snapshot.data.accountPermit.account.name;

    return fb.group(
      {
        installationName: [
          installationName,
          [
            GovukValidators.required('Enter the installation name'),
            GovukValidators.maxLength(255, 'The installation name should not be more than 255 characters'),
          ],
        ],
      },
      {
        updateOn: 'change',
        asyncValidators: [installationNameNotExists(accountsService, installationName)],
      },
    );
  },
};

function installationNameNotExists(accountsService: InstallationAccountsService, name: string): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> =>
    control.get('installationName').value === name
      ? of(null)
      : accountsService.isExistingAccountName(control.get('installationName').value).pipe(
          first(),
          map((res) =>
            res ? { installationNameExists: 'The installation name already exists. Enter a new name.' } : null,
          ),
        );
}
