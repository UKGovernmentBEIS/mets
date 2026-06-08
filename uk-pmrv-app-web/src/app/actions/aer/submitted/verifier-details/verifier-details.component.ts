import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import {
  AerApplicationCompletedRequestActionPayload,
  AerApplicationVerificationSubmittedRequestActionPayload,
} from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-verifier-details',
  standalone: false,
  template: `
    <app-action-task header="Verifier details" [breadcrumb]="true">
      <app-verifier-details-group
        [verificationReport]="verificationReportData$ | async"
        [emissionsTradingScheme]="emissionsTradingScheme()"></app-verifier-details-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifierDetailsComponent {
  verificationReportData$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationCompletedRequestActionPayload | AerApplicationVerificationSubmittedRequestActionPayload
    >
  ).pipe(map((payload) => payload.verificationReport));

  readonly emissionsTradingScheme = this.aerService.getEmissionsTradingSchemeSignal();

  constructor(private readonly aerService: AerService) {}
}
