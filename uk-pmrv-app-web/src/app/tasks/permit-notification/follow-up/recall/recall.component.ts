import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { BusinessErrorService } from '../../../../error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '../../../../error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '../../../../error/not-found-error';

@Component({
  selector: 'app-permit-notification-follow-up-recall-from-amends',
  template: `
    <div *ngIf="(isRecallSubmitted$ | async) === false; else confirmationTemplate">
      <app-page-heading size="xl">Are you sure you want to recall your response ?</app-page-heading>
      <div class="govuk-button-group">
        <button type="button" appPendingButton (click)="recall()" govukWarnButton>Yes, Recall your response</button>
      </div>
      <a govukLink routerLink="..">Return to: Awaiting follow up response to notification</a>
    </div>
    <ng-template #confirmationTemplate>
      <app-confirmation-shared title="Your response has been recalled"></app-confirmation-shared>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecallComponent {
  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  isRecallSubmitted$ = new BehaviorSubject<boolean>(false);

  recall(): void {
    this.taskId$
      .pipe(
        first(),
        switchMap((taskId) =>
          this.tasksService.processRequestTaskAction({
            requestTaskActionType: 'PERMIT_NOTIFICATION_FOLLOW_UP_RECALL_FROM_AMENDS',
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'EMPTY_PAYLOAD',
            } as RequestTaskActionPayload,
          }),
        ),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.isRecallSubmitted$.next(true));
  }
}
