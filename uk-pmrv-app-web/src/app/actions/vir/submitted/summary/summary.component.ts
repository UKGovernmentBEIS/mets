import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';

import { VirApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { VirService } from '../../core/vir.service';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  verificationDataItem = this.route.snapshot.data.verificationDataItem as VerificationDataItem;
  reference = this.verificationDataItem.reference;
  virPayload$ = this.virService.payload$ as Observable<VirApplicationSubmittedRequestActionPayload>;
  operatorImprovementResponse$ = this.virPayload$.pipe(
    map((payload) => payload?.operatorImprovementResponses?.[this.reference]),
  );
  documentFiles$ = this.virPayload$.pipe(
    map((payload) =>
      payload?.operatorImprovementResponses?.[this.reference]?.files
        ? this.virService.getDownloadUrlFiles(payload?.operatorImprovementResponses?.[this.reference]?.files)
        : [],
    ),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly virService: VirService,
    private readonly route: ActivatedRoute,
  ) {}
}
