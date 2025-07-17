import { NgForOf, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'app-multiple-methods-summary-template',
  standalone: true,
  imports: [GovukComponentsModule, RouterLinkWithHref, NgForOf, NgIf],
  template: `
    <h2 class="govuk-heading-m">Using multiple methods</h2>
    <dl govuk-summary-list>
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Reason for using more than one method to measure fuel consumption</dt>
        <dd govukSummaryListRowValue class="pre-wrap">{{ multipleMethodsExplanation }}</dd>
        <dd govukSummaryListRowActions>
          <a govukLink *ngIf="editable" [routerLink]="['../multiple-methods']" [queryParams]="{ change: 'true' }">
            Change
          </a>
        </dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MultipleMethodsSummaryTemplateComponent {
  @Input() editable = false;
  @Input() multipleMethodsExplanation: string;
}
