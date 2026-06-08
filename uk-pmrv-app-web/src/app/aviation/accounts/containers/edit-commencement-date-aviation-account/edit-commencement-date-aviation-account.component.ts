import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap, tap } from 'rxjs';

import { AviationAccountFormProvider } from '@aviation/accounts/services';
import { AviationAccountsStore, selectAccountInfo } from '@aviation/accounts/store';

import { GovukValidators } from 'govuk-components';

import { AviationAccountReportingStatusService } from 'pmrv-api';

@Component({
  selector: 'app-edit-commencement-date-aviation-account',
  standalone: false,
  template: `
    <app-wizard-step (formSubmit)="onContinue()" [formGroup]="form" heading="Edit first year of reporting obligation">
      <p class="govuk-body">First year of reporting obligation</p>
      <div formControlName="commencementDate" govuk-date-input></div>
      <div govuk-textarea label="Reason" formControlName="reason"></div>
    </app-wizard-step>
    <a govukLink routerLink="../">Return to aviation operator's details</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditCommencementDateAviationAccountComponent implements OnInit, OnDestroy {
  private readonly accountInfo$ = this.store.pipe(selectAccountInfo, first());
  public readonly upsertStatus = this.store.getState().currentAccount?.upsertFirstYearOfReportingObligation;

  maxReasonLength = 2000;
  form = new FormGroup({
    commencementDate: this.formProvider.getCommencementDateFormControl(true),
    reason: new FormControl(this.upsertStatus?.reason, {
      validators: [
        GovukValidators.required('Enter a reason'),
        GovukValidators.maxLength(this.maxReasonLength, `Enter up to ${this.maxReasonLength} characters`),
      ],
    }),
  });

  constructor(
    private readonly formProvider: AviationAccountFormProvider,
    private readonly store: AviationAccountsStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly reportingStatusService: AviationAccountReportingStatusService,
  ) {}

  ngOnInit(): void {
    this.accountInfo$.subscribe((response) => {
      this.form.patchValue({
        commencementDate: new Date(this.upsertStatus?.commencementDate ?? response?.commencementDate) as any,
      });
    });
  }

  ngOnDestroy(): void {
    const { account, reportingStatus } = this.store.getState().currentAccount;

    this.reportingStatusService
      .getAllReportingStatuses(account.aviationAccount.id, 0, reportingStatus?.paging.pageSize)
      .pipe(
        switchMap(() => {
          return this.reportingStatusService.getAllReportingStatuses(
            account.aviationAccount.id,
            0,
            reportingStatus?.paging.pageSize,
          );
        }),
        tap((res) => {
          this.store.setReportingStatuses((res as any)?.reportingStatusList);
          this.store.setReportingStatusTotal((res as any)?.total);
        }),
      )
      .subscribe();
  }

  onContinue() {
    if (this.form.valid) {
      this.store.editFirstYearOfReportingObligation({
        commencementDate: this.form.value.commencementDate,
        reason: this.form.value.reason,
      });
      this.router.navigate(['./summary'], { relativeTo: this.route });
    }
  }
}
