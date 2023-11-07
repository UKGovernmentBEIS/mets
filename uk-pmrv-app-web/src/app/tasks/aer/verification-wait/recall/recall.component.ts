import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';

@Component({
  selector: 'app-recall',
  templateUrl: './recall.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecallComponent implements PendingRequest {
  readonly isConfirmed$ = new BehaviorSubject(false);

  constructor(readonly pendingRequest: PendingRequestService, private readonly aerService: AerService) {}

  recall(): void {
    this.aerService
      .postSubmit('AER_RECALL_FROM_VERIFICATION')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.isConfirmed$.next(true));
  }
}
