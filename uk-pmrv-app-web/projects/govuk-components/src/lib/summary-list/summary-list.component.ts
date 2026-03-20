import {
  ChangeDetectionStrategy,
  Component,
  ContentChild,
  ContentChildren,
  Input,
  QueryList,
  TemplateRef,
} from '@angular/core';

import { SummaryListRowDirective } from './directives/summary-list-row.directive';
import { SummaryItem } from './summary-list.interface';

@Component({
  selector: 'dl[govuk-summary-list]',
  standalone: false,
  templateUrl: './summary-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    '[class.govuk-summary-list]': 'true',
    '[class.govuk-summary-list--no-border]': '!this.hasBorders',
  },
})
export class SummaryListComponent {
  @Input() details: SummaryItem[];
  @Input() hasBorders = true;

  @ContentChildren(SummaryListRowDirective) rows: QueryList<SummaryListRowDirective>;
  @ContentChild('keyTemplate') keyTemplate: TemplateRef<any>;
  @ContentChild('valueTemplate') valueTemplate: TemplateRef<any>;
}
