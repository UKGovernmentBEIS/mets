import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationVerificationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-fuels',
  template: `
    <app-action-task header="Fuels and equipment inventory" [breadcrumb]="true">
      <app-fuels-group [aerData]="aerData$ | async"></app-fuels-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FuelsComponent {
  payload$ = this.aerService.getPayload() as Observable<AerApplicationVerificationSubmittedRequestActionPayload>;
  aerData$ = this.payload$.pipe(map((payload) => payload.aer));

  constructor(private readonly aerService: AerService) {}
}
