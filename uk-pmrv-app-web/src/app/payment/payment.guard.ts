import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { map, tap } from 'rxjs';

import { taskNotFoundError } from '@shared/errors/request-task-error';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { TasksService } from 'pmrv-api';

import { BusinessErrorService } from '../error/business-error/business-error.service';
import { catchNotFoundRequest, ErrorCode } from '../error/not-found-error';
import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { PaymentStore } from './store/payment.store';

@Injectable({ providedIn: 'root' })
export class PaymentGuard {
  constructor(
    private readonly store: PaymentStore,
    private readonly incorporateHeaderStore: IncorporateHeaderStore,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly commonStore: CommonTasksStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): any {
    return this.tasksService.getTaskItemInfoById(Number(route.paramMap.get('taskId'))).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      tap((requestTaskItem) => {
        this.commonStore.setState({ ...this.commonStore.getState(), requestTaskItem });
        this.store.reset();
        this.store.setState({
          ...this.store.getState(),
          requestId: requestTaskItem.requestInfo.id,
          requestType: requestTaskItem.requestInfo.type,
          competentAuthority: requestTaskItem.requestInfo.competentAuthority,
          requestTaskId: requestTaskItem.requestTask.id,
          requestTaskItem: {
            requestTask: requestTaskItem.requestTask,
            requestInfo: requestTaskItem.requestInfo,
            userAssignCapable: requestTaskItem.userAssignCapable,
            allowedRequestTaskActions: requestTaskItem.allowedRequestTaskActions,
          },
          isEditable:
            requestTaskItem.allowedRequestTaskActions.includes('PAYMENT_MARK_AS_PAID') ||
            requestTaskItem.allowedRequestTaskActions.includes('PAYMENT_MARK_AS_RECEIVED'),
          paymentDetails: requestTaskItem.requestTask.payload,
        });

        this.incorporateHeaderStore.reset();
        this.incorporateHeaderStore.setState({
          ...this.incorporateHeaderStore.getState(),
          accountId: requestTaskItem.requestInfo.accountId,
        });
      }),
      map(() => true),
    );
  }

  canDeactivate(): boolean {
    this.incorporateHeaderStore.reset();
    return true;
  }
}
