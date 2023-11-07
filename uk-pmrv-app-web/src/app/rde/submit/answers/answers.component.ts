import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, switchMap, takeUntil } from 'rxjs';

import { templateError } from '@shared/errors/template-error';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { BusinessErrorService } from '../../../error/business-error/business-error.service';
import {
  catchBadRequest,
  catchTaskReassignedBadRequest,
  ErrorCodes as BusinessErrorCode,
} from '../../../error/business-errors';
import { catchNotFoundRequest, ErrorCode as NotFoundError } from '../../../error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '../../../shared/errors/request-task-error';
import { UserFullNamePipe } from '../../../shared/pipes/user-full-name.pipe';
import { RdeStore } from '../../store/rde.store';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserFullNamePipe, DestroySubject],
})
export class AnswersComponent implements OnInit {
  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly isTemplateGenerationErrorDisplayed$ = new BehaviorSubject<boolean>(false);
  errorMessage$ = new BehaviorSubject<string>(null);

  constructor(
    readonly store: RdeStore,
    readonly pendingRequest: PendingRequestService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly tasksService: TasksService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.store
      .pipe(
        first(),
        map((state) =>
          state.requestTaskId
            ? // Very bad practice to mutate the current state like this. TODO: set all the state once in the guard
              state.requestType === 'PERMIT_SURRENDER'
              ? `/permit-surrender/${state.requestTaskId}/review`
              : state.requestType === 'PERMIT_VARIATION'
              ? `/permit-variation/${state.requestTaskId}/review`
              : state.requestType === 'PERMIT_ISSUANCE'
              ? `/permit-issuance/${state.requestTaskId}/review`
              : null
            : null,
        ),
        takeUntil(this.destroy$),
      )
      .subscribe();
  }

  confirm(): void {
    combineLatest([this.taskId$, this.store])
      .pipe(
        first(),
        switchMap(([taskId, state]) =>
          this.tasksService.processRequestTaskAction({
            requestTaskActionType: 'RDE_SUBMIT',
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'RDE_SUBMIT_PAYLOAD',
              rdePayload: {
                extensionDate: this.getDate(state.rdePayload.extensionDate),
                deadline: this.getDate(state.rdePayload.deadline),
                ...(state.rdePayload.operators
                  ? {
                      operators: state.rdePayload.operators,
                    }
                  : {}),
                signatory: state.rdePayload.signatory,
              },
            } as RequestTaskActionPayload,
          }),
        ),
        this.pendingRequest.trackRequest(),
        catchNotFoundRequest(NotFoundError.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchBadRequest(
          [
            BusinessErrorCode.NOTIF1000,
            BusinessErrorCode.NOTIF1001,
            BusinessErrorCode.NOTIF1002,
            BusinessErrorCode.NOTIF1003,
          ],
          (res) => {
            return templateError(res, this.isTemplateGenerationErrorDisplayed$, this.errorMessage$);
          },
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe(() => this.router.navigate(['../confirmation'], { relativeTo: this.route }));
  }

  private getDate(dateTime) {
    const dt = new Date(dateTime);

    return dt.getFullYear() + '-' + ('0' + (dt.getMonth() + 1)).slice(-2) + '-' + ('0' + dt.getDate()).slice(-2);
  }
}
