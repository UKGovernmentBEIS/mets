import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-data-gaps',
  template: `
    <app-action-task header="Methodologies to close data gaps" [breadcrumb]="true">
      <app-data-gaps-group [dataGapsInfo]="dataGapsInfo$ | async"></app-data-gaps-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataGapsComponent {
  dataGapsInfo$ = (this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>).pipe(
    map((payload) => payload.verificationReport.methodologiesToCloseDataGaps),
  );

  constructor(private readonly aerService: AerService) {}
}
