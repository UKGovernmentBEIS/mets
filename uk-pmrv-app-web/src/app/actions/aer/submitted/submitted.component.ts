import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, filter, map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { reportableAerRequestActionTypes, reportComponentRequestActionTypes } from '../../request-action.util';
import { AerService } from '../core/aer.service';
import { monitoringApproachMap } from '../core/monitoringApproaches';
import { getAerTitle } from './submitted';

@Component({
  selector: 'app-aer-submitted',
  templateUrl: './submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedComponent {
  payload$ = this.aerService.getPayload() as Observable<
    AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
  >;

  requestActionType$ = this.aerService.requestAction$.pipe(map((requestAction) => requestAction.type));

  hasReport$ = this.requestActionType$.pipe(
    map((requestActionType) => reportableAerRequestActionTypes.includes(requestActionType)),
  );

  aerTitle$ = combineLatest([this.requestActionType$, this.payload$]).pipe(
    map(([requestActionType, payload]) => getAerTitle(requestActionType, payload)),
  );

  isVerificationSubmitted$ = this.requestActionType$.pipe(
    map((requestActionType) => requestActionType === 'AER_APPLICATION_VERIFICATION_SUBMITTED'),
  );

  dataComponent$ = this.requestActionType$.pipe(
    map(
      (requestActionType) =>
        reportComponentRequestActionTypes.find((entry) => entry.requestActionTypes.includes(requestActionType))
          ?.reportComponent,
    ),
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
