import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanDeactivate } from '@angular/router';

import { combineLatest, first, map } from 'rxjs';

import { AuthStore, selectUserState } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { PermitApplicationTaskGuard } from '@permit-application/permit-application-task.guard';
import {
  initializePermitSectionsCompleted,
  initializeReviewSectionsCompleted,
} from '@permit-application/shared/utils/initialize-sections';
import { taskNotFoundError } from '@shared/errors/request-task-error';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { TasksService } from 'pmrv-api';

import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { PermitVariationStore } from './store/permit-variation.store';

@Injectable({
  providedIn: 'root',
})
export class PermitVariationTaskGuard extends PermitApplicationTaskGuard implements CanActivate, CanDeactivate<any> {
  constructor(
    private readonly store: PermitVariationStore,
    private readonly commonStore: CommonTasksStore,
    private readonly incorporateHeaderStore: IncorporateHeaderStore,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly authStore: AuthStore,
  ) {
    super();
  }

  canActivate(route: ActivatedRouteSnapshot): any {
    return combineLatest([
      this.tasksService.getTaskItemInfoById(Number(route.paramMap.get('taskId'))),
      this.authStore.pipe(selectUserState),
    ]).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      first(),
      map(([requestTaskItem, userState]) => {
        this.commonStore.reset();
        this.commonStore.setState({ ...this.commonStore.getState(), requestTaskItem });

        this.store.reset();
        const state = this.store.getState();
        this.store.setState({
          ...state,
          ...this.buildPermitApplicationState(+route.paramMap.get('taskId'), requestTaskItem, userState),
          isEditable: [
            'PERMIT_VARIATION_SAVE_APPLICATION_REGULATOR_LED',
            'PERMIT_VARIATION_SAVE_APPLICATION',
            'PERMIT_VARIATION_SAVE_APPLICATION_REVIEW',
            'PERMIT_VARIATION_SAVE_APPLICATION_AMEND',
            'PERMIT_VARIATION_SUBMIT_APPLICATION',
            'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT',
          ].some((type) => requestTaskItem.allowedRequestTaskActions.includes(type)),
          permit: {
            ...state.permit,
            ...requestTaskItem.requestTask.payload?.permit,
          },
          permitSectionsCompleted: {
            ...(Object.keys(requestTaskItem.requestTask.payload?.permitSectionsCompleted ?? {})?.length === 0 &&
            ['PERMIT_VARIATION_APPLICATION_SUBMIT', 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT'].some((type) =>
              requestTaskItem.requestTask.type.includes(type),
            )
              ? initializePermitSectionsCompleted(requestTaskItem.requestTask.payload?.permit)
              : requestTaskItem.requestTask.payload?.permitSectionsCompleted),
          },
          reviewSectionsCompleted: {
            ...(Object.keys(requestTaskItem.requestTask.payload?.reviewSectionsCompleted ?? {})?.length === 0 &&
            ['PERMIT_VARIATION_APPLICATION_SUBMIT'].some((type) => requestTaskItem.requestTask.type.includes(type))
              ? initializeReviewSectionsCompleted(requestTaskItem.requestTask.payload?.permit)
              : requestTaskItem.requestTask.payload?.reviewSectionsCompleted),
          },
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
