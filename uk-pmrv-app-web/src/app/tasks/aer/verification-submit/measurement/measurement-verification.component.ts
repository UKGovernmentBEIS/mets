import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-measurement-verification',
  template: `
    <app-page-heading>{{ taskKey | monitoringApproachEmissionDescription }} </app-page-heading>
    <app-measurement-group [data]="aerData$ | async" [taskKey]="taskKey"></app-measurement-group>
    <app-return-link></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MeasurementVerificationComponent {
  taskKey = this.route.snapshot.data.taskKey;
  aerData$ = (this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>).pipe(
    map((payload) => payload?.aer),
  );

  constructor(private readonly aerService: AerService, private readonly route: ActivatedRoute) {}
}
