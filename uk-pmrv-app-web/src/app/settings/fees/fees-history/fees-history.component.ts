import { CurrencyPipe, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';

import { switchMap } from 'rxjs';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { SharedModule } from '@shared/shared.module';

import {
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from 'govuk-components';

import { FeeHistoryPage } from '../fees.model';
import { FeesService } from '../fees.service';

@Component({
  selector: 'app-fees-history',
  imports: [
    PageHeadingComponent,
    SharedModule,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    CurrencyPipe,
    DatePipe,
  ],
  templateUrl: './fees-history.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FeesHistoryComponent {
  private readonly feesService = inject(FeesService);

  readonly pageSize = 30;
  readonly page = signal(1);

  readonly history = toSignal(
    toObservable(this.page).pipe(switchMap((page) => this.feesService.getFeeHistory(page, this.pageSize))),
    { initialValue: { records: [], total: 0 } as FeeHistoryPage },
  );

  onPageChange(page: number): void {
    this.page.set(page);
  }
}
