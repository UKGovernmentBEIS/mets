import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import {
  AerApplicationCompletedRequestActionPayload,
  AerApplicationSubmittedRequestActionPayload,
  MeasurementOfCO2Emissions,
} from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-measurement-tier',
  template: `
    <app-action-task
      [header]="heading$ | async"
      [breadcrumb]="true"
    >
      <app-measurement-tier-summary
        [payload]="payload$ | async"
        [taskKey]="taskKey"
        [index]="index$ | async"
      ></app-measurement-tier-summary>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MeasurementTierComponent {
  taskKey = this.route.snapshot.data.taskKey;
  payload$ = this.aerService.getPayload() as Observable<
    AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
  >;
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  heading$ = combineLatest([this.payload$, this.index$]).pipe(
    first(),
    map(([payload, index]) => {
      const emissionPointEmission = (
        payload.aer.monitoringApproachEmissions?.[this.taskKey] as MeasurementOfCO2Emissions
      ).emissionPointEmissions[index];

      const aerEmissionPoint = payload.aer.emissionPoints.find(
        (emissionPoint) => emissionPoint.id === emissionPointEmission.emissionPoint,
      );

      return `${aerEmissionPoint.reference}`;
    }),
  );

  constructor(private readonly aerService: AerService, private readonly route: ActivatedRoute) {}
}
