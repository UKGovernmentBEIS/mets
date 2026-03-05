import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-amend-summary',
  template: `
    <app-amend-summary-template></app-amend-summary-template>
    <dl govuk-summary-list class="govuk-summary-list--no-border summary-list--edge-border">
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>I have made changes and want to mark this task as complete</dt>
        <dd govukSummaryListRowValue>Yes</dd>
      </div>
    </dl>
    <app-return-link [returnLink]="'../../..'"></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AmendSummaryComponent {}
