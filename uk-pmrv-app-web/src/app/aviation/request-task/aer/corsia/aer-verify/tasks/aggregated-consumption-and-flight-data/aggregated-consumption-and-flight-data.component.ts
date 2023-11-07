import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { RequestTaskStore } from '@aviation/request-task/store';
import { FlightDataTableComponent } from '@aviation/shared/components/aer/flight-data-table';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaAggregatedEmissionDataDetails } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: AviationAerCorsiaAggregatedEmissionDataDetails[];
}

@Component({
  selector: 'app-aggregated-consumption-and-flight-data',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, FlightDataTableComponent],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-flight-data-table [headingText]="'File uploaded'" [emissionDataDetails]="vm.data"></app-flight-data-table>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AggregatedConsumptionAndFlightDataComponent {
  vm$: Observable<ViewModel> = this.store.pipe(aerVerifyCorsiaQuery.selectAer).pipe(
    map((aer) => {
      return {
        heading: aerHeaderTaskMap['aggregatedEmissionsData'],
        data: aer.aggregatedEmissionsData.aggregatedEmissionDataDetails,
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
