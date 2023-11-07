import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { RequestTaskStore } from '@aviation/request-task/store';
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
}

@Component({
  selector: 'app-data-gaps',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, DataGapsSummaryTemplateComponent, DataGapsListTemplateComponent],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-data-gaps-summary-template
        [data]="vm.dataGaps"
        [affectedFlightsDataGaps]="vm.affectedFlightsDataGaps"
      ></app-data-gaps-summary-template>
      <app-data-gaps-list-template
        *ngIf="vm.dataGaps?.dataGapsDetails?.dataGaps"
        [dataGaps]="vm.dataGaps.dataGapsDetails.dataGaps"
      ></app-data-gaps-list-template>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataGapsComponent {
  vm$: Observable<ViewModel> = this.store.pipe(aerVerifyCorsiaQuery.selectAer).pipe(
    map((aer) => {
      return {
        heading: aerHeaderTaskMap['dataGaps'],
        dataGaps: aer.dataGaps,
        affectedFlightsDataGaps: calculateAffectedFlightsDataGaps(aer.dataGaps?.dataGapsDetails?.dataGaps ?? []),
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
