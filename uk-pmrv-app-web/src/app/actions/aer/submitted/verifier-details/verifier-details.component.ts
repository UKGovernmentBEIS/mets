import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import {
  AerApplicationCompletedRequestActionPayload,
  AerApplicationVerificationSubmittedRequestActionPayload,
} from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-verifier-details',
  template: `
    <app-action-task header="Verifier details" [breadcrumb]="true">
      <app-verifier-details-group [verificationReport]="verificationReportData$ | async"></app-verifier-details-group>
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

  constructor(private readonly aerService: AerService) {}
}
