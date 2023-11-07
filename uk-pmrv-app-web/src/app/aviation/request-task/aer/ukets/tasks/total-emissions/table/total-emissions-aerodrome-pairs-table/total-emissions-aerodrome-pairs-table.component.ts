import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TotalEmissionsAerodromePairsTableTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-aerodrome-pairs-table-template/total-emissions-aerodrome-pairs-table-template.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerUkEts } from 'pmrv-api';

interface ViewModel {
  data: AviationAerUkEts;
}

@Component({
  selector: 'app-total-emissions-aerodrome-pairs-table',
  template: `
    <app-total-emissions-aerodrome-pairs-table-template
      [data]="(vm$ | async).data"
    ></app-total-emissions-aerodrome-pairs-table-template>
  `,
  standalone: true,
  imports: [SharedModule, TotalEmissionsAerodromePairsTableTemplateComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsAerodromePairsTableComponent {
  vm$: Observable<ViewModel> = combineLatest([this.store.pipe(aerQuery.selectAer)]).pipe(
    map(([aer]) => {
      return {
        data: aer,
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
