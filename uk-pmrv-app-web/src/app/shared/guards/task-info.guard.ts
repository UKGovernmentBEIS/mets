import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { RequestTaskItemDTO, TasksService } from 'pmrv-api';

import { BusinessErrorService } from '../../error/business-error/business-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { taskNotFoundError } from '../errors/request-task-error';
import { IncorporateHeaderStore } from '../incorporate-header/store/incorporate-header.store';

@Injectable({ providedIn: 'root' })
export class TaskInfoGuard {
  private info: RequestTaskItemDTO;

  constructor(
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly incorporateHeaderStore: IncorporateHeaderStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.tasksService.getTaskItemInfoById(Number(route.paramMap.get('taskId'))).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      tap((info) => {
        this.info = info;
        this.incorporateHeaderStore.reset();
        this.incorporateHeaderStore.setState({
          ...this.incorporateHeaderStore.getState(),
          accountId: info.requestInfo?.accountId,
        });
      }),
      map(() => true),
    );
  }

  canDeactivate(): boolean {
    this.incorporateHeaderStore.reset();
    return true;
  }

  resolve(): RequestTaskItemDTO {
    return this.info;
  }
}
