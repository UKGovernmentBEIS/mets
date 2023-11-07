import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { BehaviorSubject, EMPTY, map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { catchBadRequest, ErrorCodes as BusinessErrorCode } from '@error/business-errors';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { PermitTransferAService } from '@tasks/permit-transfer-a/core/permit-transfer-a.service';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-transfer-send-application',
  templateUrl: './send-application.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendApplicationComponent implements OnInit {
  payload$: Observable<PermitTransferAApplicationRequestTaskPayload> = this.permitTransferAService.getPayload();
  isNotSubmitted$ = new BehaviorSubject(true);
  requestId$ = this.permitTransferAService.requestId;
  files$ = this.payload$.pipe(
    map((payload) => this.permitTransferAService.getDownloadUrlFiles(payload.reasonAttachments)),
  );
  errorMessage$: BehaviorSubject<string> = new BehaviorSubject(null);

  constructor(
    readonly permitTransferAService: PermitTransferAService,
    private readonly backlinkService: BackLinkService,
    readonly pendingRequest: PendingRequestService,
  ) {}

  ngOnInit(): void {
    this.backlinkService.show();
  }

  onSubmit() {
    this.permitTransferAService
      .postSubmit()
      .pipe(
        this.pendingRequest.trackRequest(),
        catchBadRequest(BusinessErrorCode.FORM1001, (res) => {
          this.errorMessage$.next(res.error.data[0]);
          return EMPTY;
        }),
      )
      .subscribe(() => {
        this.isNotSubmitted$.next(false);
        this.backlinkService.hide();
      });
  }
}
