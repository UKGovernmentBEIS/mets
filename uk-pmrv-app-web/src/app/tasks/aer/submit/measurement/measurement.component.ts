import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { filter, map } from 'rxjs';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';
import { emissionPointEmissionsStatus } from './measurement-status';

@Component({
  selector: 'app-measurement',
  templateUrl: './measurement.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MeasurementComponent {
  taskKey = this.route.snapshot.data.taskKey;
  isEditable$ = this.aerService.isEditable$;

  aer$ = this.aerService.getPayload().pipe(
    filter((payload) => !!payload),
    map((payload) => (payload as AerApplicationSubmitRequestTaskPayload).aer),
  );

  emissionPointEmissions$ = this.aer$.pipe(
    map((aer) => (aer.monitoringApproachEmissions[this.taskKey] as any)?.emissionPointEmissions),
  );

  statuses$ = this.aerService.getPayload().pipe(
    filter((payload) => !!payload),
    map((payload) => {
      const aer = (payload as AerApplicationSubmitRequestTaskPayload).aer;
      return (
        (aer.monitoringApproachEmissions[this.taskKey] as any)?.emissionPointEmissions?.map((tier, index) =>
          emissionPointEmissionsStatus(this.taskKey, payload, index),
        ) ?? []
      );
    }),
  );

  constructor(private readonly aerService: AerService, private readonly route: ActivatedRoute) {}
}
