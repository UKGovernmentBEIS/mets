import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, Observable, pluck, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { templateError } from '@shared/errors/template-error';

import { RequestActionUserInfo, RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { BusinessErrorService } from '../../../error/business-error/business-error.service';
import {
  catchBadRequest,
  catchTaskReassignedBadRequest,
  ErrorCodes as BusinessErrorCode,
} from '../../../error/business-errors';
import { catchNotFoundRequest, ErrorCode as NotFoundError } from '../../../error/not-found-error';
import { RfiStore } from '../../store/rfi.store';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnswersComponent implements PendingRequest, OnInit {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  questions$: Observable<string[]>;
  deadline$: Observable<string>;
  signatory$: Observable<string>;
  usersInfo$: Observable<{
    [key: string]: RequestActionUserInfo;
  }>;
  operators$: Observable<string[]>;
  readonly isTemplateGenerationErrorDisplayed$ = new BehaviorSubject<boolean>(false);
  errorMessage$ = new BehaviorSubject<string>(null);
  aviationPrefix = this.router.url.includes('/aviation/') ? '/aviation' : '';

  constructor(
    readonly store: RfiStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly destroy$: DestroySubject,
    private readonly breadcrumbs: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.breadcrumbs.showDashboardBreadcrumb(this.router.url);

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

    this.questions$ = this.store.pipe(pluck('rfiSubmitPayload', 'questions'));
    this.deadline$ = this.store.pipe(pluck('rfiSubmitPayload', 'deadline'));
    this.signatory$ = this.store.pipe(pluck('rfiSubmitPayload', 'signatory'));
    this.usersInfo$ = this.store.pipe(pluck('usersInfo')) as Observable<{ [key: string]: RequestActionUserInfo }>;
    this.operators$ = combineLatest([this.usersInfo$, this.signatory$]).pipe(
      first(),
      map(([usersInfo, signatory]) => Object.keys(usersInfo).filter((userId) => userId !== signatory)),
    );
  }

  onConfirm(): void {
    combineLatest([this.taskId$, this.store])
      .pipe(
        first(),
        switchMap(([taskId, store]) =>
          this.tasksService.processRequestTaskAction({
            requestTaskActionType: 'RFI_SUBMIT',
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'RFI_SUBMIT_PAYLOAD',
              rfiSubmitPayload: {
                questions: store.rfiSubmitPayload.questions,
                files: store.rfiSubmitPayload.files,
                deadline: store.rfiSubmitPayload.deadline,
                ...(store.rfiSubmitPayload.operators
                  ? {
                      operators: store.rfiSubmitPayload.operators,
                    }
                  : {}),
                signatory: store.rfiSubmitPayload.signatory,
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
      .subscribe(() => this.router.navigate(['../submit-confirmation'], { relativeTo: this.route }));
  }

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
