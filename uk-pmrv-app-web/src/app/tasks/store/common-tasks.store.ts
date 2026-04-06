import { Injectable, Optional } from '@angular/core';
import { Router } from '@angular/router';

import { forkJoin, map, Observable, of, switchMap, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { Store } from '@core/store/store';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { skipReviewMap } from '@tasks/aer/review/skip-review/skip-review.map';

import {
  AerApplicationSkipReviewRequestTaskActionPayload,
  AerSkipReviewDecision,
  ItemDTO,
  RequestActionInfoDTO,
  RequestActionsService,
  RequestInfoDTO,
  RequestItemsService,
  RequestMetadata,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
  RequestTaskPayload,
  TasksService,
} from 'pmrv-api';

import { cancelActionMap } from '../../cancel-task/cancel-action.util';
import { requestTaskEditActionsMap } from './common-task.util';
import { CommonTasksState, initialState } from './common-tasks.state';

@Injectable({ providedIn: 'root' })
export class CommonTasksStore extends Store<CommonTasksState> {
  private readonly defaultTaskActionPayload = {
    payloadType: 'EMPTY_PAYLOAD',
  } as RequestTaskActionPayload;

  constructor(
    private readonly tasksService: TasksService,
    @Optional() private readonly aviationRequestTaskStore: RequestTaskStore,
    private readonly router: Router,
    private readonly requestItemsService: RequestItemsService,
    private readonly requestActionsService: RequestActionsService, // authService will be used when Permit Application will be migrated
  ) {
    super(initialState);
  }

  setState(state: CommonTasksState): void {
    super.setState(state);
  }

  get state$() {
    return this.asObservable();
  }

  get requestInfo$(): Observable<RequestInfoDTO> {
    return this.state$.pipe(map((state) => state.requestTaskItem.requestInfo));
  }

  get requestTaskItem$(): Observable<RequestTaskItemDTO> {
    return this.state$.pipe(
      map((state) => {
        return state?.requestTaskItem ?? this.aviationRequestTaskStore?.getState()?.requestTaskItem;
      }),
    );
  }

  get requestMetadata$(): Observable<RequestMetadata> {
    return this.state$.pipe(map((state) => state.requestTaskItem?.requestInfo?.requestMetadata));
  }

  get requestTaskType$(): Observable<RequestTaskDTO['type']> {
    return this.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem?.requestTask?.type));
  }

  get relatedTasksItems$(): Observable<ItemDTO[]> {
    return this.state$.pipe(map((state) => state.relatedTasks));
  }

  get timeLineActions$(): Observable<RequestActionInfoDTO[]> {
    return this.state$.pipe(map((state) => state.timeLineActions));
  }

  get storeInitialized$(): Observable<boolean> {
    return this.state$.pipe(map((state) => !!state.storeInitialized));
  }

  get payload$(): Observable<RequestTaskPayload> {
    return this.requestTaskItem$.pipe(map((item) => item?.requestTask.payload));
  }

  get requestTaskId() {
    return this.getValue().requestTaskItem.requestTask.id;
  }

  get requestTaskType() {
    return this.getValue().requestTaskItem?.requestTask?.type;
  }

  get isEditable$() {
    return this.state$.pipe(map((s) => s.isEditable));
  }

  get requestId() {
    return this.getValue().requestTaskItem?.requestInfo?.id;
  }

  resetStoreInitialized(): void {
    this.patchState({ storeInitialized: false });
  }

  requestedTask(taskId: number) {
    this.tasksService
      .getTaskItemInfoById(taskId)
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () => {
          this.router.navigate(['error', '404']);
          return of(null);
        }),
        tap((requestTask) => {
          if (requestTask) {
            this.setState({
              ...initialState,
              requestTaskItem: requestTask,
              isEditable: this.isTaskEditable(requestTask),
            });
          }
        }),
        switchMap((requestTask) => {
          if (requestTask) {
            return this.requestRelatedItemsAndActions$(requestTask);
          } else {
            return of(null);
          }
        }),
        tap(() => this.patchState({ storeInitialized: true })),
        take(1),
      )
      .subscribe();
  }

  requestTaskObservable(taskId: number) {
    return this.tasksService.getTaskItemInfoById(taskId).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () => {
        this.router.navigate(['error', '404']);
        return of(null);
      }),
      tap((requestTask) => {
        if (requestTask) {
          this.setState({
            ...initialState,
            requestTaskItem: requestTask,
            isEditable: this.isTaskEditable(requestTask),
          });
        }
      }),
      switchMap((requestTask) => {
        if (requestTask) {
          return this.requestRelatedItemsAndActions$(requestTask);
        } else {
          return of(null);
        }
      }),
      tap(() => this.patchState({ storeInitialized: true })),
      take(1),
    );
  }

  private requestRelatedItemsAndActions$(requestTaskItem: RequestTaskItemDTO) {
    if (!requestTaskItem) {
      throw Error('No request task item found in Store');
    }
    const { requestInfo, requestTask } = requestTaskItem;
    return forkJoin([
      this.requestItemsService
        .getItemsByRequest(requestInfo.id)
        .pipe(map((response) => response.items.filter((item) => item.taskId !== requestTask.id))),
      this.requestActionsService
        .getRequestActionsByRequestId(requestInfo.id)
        .pipe(map((actions) => this.orderTimelineActions(actions))),
    ]).pipe(
      tap(([relatedTasks, timeLineActions]) => {
        return this.patchState({ relatedTasks, timeLineActions });
      }),
    );
  }

  updateTimelineActions(requestId: string): Observable<Array<RequestActionInfoDTO>> {
    return this.requestActionsService.getRequestActionsByRequestId(requestId).pipe(
      map((actions) => this.orderTimelineActions(actions)),
      tap((timeLineActions) =>
        this.setState({
          ...this.getState(),
          ...{ timeLineActions },
        }),
      ),
    );
  }

  private orderTimelineActions(timelineActions: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return timelineActions
      .slice()
      .sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }

  skipReview(decision: AerSkipReviewDecision): Observable<any> {
    const task = this.getState().requestTaskItem.requestTask;
    const aerSkipReviewTaskPayload: AerApplicationSkipReviewRequestTaskActionPayload = {
      reason: decision.reason,
      type: decision.type,
      payloadType: 'AER_SKIP_REVIEW_PAYLOAD',
    };
    return this.processRequestTaskAction(skipReviewMap?.[task.type], task.id, aerSkipReviewTaskPayload);
  }

  cancelCurrentTask() {
    const taskType = this.getState().requestTaskItem.requestTask.type;
    return this.performActionForCurrentTask(cancelActionMap[taskType]);
  }

  private performActionForCurrentTask(taskType: RequestTaskActionProcessDTO['requestTaskActionType']) {
    const task = this.getState()?.requestTaskItem?.requestTask;
    if (!task) {
      throw new Error('No task is currently selected');
    }
    return this.processRequestTaskAction(taskType, task.id);
  }

  private patchState(state: Partial<CommonTasksState>) {
    this.setState({ ...this.getState(), ...state });
  }

  private processRequestTaskAction(
    taskType: RequestTaskActionProcessDTO['requestTaskActionType'],
    taskId: number,
    requestTaskActionPayload: RequestTaskActionPayload = this.defaultTaskActionPayload,
  ) {
    return this.tasksService.processRequestTaskAction({
      requestTaskActionType: taskType,
      requestTaskId: taskId,
      requestTaskActionPayload: requestTaskActionPayload,
    });
  }

  private isTaskEditable(requestTask: RequestTaskItemDTO): boolean {
    return requestTaskEditActionsMap[requestTask.requestTask?.type]?.some((taskAction) =>
      requestTask.allowedRequestTaskActions.includes(taskAction),
    );
  }
}
