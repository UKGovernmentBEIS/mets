import { NgTemplateOutlet } from '@angular/common';
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  contentChild,
  inject,
  input,
  model,
  output,
  TemplateRef,
} from '@angular/core';

import { SafeHtmlPipe } from '../pipes';
import { GovukTableColumn, SortEvent } from './table.interface';

@Component({
  selector: 'govuk-table',
  imports: [NgTemplateOutlet, SafeHtmlPipe],
  templateUrl: './table.component.html',
  styleUrl: './table.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TableComponent<T> {
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  readonly columns = input<GovukTableColumn<T>[]>();
  readonly data = model<Pick<T, GovukTableColumn<T>['field']>[]>();
  readonly caption = input<string>();
  readonly description = input<string>();
  readonly emptyTableText = input<string>();
  readonly rowCssClasses = input<(rowItem: T) => string | string[]>();

  readonly sort = output<SortEvent>();
  readonly template = contentChild(TemplateRef);

  sortedField: GovukTableColumn<T>['field'];
  sortedColumn: GovukTableColumn<T>['header'];
  sortingDirection: 'ascending' | 'descending';

  sortBy(columnField: GovukTableColumn<T>['field']): void {
    this.sortingDirection =
      this.sortedField === columnField && this.sortingDirection === 'ascending' ? 'descending' : 'ascending';
    this.sortedField = columnField;
    this.sortedColumn = this.columns().find((column) => column.field === columnField).header;

    this.changeDetectorRef.markForCheck();

    this.sort.emit({ column: this.sortedField, direction: this.sortingDirection });
  }
}
