import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { DataGapsListTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-list-template';
import { calculateAffectedFlightsDataGaps } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template-corsia/data-gaps.util';
import { DataGapsSummaryTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template-corsia/data-gaps-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaDataGaps } from 'pmrv-api';

interface ViewModel {
  heading: string;
  dataGaps: AviationAerCorsiaDataGaps;
  affectedFlightsDataGaps: number;
  showDecision: boolean;
}

@Component({
  selector: 'app-data-gaps',
  standalone: true,
  imports: [
    ReturnToLinkComponent,
    SharedModule,
    DataGapsSummaryTemplateComponent,
    DataGapsListTemplateComponent,
    AerReviewDecisionGroupComponent,
  ],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-data-gaps-summary-template
        [data]="vm.dataGaps"
        [affectedFlightsDataGaps]="vm.affectedFlightsDataGaps"></app-data-gaps-summary-template>
      <app-data-gaps-list-template
        *ngIf="vm.dataGaps?.dataGapsDetails?.dataGaps"
        [dataGaps]="vm.dataGaps.dataGapsDetails.dataGaps"></app-data-gaps-list-template>
      <app-aviation-aer-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="dataGaps"></app-aviation-aer-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataGapsComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectAer),
  ]).pipe(
    map(([type, aer]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.dataGaps,
        dataGaps: aer.dataGaps,
        affectedFlightsDataGaps: calculateAffectedFlightsDataGaps(aer.dataGaps?.dataGapsDetails?.dataGaps ?? []),
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
