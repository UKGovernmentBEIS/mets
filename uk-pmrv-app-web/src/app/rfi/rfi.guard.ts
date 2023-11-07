import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanDeactivate, RouterStateSnapshot } from '@angular/router';

import { mapTo, tap } from 'rxjs';

import { taskNotFoundError } from '@shared/errors/request-task-error';

import { TasksService } from 'pmrv-api';

import { BusinessErrorService } from '../error/business-error/business-error.service';
import { catchNotFoundRequest, ErrorCode } from '../error/not-found-error';
import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { RfiStore } from './store/rfi.store';

@Injectable({ providedIn: 'root' })
export class RfiGuard implements CanActivate, CanDeactivate<any> {
  constructor(
    private readonly store: RfiStore,
    private readonly incorporateHeaderStore: IncorporateHeaderStore,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, routerState: RouterStateSnapshot): any {
    return this.tasksService.getTaskItemInfoById(Number(route.paramMap.get('taskId'))).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      tap((requestTaskItem) => {
        // do not reset the state when a download file tab opens, as rfi state is not saved in the backend
        if (!routerState.url.includes('file-download')) {
          this.store.reset();
          const state = this.store.getState();
          this.store.setState({
            ...state,
            requestTaskId: requestTaskItem.requestTask.id,
            assignee: {
              assigneeUserId: requestTaskItem.requestTask.assigneeUserId,
              assigneeFullName: requestTaskItem.requestTask.assigneeFullName,
            },
            paymentCompleted: requestTaskItem.requestInfo.paymentCompleted,
            accountId: requestTaskItem.requestInfo.accountId,
            ...requestTaskItem.requestTask.payload,
            isEditable: ['RFI_SUBMIT', 'RFI_RESPONSE_SUBMIT', 'RFI_CANCEL'].some((perm) =>
              requestTaskItem.allowedRequestTaskActions.includes(perm as any),
            ),
            assignable: requestTaskItem.requestTask.assignable,
            daysRemaining: requestTaskItem.requestTask.daysRemaining,
            requestId: requestTaskItem.requestInfo.id,
            requestTaskType: requestTaskItem.requestTask.type,
            requestType: requestTaskItem.requestInfo.type,
            competentAuthority: requestTaskItem.requestInfo.competentAuthority,
            allowedRequestTaskActions: requestTaskItem.allowedRequestTaskActions,
          });
        }

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
