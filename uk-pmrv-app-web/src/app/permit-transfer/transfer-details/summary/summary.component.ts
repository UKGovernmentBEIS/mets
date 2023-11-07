import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { PermitTransferBApplicationRequestTaskPayload } from 'pmrv-api';

import { PermitTransferStore } from '../../store/permit-transfer.store';

@Component({
  selector: 'app-transfer-details-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferDetailsConfirmationSummaryComponent {
  isEditable$ = this.store.pipe(
    map((state) => state.isEditable && state.requestTaskType !== 'PERMIT_TRANSFER_B_APPLICATION_REVIEW'),
  );
  transferDetails$: Observable<PermitTransferBApplicationRequestTaskPayload['permitTransferDetails']> = this.store.pipe(
    map((state) => state.permitTransferDetails),
  );
  files$ = this.transferDetails$.pipe(map((payload) => this.store.getDownloadUrlFiles(payload.reasonAttachments)));
  transferDetailsConfirmation$ = this.store.pipe(map((state) => state.permitTransferDetailsConfirmation));

  constructor(protected readonly store: PermitTransferStore) {}
}
