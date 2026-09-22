import { CurrencyPipe, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';

import { ButtonDirective, LinkDirective, WarningTextComponent } from 'govuk-components';

import { FeesService } from '../fees.service';

@Component({
  selector: 'app-cancel-scheduled-change',
  imports: [
    PageHeadingComponent,
    WarningTextComponent,
    ButtonDirective,
    LinkDirective,
    RouterLink,
    CurrencyPipe,
    DatePipe,
  ],
  templateUrl: './cancel-scheduled-change.component.html',
  providers: [CurrencyPipe, DatePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelScheduledChangeComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly feesService = inject(FeesService);

  private readonly key = this.route.snapshot.paramMap.get('key');

  readonly feeRow = toSignal(this.feesService.getFee(this.key));

  readonly isSubmitting = signal(false);
  readonly saveError = signal<string | null>(null);

  onConfirm(): void {
    const feeRow = this.feeRow();
    if (!feeRow) {
      return;
    }

    this.isSubmitting.set(true);
    this.saveError.set(null);

    this.feesService.cancelScheduledFeeUpdate(feeRow.id, feeRow.feeType).subscribe({
      next: () => this.navigateWithNotification(),
      error: () => this.failCancel(),
    });
  }

  private failCancel(): void {
    this.isSubmitting.set(false);
    this.saveError.set('Sorry, there was a problem cancelling the scheduled change. Try again later.');
  }

  private navigateWithNotification(): void {
    this.router.navigate(['../..'], {
      relativeTo: this.route,
      state: { notification: 'The scheduled fee change has been cancelled. The current fee will stay the same.' },
    });
  }
}
