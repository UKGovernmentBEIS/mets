import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { RequestTaskItemDTO, TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../testing';
import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { PaymentGuard } from './payment.guard';
import { initialState } from './store/payment.state';
import { PaymentStore } from './store/payment.store';

describe('PaymentGuard', () => {
  let guard: PaymentGuard;
  let store: PaymentStore;
  let incorporateHeaderStore: IncorporateHeaderStore;

  const tasksService = mockClass(TasksService);
  const mockRequestTaskItem: RequestTaskItemDTO = {
    requestTask: {
      id: 1,
      type: 'PERMIT_ISSUANCE_MAKE_PAYMENT',
    },
    requestInfo: {
      id: '2',
      type: 'PERMIT_ISSUANCE',
      accountId: 3,
      competentAuthority: 'ENGLAND',
    },
    userAssignCapable: true,
    allowedRequestTaskActions: ['PAYMENT_MARK_AS_PAID'],
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(PaymentGuard);
    store = TestBed.inject(PaymentStore);
    incorporateHeaderStore = TestBed.inject(IncorporateHeaderStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow payment and update the stores', async () => {
    tasksService.getTaskItemInfoById.mockReturnValueOnce(of(mockRequestTaskItem));
    const storeResetSpy = jest.spyOn(store, 'reset');
    const incorporateHeaderStoreSpy = jest.spyOn(incorporateHeaderStore, 'reset');

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1' }))),
    ).resolves.toBeTruthy();

    expect(storeResetSpy).toHaveBeenCalledTimes(1);
    expect(store.getState()).toEqual({
      ...initialState,
      requestType: 'PERMIT_ISSUANCE',
      requestTaskId: 1,
      requestId: '2',
      isEditable: true,
      requestTaskItem: mockRequestTaskItem,
      competentAuthority: 'ENGLAND',
    });

    expect(incorporateHeaderStoreSpy).toHaveBeenCalledTimes(1);
    expect(incorporateHeaderStore.getState()).toEqual({ accountId: 3 });
  });
});
