import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { iif, map, Observable } from 'rxjs';

import { CalculationOfPFCMonitoringApproach, CellAndAnodeType } from 'pmrv-api';

import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-pfc-approach-types-summary-template',
  template: `
    <dl
      *ngFor="let cellAnodeType of cellAnodeTypes$ | async"
      govuk-summary-list
      [hasBorders]="false"
      [class.summary-list--edge-border]="hasBottomBorder">
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Cell type</dt>
        <dd class="pre-wrap" govukSummaryListRowValue>{{ cellAnodeType.cellType }}</dd>
      </div>
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Anode type</dt>
        <dd class="pre-wrap" govukSummaryListRowValue>{{ cellAnodeType.anodeType }}</dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryTemplateComponent {
  @Input() showOriginal = false;
  @Input() hasBottomBorder = true;

  cellAnodeTypes$: Observable<CellAndAnodeType[]> = iif(
    () => this.showOriginal,
    this.store.getOriginalTask('monitoringApproaches'),
    this.store.getTask('monitoringApproaches'),
  ).pipe(map((task) => (task.CALCULATION_PFC as CalculationOfPFCMonitoringApproach)?.cellAndAnodeTypes));

  constructor(readonly store: PermitApplicationStore<PermitApplicationState>) {}
}
