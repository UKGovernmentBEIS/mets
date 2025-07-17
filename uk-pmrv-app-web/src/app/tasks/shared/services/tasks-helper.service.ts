import { Injectable } from '@angular/core';

import { map, Observable } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { RequestMetadata, RequestTaskDTO, RequestTaskItemDTO, TasksService } from 'pmrv-api';

@Injectable()
export abstract class TasksHelperService {
  constructor(
    protected readonly store: CommonTasksStore,
    protected readonly tasksService: TasksService,
    protected readonly businessErrorService: BusinessErrorService,
  ) {}

  get requestTaskItem$(): Observable<RequestTaskItemDTO> {
    return this.store.requestTaskItem$;
  }

  get requestTaskType$(): Observable<RequestTaskDTO['type']> {
    return this.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem?.requestTask?.type));
  }

  get payload$(): Observable<any> {
    return this.store.payload$;
  }

  get requestMetadata$(): Observable<RequestMetadata> {
    return this.store.requestMetadata$;
  }

  get isEditable$(): Observable<boolean> {
    return this.store.isEditable$;
  }

  getPayload(): Observable<any> {
    return this.store.payload$;
  }

  getBaseFileDownloadUrl() {
    const requestTaskId = this.store.requestTaskId;
    return `/tasks/${requestTaskId}/file-download/`;
  }

  get daysRemaining$() {
    return this.store.requestTaskItem$.pipe(map((task) => task?.requestTask?.daysRemaining));
  }
}
