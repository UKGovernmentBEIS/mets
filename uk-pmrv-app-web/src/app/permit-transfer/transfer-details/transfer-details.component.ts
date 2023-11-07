import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { PermitTransferBApplicationRequestTaskPayload } from 'pmrv-api';

import { PERMIT_TRANSFER_B_FORM } from '../core/permit-transfer-task-form.token';
import { PermitTransferStore } from '../store/permit-transfer.store';
import { transferDetailsFormProvider } from './transfer-details-form.provider';

@Component({
  selector: 'app-transfer-details',
  templateUrl: './transfer-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [transferDetailsFormProvider],
})
export class TransferDetailsComponent {
  permitTransferDetails$: Observable<PermitTransferBApplicationRequestTaskPayload['permitTransferDetails']> =
    this.store.pipe(map((state) => state.permitTransferDetails));
  files$ = this.permitTransferDetails$.pipe(
    map((payload) => this.store.getDownloadUrlFiles(payload.reasonAttachments)),
  );

  hideSubmit$ = this.store.isEditable$.pipe(map((isEditable) => !isEditable));
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);

  constructor(
    @Inject(PERMIT_TRANSFER_B_FORM) readonly form: FormGroup,
    protected readonly store: PermitTransferStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit() {
    this.store
      .postTransferDetailsConfirmation({ ...this.form.value })
      .pipe(first(), this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route }));
  }
}
