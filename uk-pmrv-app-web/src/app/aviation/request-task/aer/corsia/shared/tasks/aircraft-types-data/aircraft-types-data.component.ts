import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { AircraftTypesDataTableComponent } from '@aviation/shared/components/aer/aircraft-types-table/aircraft-types-data-table.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerAircraftDataDetails } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: AviationAerAircraftDataDetails[];
  showDecision: boolean;
}

@Component({
  selector: 'app-aircraft-types-data',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, AircraftTypesDataTableComponent, AerReviewDecisionGroupComponent],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-aircraft-types-data-table
        [headingText]="'File uploaded'"
        [aviationAerAircraftDataDetails]="vm.data"></app-aircraft-types-data-table>
      <app-aviation-aer-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="aviationAerAircraftData"></app-aviation-aer-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AircraftTypesDataComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectAer),
  ]).pipe(
    map(([type, aer]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.aviationAerAircraftData,
        data: aer.aviationAerAircraftData.aviationAerAircraftDataDetails,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
