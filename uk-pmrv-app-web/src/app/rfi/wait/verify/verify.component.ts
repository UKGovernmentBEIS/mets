import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { BusinessErrorService } from '../../../error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '../../../error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '../../../error/not-found-error';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { requestTaskReassignedError, taskNotFoundError } from '../../../shared/errors/request-task-error';

@Component({
  selector: 'app-verify-cancel',
  template: `
    <app-page-heading size="xl">
      Are you sure you want to cancel this official request for information?
    </app-page-heading>
    <div class="govuk-button-group">
      <button type="button" appPendingButton (click)="cancel()" govukWarnButton>Yes, cancel request</button>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifyComponent implements OnInit, OnDestroy {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit() {
    this.backLinkService.show();
  }

  cancel(): void {
    this.taskId$
      .pipe(
        first(),
        switchMap((taskId) =>
          this.tasksService.processRequestTaskAction({
            requestTaskActionType: 'RFI_CANCEL',
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'EMPTY_PAYLOAD',
            } as RequestTaskActionPayload,
          }),
        ),
        this.pendingRequest.trackRequest(),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe(() => this.router.navigate(['../cancel-confirmation'], { relativeTo: this.route }));
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }
}
