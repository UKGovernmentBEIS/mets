import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, tap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { SharedStore } from '@shared/store/shared.store';
import { resolveRequestType } from '@shared/store-resolver/request-type.resolver';
import { StoreContextResolver } from '@shared/store-resolver/store-context.resolver';

import { PeerReviewDecision, RequestTaskActionPayload, TasksService } from 'pmrv-api';

import {
  resolveRequestTaskActionPayloadType,
  resolveRequestTaskActionType,
  resolveReturnToText,
} from '../peer-review-decision-type-resolver';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnswersComponent implements OnInit, PendingRequest {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  type: PeerReviewDecision['type'];
  notes: string;

  requestType = resolveRequestType(this.location.path());
  private readonly requestTaskType$ = this.storeResolver.getRequestTaskType(this.requestType);
  returnTo$ = this.requestTaskType$.pipe(
    map((requestTaskType) => resolveReturnToText(this.requestType, requestTaskType)),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly location: Location,
    private readonly route: ActivatedRoute,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly sharedStore: SharedStore,
    private storeResolver: StoreContextResolver,
  ) {}

  ngOnInit(): void {
    this.sharedStore
      .pipe(
        map((state) => state.peerReviewDecision),
        first(),
      )
      .subscribe((peerReviewDecision) => {
        this.type = peerReviewDecision?.type;
        this.notes = peerReviewDecision?.notes;
      });
  }

  onSubmit(): void {
    this.taskId$
      .pipe(
        first(),
        withLatestFrom(this.requestTaskType$),
        switchMap(([taskId, taskType]) =>
          this.tasksService.processRequestTaskAction({
            requestTaskActionType: resolveRequestTaskActionType(taskType),
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: resolveRequestTaskActionPayloadType(taskType),
              decision: {
                type: this.type,
                notes: this.notes,
              },
            } as RequestTaskActionPayload,
          }),
        ),
        tap(() =>
          this.sharedStore.setState({
            ...this.sharedStore.getState(),
            peerReviewDecision: undefined,
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

  changeClick(): void {
    this.router.navigate(['../'], {
      relativeTo: this.route,
      state: {
        type: this.type,
        notes: this.notes,
      },
      queryParams: this.route.snapshot.queryParams,
    });
  }
}
