import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanDeactivate } from '@angular/router';

import { mapTo, tap } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { TasksService } from 'pmrv-api';

import { BusinessErrorService } from '../error/business-error/business-error.service';
import { catchNotFoundRequest, ErrorCode } from '../error/not-found-error';
import { taskNotFoundError } from '../shared/errors/request-task-error';
import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { PermitSurrenderStore } from './store/permit-surrender.store';

@Injectable({
  providedIn: 'root',
})
export class PermitSurrenderTaskGuard implements CanActivate, CanDeactivate<any> {
  constructor(
    private readonly store: PermitSurrenderStore,
    private readonly commonStore: CommonTasksStore,
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
        this.commonStore.reset();
        this.commonStore.setState({ ...this.commonStore.getState(), requestTaskItem });
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
            'PERMIT_SURRENDER_SAVE_APPLICATION',
            'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION',
            'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION',
            'PERMIT_SURRENDER_SAVE_CESSATION',
          ].some((type) => requestTaskItem.allowedRequestTaskActions.includes(type)),
          assignable: requestTaskItem.requestTask.assignable,

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
      mapTo(true),
    );
  }

  canDeactivate(): boolean {
    this.incorporateHeaderStore.reset();
    return true;
  }
}
