import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { PermitNotificationService } from '../../core/permit-notification.service';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AnswersComponent {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  constructor(
    readonly store: CommonTasksStore,
    readonly permitNotificationService: PermitNotificationService,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  confirm(): void {
    this.permitNotificationService
      .getPayload()
      .pipe(
        first(),
        switchMap((payload) =>
          this.permitNotificationService.postTaskSave(
            {
              permitNotification: {
                ...payload.permitNotification,
              },
            },
            {},
            true,
            'DETAILS_CHANGE',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route, state: { notification: true } }));
  }
}
