import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TotalEmissionsNonDomesticFlightsTableTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-non-domestic-flights-table-template/total-emissions-non-domestic-flights-table-template.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerUkEts } from 'pmrv-api';

interface ViewModel {
  data: AviationAerUkEts;
}

@Component({
  selector: 'app-total-emissions-non-domestic-flights-table',
  imports: [SharedModule, TotalEmissionsNonDomesticFlightsTableTemplateComponent],
  template: `
    <app-total-emissions-non-domestic-flights-table-template
      [data]="(vm$ | async).data"></app-total-emissions-non-domestic-flights-table-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsNonDomesticFlightsTableComponent {
  vm$: Observable<ViewModel> = combineLatest([this.store.pipe(aerQuery.selectAer)]).pipe(
    map(([aer]) => {
      return {
        data: aer,
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
