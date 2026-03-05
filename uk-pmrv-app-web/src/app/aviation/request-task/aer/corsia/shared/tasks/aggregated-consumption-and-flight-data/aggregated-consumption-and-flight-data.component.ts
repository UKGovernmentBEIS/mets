import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { FlightDataTableComponent } from '@aviation/shared/components/aer/flight-data-table';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaAggregatedEmissionDataDetails } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: AviationAerCorsiaAggregatedEmissionDataDetails[];
  showDecision: boolean;
  isCorsia: boolean;
}

@Component({
  selector: 'app-aggregated-consumption-and-flight-data',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, FlightDataTableComponent, AerReviewDecisionGroupComponent],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-flight-data-table
        [headingText]="'File uploaded'"
        [emissionDataDetails]="vm.data"
        [isCorsia]="vm.isCorsia"></app-flight-data-table>
      <app-aviation-aer-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="aggregatedEmissionsData"></app-aviation-aer-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AggregatedConsumptionAndFlightDataComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectAer),
    this.store.pipe(aerQuery.selectIsCorsia),
  ]).pipe(
    map(([type, aer, isCorsia]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.aggregatedEmissionsData,
        data: aer.aggregatedEmissionsData.aggregatedEmissionDataDetails,
        showDecision: showReviewDecisionComponent.includes(type),
        isCorsia: isCorsia,
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
