import { Injectable } from '@angular/core';

import { map, Observable, tap } from 'rxjs';

import { Store } from '@core/store/store';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';

import {
  PaymentCancelRequestTaskActionPayload,
  PaymentMarkAsReceivedRequestTaskActionPayload,
  PaymentsService,
  RequestTaskActionEmptyPayload,
  TasksService,
} from 'pmrv-api';

import { initialState, PaymentState } from './payment.state';

@Injectable({ providedIn: 'root' })
export class PaymentStore extends Store<PaymentState> {
  constructor(
    private readonly tasksService: TasksService,
    private readonly paymentsService: PaymentsService,
    private readonly businessErrorService: BusinessErrorService,
  ) {
    super(initialState);
  }

  setState(state: PaymentState): void {
    super.setState(state);
  }

  get isEditable$(): Observable<boolean> {
    return this.pipe(map((state) => state?.isEditable));
  }

  postMarkAsPaid(state: PaymentState) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PAYMENT_MARK_AS_PAID',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionEmptyPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => this.setState(state)),
      );
  }

  postTrackPaymentAsPaid(data: any) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PAYMENT_MARK_AS_RECEIVED',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PAYMENT_MARK_AS_RECEIVED_PAYLOAD',
          ...data,
        } as PaymentMarkAsReceivedRequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }

  postTrackPaymentCancel(data: any) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PAYMENT_CANCEL',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PAYMENT_CANCEL_PAYLOAD',
          ...data,
        } as PaymentCancelRequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }

  postCreateCardPayment(requestTaskId: number) {
    return this.paymentsService
      .createCardPayment(requestTaskId)
      .pipe(
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }

  postProcessExistingCardPayment(requestTaskId: number) {
    return this.paymentsService
      .processExistingCardPayment(requestTaskId)
      .pipe(
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }
}
