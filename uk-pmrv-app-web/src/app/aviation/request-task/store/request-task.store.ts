import { Injectable } from '@angular/core';

import { skipReviewMap } from '@aviation/request-task/containers/skip-review/skip-review.map';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
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
import { AccountClosureStoreDelegate, AerUkEtsStoreDelegate, EmpUkEtsStoreDelegate } from './delegates';
import { AerCorsia3YearOffsettingStoreDelegate } from './delegates/aer-corsia-3year-period-offsetting/aer-corsia-3year-period-offsetting-store-delegate';
import { AerCorsiaAnnualOffsettingStoreDelegate } from './delegates/aer-corsia-annual-offsetting/aer-corsia-annual-offsetting-store-delegate';
import { DoeStoreDelegate } from './delegates/doe';
import { DreStoreDelegate } from './delegates/dre';
import { EmpCorsiaStoreDelegate } from './delegates/emp-corsia';
import { initialState, RequestTaskState } from './request-task.state';

@Injectable({ providedIn: 'root' })
export class RequestTaskStore extends Store<RequestTaskState> implements TypeAwareStore {
  private _empUkEtsDelegate: EmpUkEtsStoreDelegate;
  private _empCorsiaDelegate: EmpCorsiaStoreDelegate;
  private _aerDelegate: AerUkEtsStoreDelegate | AerCorsiaStoreDelegate;
  private _aerVerifyDelegate: AerVerifyUkEtsStoreDelegate | AerVerifyCorsiaStoreDelegate;
  private _accountClosureDelegate: AccountClosureStoreDelegate;
  private readonly defaultTaskActionPayload = {
    payloadType: 'EMPTY_PAYLOAD',
  } as RequestTaskActionPayload;
  private _dreDelegate: DreStoreDelegate;
  private _virDelegate: VirStoreDelegate;
  private _aerCorsiaAnnualOffsetting: AerCorsiaAnnualOffsettingStoreDelegate;
  private _aerCorsia3YearPeriodOffsetting: AerCorsia3YearOffsettingStoreDelegate;
  private _doeDelegate: DoeStoreDelegate;

  constructor(
    public tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
  ) {
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
          : new AerUkEtsStoreDelegate(this, this.businessErrorService);
    }
    return this._aerDelegate;
  }

  get aerVerifyDelegate() {
    if (!this._aerVerifyDelegate) {
      this._aerVerifyDelegate =
        this.getState().requestTaskItem.requestInfo?.type === 'AVIATION_AER_CORSIA'
          ? new AerVerifyCorsiaStoreDelegate(this, this.businessErrorService)
          : new AerVerifyUkEtsStoreDelegate(this, this.businessErrorService);
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

  get aerCorsiaAnnualOffsetting() {
    if (!this._aerCorsiaAnnualOffsetting) {
      this._aerCorsiaAnnualOffsetting = new AerCorsiaAnnualOffsettingStoreDelegate(this, this.businessErrorService);
    }
    return this._aerCorsiaAnnualOffsetting;
  }

  get aerCorsia3YearPeriodOffsetting() {
    if (!this._aerCorsia3YearPeriodOffsetting) {
      this._aerCorsia3YearPeriodOffsetting = new AerCorsia3YearOffsettingStoreDelegate(this, this.businessErrorService);
    }
    return this._aerCorsia3YearPeriodOffsetting;
  }

  get doeDelegate() {
    if (!this._doeDelegate) {
      this._doeDelegate = new DoeStoreDelegate(this, this.businessErrorService);
    }
    return this._doeDelegate;
  }

  initStoreDelegateByRequestType(requestType: RequestInfoDTO['type']) {
    switch (requestType) {
      case 'AVIATION_DRE_UKETS':
        this.dreDelegate.init();
        break;
      case 'AVIATION_DOE_CORSIA':
        this.doeDelegate.init();
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
      case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING':
        this.aerCorsiaAnnualOffsetting.init();
        break;
      case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING':
        this.aerCorsia3YearPeriodOffsetting.init();
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
    this._aerCorsiaAnnualOffsetting = null;
    this._aerCorsia3YearPeriodOffsetting = null;
    this._doeDelegate = null;
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
