import { TestBed } from '@angular/core/testing';
import { RouterStateSnapshot } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteSnapshotStub, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { RfiGuard } from './rfi.guard';
import { initialState } from './store/rfi.state';
import { RfiStore } from './store/rfi.store';

describe('RfiGuard', () => {
  let guard: RfiGuard;
  let store: RfiStore;

  const tasksService = mockClass(TasksService);
  const routerStateSnapshot = { url: '/rfi/15/questions' } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(RfiGuard);
    store = TestBed.inject(RfiStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow rfi', async () => {
    tasksService.getTaskItemInfoById.mockReturnValueOnce(
      of({
        allowedRequestTaskActions: [],
        requestTask: {
          id: 1,
        },
        requestInfo: {
          id: '2',
          accountId: 3,
        },
      }),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1' }), routerStateSnapshot)),
    ).resolves.toBeTruthy();
  });

  it('should update the store', async () => {
    tasksService.getTaskItemInfoById.mockReturnValueOnce(
      of({
        allowedRequestTaskActions: ['RFI_CANCEL'],
        requestTask: {
          id: 1,
          daysRemaining: 10,
          assigneeFullName: 'John Doe',
          assigneeUserId: '100',
          assignable: true,
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

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 1 }), routerStateSnapshot)),
    ).resolves.toBeTruthy();
    expect(resetSpy).toHaveBeenCalledTimes(1);
    expect(store.getState().requestTaskId).toEqual(1);
    expect(store.getState()).toEqual({
      ...initialState,
      requestTaskId: 1,
      requestId: '2',
      requestType: 'PERMIT_ISSUANCE',
      competentAuthority: 'ENGLAND',
      accountId: 3,
      isEditable: true,
      daysRemaining: 10,
      assignee: {
        assigneeFullName: 'John Doe',
        assigneeUserId: '100',
      },
      assignable: true,
      allowedRequestTaskActions: ['RFI_CANCEL'],
    });
  });

  it('should not update the store', async () => {
    tasksService.getTaskItemInfoById.mockReturnValueOnce(
      of({
        allowedRequestTaskActions: [],
        requestTask: {
          id: 1,
        },
        requestInfo: {
          id: '2',
          accountId: 3,
        },
      }),
    );
    const resetSpy = jest.spyOn(store, 'reset');

    await expect(
      lastValueFrom(
        guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 1 }), {
          url: '/rfi/15/file-download/uuid',
        } as RouterStateSnapshot),
      ),
    ).resolves.toBeTruthy();
    expect(resetSpy).toHaveBeenCalledTimes(0);
    expect(store.getState()).toEqual({
      ...initialState,
    });
  });
});
