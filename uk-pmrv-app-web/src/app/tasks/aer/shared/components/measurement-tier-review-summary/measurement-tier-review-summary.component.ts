import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import {
  AerApplicationReviewRequestTaskPayload,
  AerApplicationVerificationSubmitRequestTaskPayload,
  MeasurementOfCO2Emissions,
} from 'pmrv-api';

import { AerService } from '../../../core/aer.service';

@Component({
  selector: 'app-measurement-tier-review-summary',
  template: `
    <app-aer-task-review
      [heading]="heading$ | async"
      [breadcrumb]="[
        {
          text: taskKey | monitoringApproachEmissionDescription,
          link: [taskKey === 'MEASUREMENT_CO2' ? 'measurement-co2' : 'measurement-n2o'],
        },
      ]"
      linkText="{{ taskKey | monitoringApproachEmissionDescription }}"
      returnToLink="../..">
      <app-measurement-tier-summary
        [payload]="payload$ | async"
        [taskKey]="taskKey"
        [index]="index$ | async"></app-measurement-tier-summary>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MeasurementTierReviewSummaryComponent {
  taskKey = this.route.snapshot.data.taskKey;
  payload$ = this.aerService.getPayload() as Observable<
    AerApplicationReviewRequestTaskPayload | AerApplicationVerificationSubmitRequestTaskPayload
  >;
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  heading$ = combineLatest([this.payload$, this.index$]).pipe(
    first(),
    map(([payload, index]) => {
      const emissionPointEmission = (payload.aer.monitoringApproachEmissions[this.taskKey] as MeasurementOfCO2Emissions)
        .emissionPointEmissions[index];

      const aerEmissionPoint = payload.aer.emissionPoints.find(
        (emissionPoint) => emissionPoint.id === emissionPointEmission.emissionPoint,
      );

      return `${aerEmissionPoint.reference}`;
    }),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
  ) {}
}
