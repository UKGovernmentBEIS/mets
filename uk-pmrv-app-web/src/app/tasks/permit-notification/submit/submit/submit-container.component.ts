import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BehaviorSubject, map, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { PermitNotificationService } from '../../core/permit-notification.service';

@Component({
  selector: 'app-submit-container',
  templateUrl: './submit-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class SubmitContainerComponent {
  allowSubmit$ = this.permitNotificationService
    .getPayload()
    .pipe(map((state) => state?.sectionsCompleted['DETAILS_CHANGE']));
  isPermitNotificationSubmitted$ = new BehaviorSubject(false);
  competentAuthority$ = this.store.pipe(map((state) => state.requestTaskItem.requestInfo.competentAuthority));

  constructor(
    readonly store: CommonTasksStore,
    readonly pendingRequest: PendingRequestService,
    private readonly permitNotificationService: PermitNotificationService,
    private readonly destroy$: DestroySubject,
  ) {}

  onSubmit(): void {
    this.permitNotificationService
      .postSubmit('PERMIT_NOTIFICATION_SUBMIT_APPLICATION', 'EMPTY_PAYLOAD')
      .pipe(this.pendingRequest.trackRequest(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.isPermitNotificationSubmitted$.next(true);
      });
  }
}
