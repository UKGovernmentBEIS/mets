import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-n2o-approach-description-summary-template',
  template: `
    <dl
      *ngIf="
        ('MEASUREMENT_N2O' | monitoringApproachTask: !showOriginal | async)?.approachDescription as approachDescription
      "
      govuk-summary-list
      [hasBorders]="false"
      [class.summary-list--edge-border]="hasBottomBorder">
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Approach description</dt>
        <dd class="pre-wrap" govukSummaryListRowValue>
          {{ approachDescription }}
        </dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryTemplateComponent {
  @Input() showOriginal = false;
  @Input() hasBottomBorder = true;
}
