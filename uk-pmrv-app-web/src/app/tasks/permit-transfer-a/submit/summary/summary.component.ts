import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PermitTransferAService } from '@tasks/permit-transfer-a/core/permit-transfer-a.service';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-transfer-a-summary',
  template: `
    <app-page-heading>Check your answers</app-page-heading>
    <app-permit-transfer-details-summary-template
      [payload]="payload$ | async"
      [allowChange]="isEditable$ | async"
      [files]="files$ | async"></app-permit-transfer-details-summary-template>
    <div *ngIf="(isEditable$ | async) === true" class="govuk-button-group">
      <button appPendingButton govukButton type="button" (click)="onConfirm()">Confirm and complete</button>
    </div>
    <a govukLink routerLink="..">Return to: Permit transfer application</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  isEditable$ = this.permitTransferAService.isEditable$;
  payload$: Observable<PermitTransferAApplicationRequestTaskPayload> = this.permitTransferAService.getPayload();
  files$ = this.payload$.pipe(
    map((payload) => this.permitTransferAService.getDownloadUrlFiles(payload.reasonAttachments)),
  );

  constructor(
    readonly permitTransferAService: PermitTransferAService,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    this.permitTransferAService
      .sendDataForPost(undefined, true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route, state: { notification: true } }));
  }
}
