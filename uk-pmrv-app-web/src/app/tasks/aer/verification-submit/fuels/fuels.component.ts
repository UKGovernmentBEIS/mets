import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-fuels',
  template: `
    <app-page-heading>Fuels and equipment inventory</app-page-heading>
    <app-fuels-group [aerData]="aerData$ | async"></app-fuels-group>
    <app-return-link></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FuelsComponent {
  aerData$ = (this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>).pipe(
    map((payload) => payload.aer),
  );

  constructor(private readonly aerService: AerService) {}
}
