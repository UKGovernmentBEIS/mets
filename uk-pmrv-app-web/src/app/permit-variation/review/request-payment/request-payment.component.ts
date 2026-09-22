import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidatorFn } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { permitTypeMap } from '@permit-application/shared/utils/permit';

import { GovukValidators } from 'govuk-components';

import { RequestsService } from 'pmrv-api';

import { PermitVariationStore } from '../../store/permit-variation.store';

@Component({
  selector: 'app-permit-variation-request-payment',
  standalone: false,
  templateUrl: './request-payment.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestPaymentComponent implements OnInit {
  form: UntypedFormGroup;
  readonly isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);

  readonly returnToText$: Observable<string> = this.store.pipe(
    map((state) => `${permitTypeMap?.[state.permitType]} permit variation review`),
  );

  constructor(
    readonly store: PermitVariationStore,
    private readonly requestsService: RequestsService,
    private readonly fb: UntypedFormBuilder,
    private readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group(
      {
        amount: [
          null,
          [
            GovukValidators.required('Enter a new payment amount in pounds, like 1500 or 1500.50'),
            GovukValidators.notNaN('New payment amount must be a number, like 1500 or 1500.50'),
            GovukValidators.positiveNumber('New payment amount must be more than 0'),
          ],
        ],
        // The "Enter a date" required-error comes from the govuk-date-input directive's own
        // [isRequired]/emptyMessage below — adding GovukValidators.required here would duplicate it.
        dueDate: [null, [todayOrFutureDateValidator()]],
        notes: [null, [GovukValidators.maxLength(10000, 'Notes should not be more than 10000 characters')]],
      },
      {
        updateOn: 'change',
      },
    );
  }

  submitForm(): void {
    if (this.form.invalid) {
      this.isSummaryDisplayed$.next(true);
      this.form.markAllAsTouched();
      return;
    }

    const dueDate: Date = this.form.value.dueDate;
    const dueDateStr = `${dueDate.getFullYear()}-${String(dueDate.getMonth() + 1).padStart(2, '0')}-${String(dueDate.getDate()).padStart(2, '0')}`;
    this.requestsService
      .requestPayment(this.store.getState().requestId, {
        amount: String(this.form.value.amount),
        dueDate: dueDateStr,
        notes: this.form.value.notes,
      })
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route, state: { paymentRequestSent: true } }));
  }
}

function todayOrFutureDateValidator(): ValidatorFn {
  return (control: AbstractControl) => {
    if (!(control.value instanceof Date)) {
      return null;
    }
    const inputDate = `${control.value.getFullYear()}-${String(control.value.getMonth() + 1).padStart(2, '0')}-${String(control.value.getDate()).padStart(2, '0')}`;
    const now = new Date();
    const today = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
    return inputDate < today ? { notFutureOrPresentDate: 'The date must be today or in the future' } : null;
  };
}
