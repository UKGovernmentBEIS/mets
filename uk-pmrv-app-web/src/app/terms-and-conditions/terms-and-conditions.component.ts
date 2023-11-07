import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { distinctUntilKeyChanged, Observable, switchMap, takeUntil } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectTerms } from '@core/store/auth';

import { GovukValidators } from 'govuk-components';

import { TermsDTO, UsersService } from 'pmrv-api';

@Component({
  selector: 'app-terms-and-conditions',
  templateUrl: './terms-and-conditions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class TermsAndConditionsComponent {
  terms$: Observable<TermsDTO> = this.authStore.pipe(selectTerms, distinctUntilKeyChanged('version'));

  form: UntypedFormGroup = this.fb.group({
    terms: [null, GovukValidators.required('You should accept terms and conditions to proceed')],
  });

  constructor(
    private readonly router: Router,
    private readonly usersService: UsersService,
    private readonly authService: AuthService,
    private readonly authStore: AuthStore,
    private readonly fb: UntypedFormBuilder,
    private readonly destroy$: DestroySubject,
  ) {}

  submitTerms(): void {
    if (this.form.valid) {
      this.terms$
        .pipe(
          switchMap((terms) => {
            return this.usersService.editUserTerms({ version: terms.version });
          }),
          switchMap(() => this.authService.loadUser()),
          takeUntil(this.destroy$),
        )
        .subscribe(() => this.router.navigate(['']));
    }
  }
}
