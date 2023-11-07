import { ChangeDetectionStrategy, Component } from '@angular/core';

import { filter, map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../core/aer.service';
import { monitoringApproachMap } from '../core/monitoringApproaches';

@Component({
  selector: 'app-aer-submitted',
  templateUrl: './submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedComponent {
  payload$ = this.aerService.getPayload() as Observable<
    AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
  >;
  aerTitle$ = this.payload$.pipe(
    map((payload) =>
      payload.payloadType === 'AER_APPLICATION_SUBMITTED_PAYLOAD' ||
      payload.payloadType === 'AER_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD'
        ? payload.reportingYear + ' emissions report submitted'
        : payload.reportingYear + ' emissions report reviewed',
    ),
  );
  isVerificationSubmitted$ = this.payload$.pipe(
    map((payload) => payload.payloadType === 'AER_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD'),
  );
  isMeasurementOrN2OApproachesSelected$ = this.payload$.pipe(
    map(
      (payload) =>
        payload.aer.monitoringApproachEmissions['MEASUREMENT_CO2'] !== undefined ||
        payload.aer.monitoringApproachEmissions['MEASUREMENT_N2O'] !== undefined,
    ),
  );
  monitoringApproaches$ = this.payload$.pipe(
    filter((payload) => !!payload.aer.monitoringApproachEmissions),
    map(
      (payload) =>
        Object.keys(payload.aer.monitoringApproachEmissions).map((approach) => monitoringApproachMap(approach)) as {
          link: string;
          linkText: string;
        }[],
    ),
  );
  hasVerificationReport$ = this.payload$.pipe(
    map((payload) => !!(payload as AerApplicationCompletedRequestActionPayload).verificationReport),
  );
  isGHGE$: Observable<boolean> = this.payload$.pipe(
    map((payload) => payload?.permitOriginatedData?.permitType === 'GHGE'),
  );

  constructor(private readonly aerService: AerService) {}
}
