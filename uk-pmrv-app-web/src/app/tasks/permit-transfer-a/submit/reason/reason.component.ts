import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PermitTransferAService } from '@tasks/permit-transfer-a/core/permit-transfer-a.service';
import { PERMIT_TRANSFER_A_FORM } from '@tasks/permit-transfer-a/core/permit-transfer-a-task-form.token';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

import { reasonFormProvider } from './reason-form.provider';

@Component({
  selector: 'app-transfer-a-reason',
  templateUrl: './reason.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [reasonFormProvider],
})
export class TransferAReasonComponent {
  hideSubmit$ = this.permitTransferAService.isEditable$.pipe(map((isEditable) => !isEditable));

  constructor(
    @Inject(PERMIT_TRANSFER_A_FORM) readonly form: FormGroup,
    readonly permitTransferAService: PermitTransferAService,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    if (!this.form.dirty) {
      this.router.navigate(['..', 'date'], { relativeTo: this.route });
    } else {
      this.permitTransferAService
        .sendDataForPost(
          {
            reason: this.form.value.reason,
            reasonAttachments: this.form.value.reasonAttachments?.map((file) => file.uuid),
          } as Partial<PermitTransferAApplicationRequestTaskPayload>,
          undefined,
          this.form.value?.reasonAttachments
            ?.map((attachment) => {
              return {
                [attachment.uuid]: attachment.file.name,
              };
            })
            .reduce((prev, cur) => ({ ...prev, ...cur }), {}),
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['..', 'date'], { relativeTo: this.route }));
    }
  }

  getDownloadUrl() {
    return this.permitTransferAService.getBaseFileDownloadUrl();
  }
}
