import { CurrencyPipe, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';

import { GovukTableColumn, LinkDirective, TableComponent } from 'govuk-components';

import { FeeRow } from './fees.model';
import { FeesService } from './fees.service';

@Component({
  selector: 'app-fees',
  imports: [PageHeadingComponent, TableComponent, LinkDirective, CurrencyPipe, DatePipe],
  templateUrl: './fees.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FeesComponent {
  private readonly feesService = inject(FeesService);

  readonly fees = toSignal(this.feesService.getFees(), { initialValue: [] as FeeRow[] });

  readonly columns: GovukTableColumn<FeeRow>[] = [
    { field: 'workflow', header: 'Workflow' },
    { field: 'currentAmount', header: 'Current payment amount' },
    { field: 'scheduledChange', header: 'Scheduled change' },
    { field: 'key', header: 'Actions', hiddenHeader: true },
  ];
}
