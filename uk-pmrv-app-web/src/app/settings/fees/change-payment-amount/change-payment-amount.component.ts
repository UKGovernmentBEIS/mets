import { CurrencyPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';

import {
  ButtonDirective,
  ConditionalContentDirective,
  DateInputComponent,
  ErrorSummaryComponent,
  LinkDirective,
  RadioComponent,
  RadioOptionComponent,
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
  TextInputComponent,
} from 'govuk-components';

import { FeesService } from '../fees.service';
import { changePaymentAmountFormProvider } from './change-payment-amount-form.provider';
import { CHANGE_PAYMENT_AMOUNT_FORM } from './change-payment-amount-form.token';

@Component({
  selector: 'app-change-payment-amount',
  imports: [
    ReactiveFormsModule,
    PageHeadingComponent,
    ErrorSummaryComponent,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    TextInputComponent,
    DateInputComponent,
    RadioComponent,
    RadioOptionComponent,
    ConditionalContentDirective,
    LinkDirective,
    RouterLink,
    ButtonDirective,
    CurrencyPipe,
  ],
  templateUrl: './change-payment-amount.component.html',
  providers: [changePaymentAmountFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangePaymentAmountComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly feesService = inject(FeesService);

  readonly key = this.route.snapshot.paramMap.get('key');
  readonly feeRow = toSignal(this.feesService.getFee(this.key));

  readonly form = inject(CHANGE_PAYMENT_AMOUNT_FORM);
  readonly isErrorSummaryDisplayed = signal(false);

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.isErrorSummaryDisplayed.set(true);
      return;
    }

    const changeWhen = this.form.value.changeWhen as 'immediately' | 'future';
    const today = new Date();
    const effectiveDate =
      changeWhen === 'immediately'
        ? new Date(Date.UTC(today.getFullYear(), today.getMonth(), today.getDate()))
        : this.form.value.effectiveDate;

    this.router.navigate(['check-your-answers'], {
      relativeTo: this.route,
      state: {
        amount: Number(this.form.value.amount),
        effectiveDate,
        changeWhen,
      },
    });
  }
}
