import { CurrencyPipe, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';

import {
  ButtonDirective,
  LinkDirective,
  SummaryListComponent,
  SummaryListRowActionsDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from 'govuk-components';

import { FeeRow, FeeUpdateDraft } from '../fees.model';
import { FeesService } from '../fees.service';

@Component({
  selector: 'app-check-your-answers',
  imports: [
    PageHeadingComponent,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SummaryListRowActionsDirective,
    LinkDirective,
    ButtonDirective,
    RouterLink,
    CurrencyPipe,
    DatePipe,
  ],
  templateUrl: './check-your-answers.component.html',
  providers: [CurrencyPipe, DatePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckYourAnswersComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly feesService = inject(FeesService);
  private readonly currencyPipe = inject(CurrencyPipe);
  private readonly datePipe = inject(DatePipe);

  private readonly key = this.route.snapshot.paramMap.get('key');

  readonly draft = this.router.currentNavigation()?.extras.state as FeeUpdateDraft | undefined;
  readonly feeRow = toSignal(this.feesService.getFee(this.key));

  readonly isSubmitting = signal(false);
  readonly saveError = signal<string | null>(null);

  constructor() {
    if (!this.draft) {
      this.router.navigate(['..'], { relativeTo: this.route });
    }
  }

  onConfirm(): void {
    const feeRow = this.feeRow();
    const draft = this.draft;
    if (!feeRow || !draft) {
      return;
    }

    this.isSubmitting.set(true);
    this.saveError.set(null);

    const effectiveDate = this.datePipe.transform(draft.effectiveDate, 'yyyy-MM-dd');
    if (!effectiveDate) {
      this.failSave();
      return;
    }

    this.feesService
      .updateFee({
        id: feeRow.id,
        feeType: feeRow.feeType,
        amount: draft.amount,
        effectiveDate,
      })
      .subscribe({
        next: () => this.navigateWithNotification(feeRow, draft),
        error: () => this.failSave(),
      });
  }

  private failSave(): void {
    this.isSubmitting.set(false);
    this.saveError.set('Sorry, there was a problem saving the payment amount. Try again later.');
  }

  private navigateWithNotification(feeRow: FeeRow, draft: FeeUpdateDraft): void {
    const amount = this.currencyPipe.transform(draft.amount, 'GBP', 'symbol', '1.2-2');

    const notification = this.isEffectiveToday(draft.effectiveDate)
      ? `The payment amount for ${feeRow.workflow} has been updated to ${amount}`
      : `The payment amount for ${feeRow.workflow} will change to ${amount} on ${this.datePipe.transform(draft.effectiveDate, 'd MMMM yyyy')}`;

    this.router.navigate(['../../..'], { relativeTo: this.route, state: { notification } });
  }

  private isEffectiveToday(effectiveDate: Date): boolean {
    // effectiveDate is built at UTC midnight by DateInputComponent, so compare its UTC
    // calendar fields against today's local calendar fields (never off by a full day in UK time).
    const today = new Date();
    return (
      effectiveDate.getUTCFullYear() === today.getFullYear() &&
      effectiveDate.getUTCMonth() === today.getMonth() &&
      effectiveDate.getUTCDate() === today.getDate()
    );
  }
}
