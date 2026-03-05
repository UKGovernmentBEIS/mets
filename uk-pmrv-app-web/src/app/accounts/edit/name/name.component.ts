import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, map, Observable, of, switchMap } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { InstallationAccountPermitDTO, InstallationAccountsService, InstallationAccountUpdateService } from 'pmrv-api';

import { EDIT_ACCOUNT_FORM, nameFormProvider } from './name-form.provider';

@Component({
  selector: 'app-name',
  template: `
    <app-page-heading size="l">Edit installation name</app-page-heading>
    <p class="govuk-body">
      This is the name that will be used to find the account. This must not be the same as another registered
      installation name, unless you receive an existing permit as part of a transfer.
    </p>
    <form (ngSubmit)="onSubmit()" [formGroup]="form">
      <govuk-error-summary *ngIf="isSummaryDisplayed$ | async" [form]="form"></govuk-error-summary>
      <div class="govuk-grid-row">
        <div class="govuk-grid-column-one-half">
          <div formControlName="installationName" govuk-text-input></div>
        </div>
      </div>
      <button appPendingButton govukButton type="submit">Confirm and complete</button>
    </form>
    <a govukLink routerLink="../..">Return to: Installation details</a>
  `,
  providers: [nameFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NameComponent implements OnInit {
  account$ = (
    this.route.data as Observable<{
      accountPermit: InstallationAccountPermitDTO;
    }>
  ).pipe(map((state) => state.accountPermit.account));
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);

  constructor(
    @Inject(EDIT_ACCOUNT_FORM) readonly form: UntypedFormGroup,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly accountUpdateService: InstallationAccountUpdateService,
    private readonly accounτService: InstallationAccountsService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit() {
    this.backLinkService.show();
  }

  onSubmit(): void {
    if (!this.form.valid) {
      this.isSummaryDisplayed$.next(true);
    } else {
      this.account$
        .pipe(
          first(),
          switchMap((account) =>
            this.form.dirty
              ? this.accounτService
                  .isExistingAccountName(this.form.get('installationName').value, account.id)
                  .pipe(map((response) => ({ reportProgress: response, accountId: account.id })))
              : of(null),
          ),
          switchMap((response) =>
            response?.reportProgress === false
              ? this.accountUpdateService.updateInstallationName(response.accountId, {
                  ...this.form.value,
                })
              : of(null),
          ),
        )
        .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
    }
  }
}
