import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { taskNotFoundError } from '@shared/errors/request-task-error';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../core/interfaces/pending-request.interface';
import { BusinessErrorService } from '../../../error/business-error/business-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../../error/not-found-error';
import { PermitApplicationState } from '../../store/permit-application.state';

@Component({
  selector: 'app-recall',
  template: `
    <app-page-heading size="xl">
      Are you sure you want to recall the {{ permitType | permitRequestType }} ?
    </app-page-heading>
    <div class="govuk-button-group">
      <button type="button" appPendingButton (click)="recall()" govukWarnButton>
        Yes, recall the {{ permitType | permitRequestType }}
      </button>
    </div>
    <a govukLink routerLink="..">{{
      isVariation ? 'Return to: Permit variation review' : 'Return to: Permit determination'
    }}</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecallComponent implements PendingRequest {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  readonly permitType = this.store.getValue().requestType;
  readonly isVariation = this.permitType === 'PERMIT_VARIATION';

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
    readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  recall(): void {
    this.taskId$
      .pipe(
        first(),
        switchMap((taskId) =>
          this.tasksService.processRequestTaskAction({
            requestTaskActionType: this.store.getRequestTaskActionTypeRecall(),
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'EMPTY_PAYLOAD',
            } as RequestTaskActionPayload,
          }),
        ),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.route }));
  }
}
