import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { VerificationDataItem } from '../../types/verification-data-item.type';

@Component({
  selector: 'app-verification-data-item',
  template: `
    <dl govuk-summary-list [hasBorders]="false" class="govuk-!-margin-bottom-0">
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Verifier's recommendation</dt>
        <dd govukSummaryListRowValue class="pre-wrap">{{ verificationDataItem.explanation | textEllipsis }}</dd>
        <dd govukSummaryListRowActions></dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationDataItemComponent {
  @Input() verificationDataItem: VerificationDataItem;
}
