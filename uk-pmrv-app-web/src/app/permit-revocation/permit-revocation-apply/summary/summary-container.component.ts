import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { first, map, tap } from 'rxjs';

import {
  effectiveDateMinValidator,
  feeDateMinValidator,
  genericMinDateValidator,
  PERMIT_REVOCATION_TASK_FORM,
  permitRevocationFormProvider,
  stoppedDateMaxDateValidator,
} from '@permit-revocation/factory/permit-revocation-form-provider';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { BaseSuccessComponent } from '@shared/base-success/base-success.component';

@Component({
  selector: 'app-summary-container',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <govuk-notification-banner *ngIf="notification" type="success">
          <h1 class="govuk-notification-banner__heading">Details updated</h1>
        </govuk-notification-banner>
        <govuk-error-summary [form]="form"></govuk-error-summary>
        <app-page-heading>{{ (route.data | async)?.pageTitle }}</app-page-heading>
        <app-summary class="govuk-!-margin-bottom-6 govuk-!-display-block" [errors]="form?.errors"></app-summary>
        <a govukLink routerLink="../..">Return to: Permit revocation</a>
      </div>
    </div>
  `,
  providers: [permitRevocationFormProvider],

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryContainerComponent extends BaseSuccessComponent implements OnInit {
  notification: any;
  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  constructor(
    @Inject(PERMIT_REVOCATION_TASK_FORM) readonly form: UntypedFormGroup,
    readonly route: ActivatedRoute,
    readonly store: PermitRevocationStore,
  ) {
    super();
    this.notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.store
      .pipe(
        first(),
        tap((state) => {
          this.form.addValidators([effectiveDateMinValidator(), stoppedDateMaxDateValidator()]);

          if (state.permitRevocation?.feeDate) {
            this.form.addValidators(feeDateMinValidator());
          }
          if (state.permitRevocation?.annualEmissionsReportDate) {
            this.form.addValidators(genericMinDateValidator('annualEmissionsReportDate'));
          }
          if (state.permitRevocation?.surrenderDate) {
            this.form.addValidators(genericMinDateValidator('surrenderDate'));
          }
        }),
      )
      .subscribe(() => this.form.updateValueAndValidity());
  }
}
