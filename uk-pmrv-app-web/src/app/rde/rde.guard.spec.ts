import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteSnapshotStub, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { RdeGuard } from './rde.guard';
import { initialState } from './store/rde.state';
import { RdeStore } from './store/rde.store';

describe('RdeGuard', () => {
  let guard: RdeGuard;
  let store: RdeStore;

  const tasksService = mockClass(TasksService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(RdeGuard);
    store = TestBed.inject(RdeStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow Rde', async () => {
    tasksService.getTaskItemInfoById.mockReturnValueOnce(
      of({
        allowedRequestTaskActions: [],
        requestTask: {
          daysRemaining: 10,
        },
        requestInfo: {
          id: '2',
          accountId: 3,
        },
      }),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1' }))),
    ).resolves.toBeTruthy();
  });

  it('should update the store', async () => {
    tasksService.getTaskItemInfoById.mockReturnValueOnce(
      of({
        allowedRequestTaskActions: [],
        requestTask: {
          daysRemaining: 10,
          assigneeFullName: 'John Doe',
          assigneeUserId: '100',
        },
        requestInfo: {
          id: '2',
          accountId: 3,
          type: 'PERMIT_ISSUANCE',
          competentAuthority: 'ENGLAND',
        },
      }),
    );
    const resetSpy = jest.spyOn(store, 'reset');

    await expect(lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 1 })))).resolves.toBeTruthy();
    expect(resetSpy).toHaveBeenCalledTimes(1);

    expect(store.getState()).toEqual({
      ...initialState,
      requestId: '2',
      accountId: 3,
      daysRemaining: 10,
      assignee: {
        assigneeFullName: 'John Doe',
        assigneeUserId: '100',
      },
      isEditable: false,
      requestType: 'PERMIT_ISSUANCE',
      competentAuthority: 'ENGLAND',
    });
  });
});
