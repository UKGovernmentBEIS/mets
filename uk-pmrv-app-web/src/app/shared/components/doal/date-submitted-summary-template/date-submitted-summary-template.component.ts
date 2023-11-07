import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { DateSubmittedToAuthority } from 'pmrv-api';

@Component({
  selector: 'app-doal-date-submitted-summary-template',
  template: `
    <dl govuk-summary-list>
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>When was the relevant information submitted to the authority?</dt>
        <dd govukSummaryListRowValue>{{ dateSubmittedToAuthority.date | govukDate }}</dd>
        <dd govukSummaryListRowActions *ngIf="editable">
          <a govukLink routerLink=".." [state]="{ changing: true }">Change</a>
        </dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DateSubmittedSummaryTemplateComponent {
  @Input() dateSubmittedToAuthority: DateSubmittedToAuthority;
  @Input() editable: boolean;
}
