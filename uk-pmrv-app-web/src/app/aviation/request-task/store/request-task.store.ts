import { Injectable } from '@angular/core';

import { skipReviewMap } from '@aviation/request-task/containers/skip-review/skip-review.map';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { VirStoreDelegate } from '@aviation/request-task/store/delegates/vir';
import { Store } from '@core/store';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import produce from 'immer';

import {
  ItemDTO,
  RequestActionInfoDTO,
  RequestInfoDTO,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskItemDTO,
  RequestTaskPayload,
  TasksService,
} from 'pmrv-api';

import { selectType, TypeAwareStore } from '../../type-aware.store';
import { cancelActionMap } from '../containers/cancel/cancel-action.map';
import { CorsiaRequestTypes, isPaymentRequired } from '../util';
import { AccountClosureStoreDelegate, AerStoreDelegate, EmpUkEtsStoreDelegate } from './delegates';
import { AerVerifyStoreDelegate } from './delegates/aer-verify';
import { DreStoreDelegate } from './delegates/dre';
import { EmpCorsiaStoreDelegate } from './delegates/emp-corsia';
import { initialState, RequestTaskState } from './request-task.state';

@Injectable({ providedIn: 'root' })
export class RequestTaskStore extends Store<RequestTaskState> implements TypeAwareStore {
  private _empUkEtsDelegate: EmpUkEtsStoreDelegate;
  private _empCorsiaDelegate: EmpCorsiaStoreDelegate;
  private _aerDelegate: AerStoreDelegate | AerCorsiaStoreDelegate;
  private _aerVerifyDelegate: AerVerifyStoreDelegate | AerVerifyCorsiaStoreDelegate;
  private _accountClosureDelegate: AccountClosureStoreDelegate;
  private readonly defaultTaskActionPayload = {
    payloadType: 'EMPTY_PAYLOAD',
  } as RequestTaskActionPayload;
  private _dreDelegate: DreStoreDelegate;
  private _virDelegate: VirStoreDelegate;

  constructor(public tasksService: TasksService, private readonly businessErrorService: BusinessErrorService) {
    super(initialState);
  }

  get type$() {
    return this.pipe(selectType);
  }

  get requestTaskId(): number | null {
    return this.getState().requestTaskItem?.requestTask?.id ?? null;
  }

  get empUkEtsDelegate() {
    if (!this._empUkEtsDelegate) {
      this._empUkEtsDelegate = new EmpUkEtsStoreDelegate(this, this.businessErrorService);
    }
    return this._empUkEtsDelegate;
  }

  get empCorsiaDelegate() {
    if (!this._empCorsiaDelegate) {
      this._empCorsiaDelegate = new EmpCorsiaStoreDelegate(this, this.businessErrorService);
    }
    return this._empCorsiaDelegate;
  }

  /** This should be used from the emp shared components */
  get empDelegate() {
    return CorsiaRequestTypes.includes(this.getState().requestTaskItem?.requestInfo?.type)
      ? this.empCorsiaDelegate
      : this.empUkEtsDelegate;
  }

  get aerDelegate() {
    if (!this._aerDelegate) {
      this._aerDelegate =
        this.getState().requestTaskItem.requestInfo?.type === 'AVIATION_AER_CORSIA'
          ? new AerCorsiaStoreDelegate(this, this.businessErrorService)
          : new AerStoreDelegate(this, this.businessErrorService);
    }
    return this._aerDelegate;
  }

  get aerVerifyDelegate() {
    if (!this._aerVerifyDelegate) {
      this._aerVerifyDelegate =
        this.getState().requestTaskItem.requestInfo?.type === 'AVIATION_AER_CORSIA'
          ? new AerVerifyCorsiaStoreDelegate(this, this.businessErrorService)
          : new AerVerifyStoreDelegate(this, this.businessErrorService);
    }

    return this._aerVerifyDelegate;
  }

