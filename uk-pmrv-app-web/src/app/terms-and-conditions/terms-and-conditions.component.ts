import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { distinctUntilKeyChanged, Observable, switchMap, takeUntil } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { LatestTermsStore } from '@core/store/latest-terms/latest-terms.store';

import { GovukValidators } from 'govuk-components';

import { TermsAndConditionsService, TermsDTO } from 'pmrv-api';

@Component({
  selector: 'app-terms-and-conditions',
  templateUrl: './terms-and-conditions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class TermsAndConditionsComponent {
  latestTerms$: Observable<TermsDTO> = this.latestTermsStore.pipe(distinctUntilKeyChanged('version'));

  form: UntypedFormGroup = this.fb.group({
    terms: [null, GovukValidators.required('You should accept terms and conditions to proceed')],
  });

  constructor(
    private readonly router: Router,
    private readonly termsAndConditionsService: TermsAndConditionsService,
    private readonly authService: AuthService,
    private readonly latestTermsStore: LatestTermsStore,
    private readonly fb: UntypedFormBuilder,
    private readonly destroy$: DestroySubject,
  ) {}

  submitTerms(): void {
    if (this.form.valid) {
      this.latestTerms$
        .pipe(
          switchMap((terms) => {
            return this.termsAndConditionsService.editUserTerms({ version: terms.version });
          }),
          switchMap(() => this.authService.loadUserTerms()),
          takeUntil(this.destroy$),
        )
        .subscribe(() => this.router.navigate(['']));
    }
  }
}
