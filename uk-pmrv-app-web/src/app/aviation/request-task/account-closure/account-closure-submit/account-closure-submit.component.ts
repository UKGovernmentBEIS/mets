import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { SharedModule } from '@shared/shared.module';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

@Component({
  selector: 'app-account-closure-reason',
  standalone: true,
  imports: [SharedModule, RouterModule],
  providers: [],
  templateUrl: './account-closure-submit.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountClosureSubmitComponent {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private readonly tasksService = inject(TasksService);
  private businessErrorService = inject(BusinessErrorService);
  private readonly pendingRequest = inject(PendingRequestService);

  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  submitAccountClosure(): void {
    this.taskId$
      .pipe(
        first(),
        switchMap((taskId) =>
          this.tasksService.processRequestTaskAction({
            requestTaskActionType: 'AVIATION_ACCOUNT_CLOSURE_SUBMIT_APPLICATION',
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
      .subscribe(() => this.router.navigate(['../confirmation'], { relativeTo: this.route }));
  }
}
