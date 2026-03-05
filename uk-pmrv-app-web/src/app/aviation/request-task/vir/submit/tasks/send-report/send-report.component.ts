import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { virQuery } from '@aviation/request-task/vir/vir.selectors';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { getSubmitStatus } from '@aviation/shared/components/vir/vir-task-list/vir-task-status.util';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

interface ViewModel {
  isSendReportAvailable: boolean;
  heading: string;
  requestId: string;
  isEditable: boolean;
}

@Component({
  selector: 'app-send-report',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, RouterLink],
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportComponent {
  isSubmitted$: BehaviorSubject<boolean> = new BehaviorSubject(false);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(virQuery.selectPayload),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([requestInfo, virPayload, isEditable]) => {
      return {
        isSendReportAvailable: getSubmitStatus(virPayload, 'sendReport') === 'not started',
        heading: 'Submit responses',
        requestId: requestInfo.id,
        isEditable: isEditable,
      };
    }),
  );

  constructor(
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
  ) {}

  onConfirm() {
    this.store.virDelegate
      .submitVir()
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.isSubmitted$.next(true);
      });
  }
}
