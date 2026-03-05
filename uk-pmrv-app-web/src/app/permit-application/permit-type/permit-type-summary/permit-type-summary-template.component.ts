import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { permitTypeMap } from '@permit-application/shared/utils/permit';

@Component({
  selector: 'app-permit-type-summary-template',
  template: `
    <dl *ngIf="permitType" govuk-summary-list [hasBorders]="false" [class.summary-list--edge-border]="hasBottomBorder">
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Type</dt>
        <dd govukSummaryListRowValue>{{ permitTypeMap?.[permitType] }}</dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitTypeSummaryTemplateComponent {
  @Input() permitType: 'GHGE' | 'HSE' | 'WASTE';
  @Input() hasBottomBorder = true;
  permitTypeMap = permitTypeMap;
}
