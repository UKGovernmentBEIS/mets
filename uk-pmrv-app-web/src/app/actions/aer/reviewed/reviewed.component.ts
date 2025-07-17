import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, filter, map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../core/aer.service';
import { monitoringApproachMap } from '../core/monitoringApproaches';

@Component({
  selector: 'app-aer-reviewed',
  templateUrl: './reviewed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewedComponent {
  payload$ = this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>;

  requestAction$ = this.aerService.requestAction$;
  aerTitle$ = combineLatest([this.requestAction$, this.payload$]).pipe(
    map(([requestAction, payload]) => {
      return requestAction?.type === 'AER_APPLICATION_REVIEW_SKIPPED'
        ? payload.reportingYear + ' completed without review'
        : payload.reportingYear + ' emissions report reviewed';
    }),
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
  hasVerificationReport$ = this.payload$.pipe(map((payload) => !!payload.verificationReport));
  isGHGE$: Observable<boolean> = this.payload$.pipe(
    map((payload) => payload?.permitOriginatedData?.permitType === 'GHGE'),
  );

  constructor(private readonly aerService: AerService) {}
}
