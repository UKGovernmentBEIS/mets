import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-nace-codes',
  template: `
    <app-action-task header="NACE codes" [breadcrumb]="true">
      <app-nace-codes-summary-template [naceCodes]="naceCodes$ | async"></app-nace-codes-summary-template>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NaceCodesComponent {
  naceCodes$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.aer.naceCodes));

  constructor(private readonly aerService: AerService) {}
}
