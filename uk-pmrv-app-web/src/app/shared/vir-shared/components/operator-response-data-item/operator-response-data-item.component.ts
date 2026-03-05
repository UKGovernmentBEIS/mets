import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { OperatorImprovementResponse } from 'pmrv-api';

@Component({
  selector: 'app-operator-response-data-item',
  template: `
    <dl govuk-summary-list [hasBorders]="false" class="govuk-!-margin-bottom-0">
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Operator's response</dt>
        <dd govukSummaryListRowValue class="pre-wrap">
          {{ operatorImprovementResponse.addressedDescription | textEllipsis }}
        </dd>
        <dd govukSummaryListRowActions></dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorResponseDataItemComponent {
  @Input() operatorImprovementResponse: OperatorImprovementResponse;
}
