import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TotalEmissionsStandardFuelsTableTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-standard-fuels-table-template/total-emissions-standard-fuels-table-template.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerUkEts } from 'pmrv-api';

interface ViewModel {
  data: AviationAerUkEts;
}

@Component({
  selector: 'app-total-emissions-standard-fuels-table',
  template: `
    <app-total-emissions-standard-fuels-table-template
      [data]="(vm$ | async).data"></app-total-emissions-standard-fuels-table-template>
  `,
  standalone: true,
  imports: [SharedModule, TotalEmissionsStandardFuelsTableTemplateComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsStandardFuelsTableComponent {
  vm$: Observable<ViewModel> = combineLatest([this.store.pipe(aerQuery.selectAer)]).pipe(
    map(([aer]) => {
      return {
        data: aer,
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
