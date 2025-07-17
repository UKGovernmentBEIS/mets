import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { combineLatest, first, map } from 'rxjs';

import { selectFeatures } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { AuthStore, selectUserState } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { PermitApplicationTaskGuard } from '@permit-application/permit-application-task.guard';
import { taskNotFoundError } from '@shared/errors/request-task-error';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { TasksService } from 'pmrv-api';

import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { PermitIssuanceStore } from './store/permit-issuance.store';

@Injectable({ providedIn: 'root' })
export class PermitIssuanceTaskGuard extends PermitApplicationTaskGuard {
  constructor(
    private readonly store: PermitIssuanceStore,
    private readonly commonStore: CommonTasksStore,
    private readonly incorporateHeaderStore: IncorporateHeaderStore,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly authStore: AuthStore,
    private readonly configStore: ConfigStore,
  ) {
    super();
  }

  canActivate(route: ActivatedRouteSnapshot): any {
    return combineLatest([
      this.tasksService.getTaskItemInfoById(Number(route.paramMap.get('taskId'))),
      this.authStore.pipe(selectUserState),
      this.configStore.pipe(selectFeatures),
    ]).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      first(),
      map(([requestTaskItem, userState, features]) => {
        this.commonStore.setState({ ...this.commonStore.getState(), requestTaskItem });

        this.store.reset();
        const state = this.store.getState();

        this.store.setState({
          ...state,
          ...this.buildPermitApplicationState(+route.paramMap.get('taskId'), requestTaskItem, userState),
          isEditable: [
            'PERMIT_ISSUANCE_SAVE_APPLICATION',
            'PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW',
            'PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND',
          ].some((type) => requestTaskItem.allowedRequestTaskActions.includes(type)),
          permit: {
            ...state.permit,
            ...requestTaskItem.requestTask.payload?.permit,
          },
          permitSectionsCompleted: { ...requestTaskItem.requestTask.payload?.permitSectionsCompleted },
          reviewSectionsCompleted: { ...requestTaskItem.requestTask.payload?.reviewSectionsCompleted },

          features: features,
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
