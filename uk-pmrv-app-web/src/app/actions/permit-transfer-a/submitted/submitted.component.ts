import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

import { PermitTransferAActionService } from '../core/permit-transfer-a.service';

@Component({
  selector: 'app-permit-transfer-a-action-submitted',
  template: `
    <app-base-action-container-component
      [header]="(route.data | async)?.pageTitle"
      [customContentTemplate]="customContentTemplate"
      [expectedActionType]="['PERMIT_TRANSFER_A_APPLICATION_SUBMITTED']"></app-base-action-container-component>

    <ng-template #customContentTemplate>
      <app-permit-transfer-details-summary-template
        [payload]="payload$ | async"
        [allowChange]="false"
        [files]="files$ | async"></app-permit-transfer-details-summary-template>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedComponent {
  payload$: Observable<PermitTransferAApplicationRequestTaskPayload> = this.permitTransferAActionService.getPayload();
  files$ = this.payload$.pipe(
    map((payload) => this.permitTransferAActionService.getDownloadUrlFiles(payload.reasonAttachments)),
  );

  constructor(
    readonly route: ActivatedRoute,
    readonly permitTransferAActionService: PermitTransferAActionService,
  ) {}
}
