import { Injectable } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, mapTo, mergeMap, Observable, take } from 'rxjs';

import { Store } from '@core/store/store';

import { RequestTaskActionProcessDTO, TasksService } from 'pmrv-api';

import { BusinessErrorService } from '../../error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '../../shared/errors/request-task-error';
import { TaskItem } from '../../shared/task-list/task-list.interface';
import { mapToAmend } from '../pipes/action-pipes';
import { initialState, InstallationAccountApplicationState, Section } from './installation-account-application.state';

@Injectable({ providedIn: 'root' })
export class InstallationAccountApplicationStore extends Store<InstallationAccountApplicationState> {
  constructor(
    private readonly router: Router,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
  ) {
    super(initialState);
  }

  updateTask<T extends Section>(type: T['type'], value: Partial<T['value']>, status?: TaskItem<T>['status']): void {
    const currentState = this.getState();
    const targetTask = this.findTask(currentState.tasks, type);

    this.setState({
      ...currentState,
      tasks: currentState.tasks.map((task) =>
        task === targetTask
          ? {
              ...targetTask,
              status: status ?? targetTask.status,
              value: { ...targetTask.value, ...value },
            }
          : task,
      ),
    });
  }

  getTask<T extends Section>(type: T['type']): Observable<T> {
    return this.select('tasks').pipe(map((tasks: Section[]) => this.findTask(tasks, type)));
  }

  nextStep(targetRoute: string, route: ActivatedRoute, groups?: UntypedFormGroup[]): void {
    const invalid = groups?.some((group) => group.invalid);

    if (this.getState().isSummarized && !invalid) {
      this.router.navigate(['/installation-account/application/summary']);
    } else if (this.getState().isReviewed && !invalid) {
      this.router.navigate(['/installation-account', route.snapshot.paramMap.get('taskId')]);
    } else {
      this.router.navigate([targetRoute], { relativeTo: route });
    }
  }

  getApplicationPayload(): Observable<RequestTaskActionProcessDTO> {
    return combineLatest([this.select('tasks'), this.select('taskId')]).pipe(take(1), mapToAmend());
  }

  amend(): Observable<void> {
    return this.getApplicationPayload().pipe(
      mergeMap((payload) => this.tasksService.processRequestTaskAction(payload)),
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
      mapTo(null),
    );
  }

  private findTask<T extends Section>(tasks: Section[], type: T['type']): T {
    return tasks.find((task): task is T => task.type === type);
  }
}
