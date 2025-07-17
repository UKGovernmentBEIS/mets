import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-calculation-approach-description-summary-template',
  template: `
    <dl
      *ngIf="
        ('CALCULATION_CO2' | monitoringApproachTask: !showOriginal | async)?.approachDescription as approachDescription
      "
      govuk-summary-list
      [hasBorders]="false"
      [class.summary-list--edge-border]="hasBottomBorder">
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Approach description</dt>
        <dd class="pre-wrap" govukSummaryListRowValue>{{ approachDescription }}</dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryTemplateComponent {
  @Input() showOriginal = false;
  @Input() hasBottomBorder = true;
}
