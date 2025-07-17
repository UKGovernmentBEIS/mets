import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { map, tap } from 'rxjs';

import { taskNotFoundError } from '@shared/errors/request-task-error';

import { RequestTaskItemDTO, TasksService } from 'pmrv-api';

import { BusinessErrorService } from '../error/business-error/business-error.service';
import { catchNotFoundRequest, ErrorCode } from '../error/not-found-error';
import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { PermitRevocationStore } from './store/permit-revocation-store';

@Injectable({
  providedIn: 'root',
})
export class PermitRevocationTaskGuard {
  constructor(
    private store: PermitRevocationStore,
    private readonly incorporateHeaderStore: IncorporateHeaderStore,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot) {
    return this.tasksService.getTaskItemInfoById(Number(route.paramMap.get('taskId'))).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      tap((requestTaskItem: RequestTaskItemDTO) => {
        this.store.reset();
        this.store.setState({
          ...this.store.getState(),
          requestTaskId: Number(route.paramMap.get('taskId')),
          requestTaskType: requestTaskItem.requestTask.type,
          daysRemaining: requestTaskItem.requestTask.daysRemaining,
          assignee: {
            assigneeUserId: requestTaskItem.requestTask.assigneeUserId,
            assigneeFullName: requestTaskItem.requestTask.assigneeFullName,
          },
          isEditable: [
            'PERMIT_REVOCATION_SAVE_APPLICATION',
            'PERMIT_REVOCATION_WITHDRAW_APPLICATION',
            'PERMIT_REVOCATION_SAVE_CESSATION',
          ].some((type: any) => requestTaskItem.allowedRequestTaskActions.includes(type)),
          assignable: requestTaskItem.requestTask.assignable,
          isRequestTask: true,

          ...requestTaskItem.requestTask.payload,

          allowedRequestTaskActions: requestTaskItem.allowedRequestTaskActions,
          userAssignCapable: requestTaskItem.userAssignCapable,

          requestId: requestTaskItem.requestInfo.id,
          requestType: requestTaskItem.requestInfo.type,
          competentAuthority: requestTaskItem.requestInfo.competentAuthority,
          accountId: requestTaskItem.requestInfo.accountId,
          paymentCompleted: requestTaskItem.requestInfo.paymentCompleted,
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
