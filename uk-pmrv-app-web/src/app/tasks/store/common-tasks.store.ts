import { Injectable, Optional } from '@angular/core';
import { Router } from '@angular/router';

import { forkJoin, map, Observable, of, switchMap, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { Store } from '@core/store/store';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { skipReviewMap } from '@tasks/aer/review/skip-review/skip-review.map';

import {
  AerApplicationSkipReviewRequestTaskPayload,
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

import { cancelActionMap } from '../../cancel-task/cancel-action.map';
import { CommonTasksState, initialState } from './common-tasks.state';

const requestTaskEditActionsMap: Partial<
  Record<RequestTaskDTO['type'], RequestTaskActionProcessDTO['requestTaskActionType'][]>
> = {
  PERMIT_NOTIFICATION_APPLICATION_REVIEW: ['PERMIT_NOTIFICATION_SAVE_REVIEW_GROUP_DECISION'],
  PERMIT_NOTIFICATION_APPLICATION_SUBMIT: ['PERMIT_NOTIFICATION_SAVE_APPLICATION'],
  PERMIT_NOTIFICATION_FOLLOW_UP: [
    'PERMIT_NOTIFICATION_FOLLOW_UP_UPLOAD_ATTACHMENT',
    'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_RESPONSE',
    'PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_RESPONSE',
  ],
  PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW: [],
  PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP: ['PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE'],
  PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW: ['PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION'],
  PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT: ['PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND'],
  PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS: ['PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE'],

  PERMIT_VARIATION_APPLICATION_SUBMIT: ['PERMIT_VARIATION_SAVE_APPLICATION'],

  AER_APPLICATION_SUBMIT: ['AER_SAVE_APPLICATION', 'AER_UPLOAD_SECTION_ATTACHMENT'],
  AER_APPLICATION_AMENDS_SUBMIT: [
    'AER_SAVE_APPLICATION_AMEND',
    'AER_UPLOAD_SECTION_ATTACHMENT',
    'AER_SUBMIT_APPLICATION_AMEND',
  ],

  AER_APPLICATION_VERIFICATION_SUBMIT: ['AER_SAVE_APPLICATION_VERIFICATION'],
  AER_AMEND_APPLICATION_VERIFICATION_SUBMIT: ['AER_SAVE_APPLICATION_VERIFICATION'],
  AER_APPLICATION_REVIEW: ['AER_SAVE_REVIEW_GROUP_DECISION', 'AER_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT'],

  DRE_APPLICATION_SUBMIT: ['DRE_SAVE_APPLICATION', 'DRE_APPLY_UPLOAD_ATTACHMENT'],

  DOAL_APPLICATION_SUBMIT: ['DOAL_SAVE_APPLICATION', 'DOAL_UPLOAD_ATTACHMENT'],
  DOAL_AUTHORITY_RESPONSE: ['DOAL_SAVE_AUTHORITY_RESPONSE', 'DOAL_AUTHORITY_RESPONSE_UPLOAD_ATTACHMENT'],

  PERMIT_TRANSFER_A_APPLICATION_SUBMIT: [
    'PERMIT_TRANSFER_A_SAVE_APPLICATION',
    'PERMIT_TRANSFER_A_UPLOAD_SECTION_ATTACHMENT',
  ],

  VIR_APPLICATION_SUBMIT: ['VIR_SAVE_APPLICATION', 'VIR_UPLOAD_ATTACHMENT'],

  VIR_APPLICATION_REVIEW: [
    'VIR_SAVE_REVIEW',
    'VIR_NOTIFY_OPERATOR_FOR_DECISION',
    'RFI_SUBMIT',
    'RFI_UPLOAD_ATTACHMENT',
  ],

  VIR_RESPOND_TO_REGULATOR_COMMENTS: [
    'VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS',
    'VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS',
  ],

  AIR_APPLICATION_SUBMIT: ['AIR_SAVE_APPLICATION', 'AIR_UPLOAD_ATTACHMENT'],

  AIR_APPLICATION_REVIEW: [
    'AIR_SAVE_REVIEW',
    'AIR_NOTIFY_OPERATOR_FOR_DECISION',
    'AIR_REVIEW_UPLOAD_ATTACHMENT',
    'RFI_SUBMIT',
    'RFI_UPLOAD_ATTACHMENT',
  ],

  AIR_RESPOND_TO_REGULATOR_COMMENTS: [
    'AIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS',
    'AIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS',
    'AIR_UPLOAD_ATTACHMENT',
  ],

  NON_COMPLIANCE_APPLICATION_SUBMIT: ['NON_COMPLIANCE_SAVE_APPLICATION'],
  NON_COMPLIANCE_DAILY_PENALTY_NOTICE: ['NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SAVE_APPLICATION'],
  NON_COMPLIANCE_NOTICE_OF_INTENT: ['NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_APPLICATION'],
  NON_COMPLIANCE_CIVIL_PENALTY: ['NON_COMPLIANCE_CIVIL_PENALTY_SAVE_APPLICATION'],
  NON_COMPLIANCE_FINAL_DETERMINATION: ['NON_COMPLIANCE_FINAL_DETERMINATION_SAVE_APPLICATION'],
  WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMIT: ['WITHHOLDING_OF_ALLOWANCES_SAVE_APPLICATION'],
  RETURN_OF_ALLOWANCES_APPLICATION_SUBMIT: ['RETURN_OF_ALLOWANCES_SAVE_APPLICATION'],
  RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_SUBMIT: ['RETURN_OF_ALLOWANCES_RETURNED_SAVE_APPLICATION'],
  WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT: ['WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION'],

  INSTALLATION_AUDIT_APPLICATION_SUBMIT: [
    'INSTALLATION_AUDIT_SAVE_APPLICATION',
    'INSTALLATION_AUDIT_UPLOAD_ATTACHMENT',
  ],
  INSTALLATION_AUDIT_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS: ['INSTALLATION_AUDIT_OPERATOR_RESPOND_SAVE'],

  INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT: ['INSTALLATION_ONSITE_INSPECTION_SAVE_APPLICATION'],
  INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS: [
    'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_SAVE',
  ],

  AVIATION_NON_COMPLIANCE_APPLICATION_SUBMIT: ['NON_COMPLIANCE_SAVE_APPLICATION'],
  AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE: ['NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SAVE_APPLICATION'],
  AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT: ['NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_APPLICATION'],
  AVIATION_NON_COMPLIANCE_CIVIL_PENALTY: ['NON_COMPLIANCE_CIVIL_PENALTY_SAVE_APPLICATION'],
  AVIATION_NON_COMPLIANCE_FINAL_DETERMINATION: ['NON_COMPLIANCE_FINAL_DETERMINATION_SAVE_APPLICATION'],

  BDR_APPLICATION_SUBMIT: ['BDR_SAVE_APPLICATION', 'BDR_UPLOAD_ATTACHMENT'],
  BDR_APPLICATION_VERIFICATION_SUBMIT: ['BDR_SAVE_APPLICATION_VERIFICATION', 'BDR_VERIFICATION_UPLOAD_ATTACHMENT'],
  BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT: [
    'BDR_REGULATOR_REVIEW_SAVE',
    'BDR_SAVE_REGULATOR_REVIEW_GROUP_DECISION',
    'BDR_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT',
  ],
  BDR_APPLICATION_AMENDS_SUBMIT: [
    'BDR_APPLICATION_AMENDS_SAVE',
    'BDR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR',
    'BDR_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER',
    'BDR_UPLOAD_ATTACHMENT',
  ],
  BDR_AMEND_APPLICATION_VERIFICATION_SUBMIT: [
    'BDR_SAVE_APPLICATION_VERIFICATION',
    'BDR_VERIFICATION_UPLOAD_ATTACHMENT',
  ],

  PERMANENT_CESSATION_APPLICATION_SUBMIT: [
    'PERMANENT_CESSATION_CANCEL_APPLICATION',
    'PERMANENT_CESSATION_SAVE_APPLICATION',
    'PERMANENT_CESSATION_UPLOAD',
  ],

  ALR_APPLICATION_SUBMIT: ['ALR_SAVE_APPLICATION', 'ALR_UPLOAD_ATTACHMENT'],
  ALR_APPLICATION_VERIFICATION_SUBMIT: ['ALR_SAVE_APPLICATION_VERIFICATION', 'ALR_VERIFICATION_UPLOAD_ATTACHMENT'],
};

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
    const aerSkipReviewTaskPayload: AerApplicationSkipReviewRequestTaskPayload = {
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
