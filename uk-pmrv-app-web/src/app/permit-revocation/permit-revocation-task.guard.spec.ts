import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteSnapshotStub, MockType } from '@testing';

import { TasksService } from 'pmrv-api';

import { PermitRevocationTaskGuard } from './permit-revocation-task.guard';
import { PermitRevocationStore } from './store/permit-revocation-store';
import { mockTaskState } from './testing/mock-state';

describe('PermitRevocationTaskGuard', () => {
  let guard: PermitRevocationTaskGuard;
  let store: PermitRevocationStore;

  const tasksService: MockType<TasksService> = {
    getTaskItemInfoById: jest.fn().mockReturnValue(of(null)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(PermitRevocationTaskGuard);
    store = TestBed.inject(PermitRevocationStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow permit revocation payloads', async () => {
    tasksService.getTaskItemInfoById.mockReturnValueOnce(
      of({
        requestTask: {
          id: mockTaskState.requestTaskId,
          type: 'PERMIT_REVOCATION_APPLICATION_SUBMIT',
          payload: {},
          assigneeFullName: mockTaskState.assignee.assigneeFullName,
          assigneeUserId: mockTaskState.assignee.assigneeUserId,
        },
        allowedRequestTaskActions: mockTaskState.allowedRequestTaskActions,
        requestInfo: { competentAuthority: mockTaskState.competentAuthority },
      }),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: `${mockTaskState.requestTaskId}` }))),
    ).resolves.toBeTruthy();

    expect(tasksService.getTaskItemInfoById).toHaveBeenCalledTimes(1);
    expect(tasksService.getTaskItemInfoById).toHaveBeenLastCalledWith(mockTaskState.requestTaskId);
  });

  it('should update store', async () => {
    tasksService.getTaskItemInfoById.mockReturnValueOnce(
      of({
        requestTask: {
          id: mockTaskState.requestTaskId,
          type: 'PERMIT_REVOCATION_APPLICATION_SUBMIT',
          assignable: mockTaskState.assignable,
          assigneeFullName: mockTaskState.assignee.assigneeFullName,
          assigneeUserId: mockTaskState.assignee.assigneeUserId,
        },
        allowedRequestTaskActions: mockTaskState.allowedRequestTaskActions,
        userAssignCapable: true,
        requestInfo: {
          id: 'AEMR2-1',
          type: 'PERMIT_REVOCATION',
          competentAuthority: mockTaskState.competentAuthority,
          accountId: mockTaskState.accountId,
        },
      }),
    );

    const resetSpy = jest.spyOn(store, 'reset');

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: `${mockTaskState.requestTaskId}` }))),
    ).resolves.toBeTruthy();

    expect(tasksService.getTaskItemInfoById).toHaveBeenCalledTimes(1);
    expect(tasksService.getTaskItemInfoById).toHaveBeenLastCalledWith(mockTaskState.requestTaskId);

    expect(resetSpy).toHaveBeenCalledTimes(1);
    expect(store.getState()).toEqual(mockTaskState);
  });
});