  get accountClosureDelegate() {
    if (!this._accountClosureDelegate) {
      this._accountClosureDelegate = new AccountClosureStoreDelegate(this, this.businessErrorService);
    }
    return this._accountClosureDelegate;
  }

  get isPaymentRequired(): boolean {
    const allowedRequestTaskActions = this.getState().requestTaskItem.requestTask.type;

    return isPaymentRequired.some((requestTaskActionType) => requestTaskActionType === allowedRequestTaskActions);
  }

  get dreDelegate() {
    if (!this._dreDelegate) {
      this._dreDelegate = new DreStoreDelegate(this, this.businessErrorService);
    }
    return this._dreDelegate;
  }

  get virDelegate() {
    if (!this._virDelegate) {
      this._virDelegate = new VirStoreDelegate(this, this.businessErrorService);
    }
    return this._virDelegate;
  }

  initStoreDelegateByRequestType(requestType: RequestInfoDTO['type']) {
    switch (requestType) {
      case 'AVIATION_DRE_UKETS':
        this.dreDelegate.init();
        break;
      case 'AVIATION_AER_UKETS':
        this.aerDelegate.init();
        this.aerVerifyDelegate.init();
        break;
      case 'AVIATION_AER_CORSIA':
        this.aerDelegate.init();
        break;
      case 'AVIATION_ACCOUNT_CLOSURE':
        this.accountClosureDelegate.init();
        break;
      case 'AVIATION_VIR':
        this.virDelegate.init();
        break;
      case 'EMP_ISSUANCE_CORSIA':
      case 'EMP_VARIATION_CORSIA':
        this.empCorsiaDelegate.init();
        break;
      case 'EMP_ISSUANCE_UKETS':
      case 'EMP_VARIATION_UKETS':
        this.empUkEtsDelegate.init();
        break;
      // Non compliance uses code from installation,no delegeta is needed
      case 'AVIATION_NON_COMPLIANCE':
        break;
      default:
        throw new Error('Request type is not supported to init store delegate');
    }
  }

  destroyDelegates() {
    this._dreDelegate = null;
    this._aerDelegate = null;
    this._empUkEtsDelegate = null;
    this._empCorsiaDelegate = null;
    this._aerVerifyDelegate = null;
    this._accountClosureDelegate = null;
    this._virDelegate = null;
  }

  setRequestTaskItem(requestTaskItem: RequestTaskItemDTO) {
    this.setState({ ...this.getState(), requestTaskItem });
  }

  setRelatedTasks(relatedTasks: ItemDTO[]) {
    this.setState({ ...this.getState(), relatedTasks });
  }

  setTimeline(timeline: RequestActionInfoDTO[]) {
    this.setState({ ...this.getState(), timeline });
  }

  setIsTaskReassigned(isTaskReassigned: boolean) {
    this.setState({ ...this.getState(), isTaskReassigned });
  }

  setTaskReassignedTo(taskReassignedTo: string) {
    this.setState({ ...this.getState(), taskReassignedTo });
  }

  setIsEditable(isEditable: boolean) {
    this.setState({ ...this.getState(), isEditable });
  }

  setPayload(payload: RequestTaskPayload) {
    this.setState(
      produce(this.getState(), (state) => {
        state.requestTaskItem.requestTask.payload = payload;
      }),
    );
  }

  cancelCurrentTask() {
    const taskType = this.getState().requestTaskItem.requestTask.type;
    return this.performActionForCurrentTask(cancelActionMap[taskType]);
  }

  skipReview() {
    const taskType = this.getState().requestTaskItem.requestTask.type;
    return this.performActionForCurrentTask(skipReviewMap[taskType]);
  }

  private performActionForCurrentTask(taskType: RequestTaskActionProcessDTO['requestTaskActionType']) {
    const task = this.getState()?.requestTaskItem?.requestTask;
    if (!task) {
      throw new Error('No task is currently selected');
    }
    return this.processRequestTaskAction(taskType, task.id);
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
}
