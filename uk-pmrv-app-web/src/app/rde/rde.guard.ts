import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { map, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { taskNotFoundError } from '@shared/errors/request-task-error';
import { IncorporateHeaderStore } from '@shared/incorporate-header/store/incorporate-header.store';

import { TasksService } from 'pmrv-api';

import { RdeStore } from './store/rde.store';

@Injectable({ providedIn: 'root' })
export class RdeGuard {
  constructor(
    private readonly store: RdeStore,
    private readonly incorporateHeaderStore: IncorporateHeaderStore,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): any {
    return this.tasksService.getTaskItemInfoById(Number(route.paramMap.get('taskId'))).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      tap((requestTaskItem) => {
        this.store.reset();
        const state = this.store.getState();
        this.store.setState({
          ...state,
          ...requestTaskItem.requestTask.payload,
          requestTaskId: requestTaskItem.requestTask.id,
          assignee: {
            assigneeUserId: requestTaskItem.requestTask.assigneeUserId,
            assigneeFullName: requestTaskItem.requestTask.assigneeFullName,
          },
          userAssignCapable: requestTaskItem.userAssignCapable,
          assignable: requestTaskItem.requestTask.assignable,
          allowedRequestTaskActions: requestTaskItem.allowedRequestTaskActions,
          paymentCompleted: requestTaskItem.requestInfo.paymentCompleted,
          accountId: requestTaskItem.requestInfo.accountId,
          daysRemaining: requestTaskItem.requestTask.daysRemaining,
          requestId: requestTaskItem.requestInfo.id,
          requestType: requestTaskItem.requestInfo.type,
          competentAuthority: requestTaskItem.requestInfo.competentAuthority,
          isEditable: ['RDE_SUBMIT', 'RDE_RESPONSE_SUBMIT', 'RDE_FORCE_DECISION'].some((perm) =>
            requestTaskItem.allowedRequestTaskActions.includes(perm as any),
          ),
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
