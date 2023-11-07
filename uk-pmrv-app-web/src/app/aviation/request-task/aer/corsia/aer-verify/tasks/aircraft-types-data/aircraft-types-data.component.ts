import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AircraftTypesDataTableComponent } from '@aviation/shared/components/aer/aircraft-types-table/aircraft-types-data-table.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerAircraftDataDetails } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: AviationAerAircraftDataDetails[];
}

@Component({
  selector: 'app-aircraft-types-data',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, AircraftTypesDataTableComponent],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-aircraft-types-data-table
        [headingText]="'File uploaded'"
        [aviationAerAircraftDataDetails]="vm.data"
      ></app-aircraft-types-data-table>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AircraftTypesDataComponent {
  vm$: Observable<ViewModel> = this.store.pipe(aerVerifyCorsiaQuery.selectAer).pipe(
    map((aer) => {
      return {
        heading: aerHeaderTaskMap['aviationAerAircraftData'],
        data: aer.aviationAerAircraftData.aviationAerAircraftDataDetails,
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
