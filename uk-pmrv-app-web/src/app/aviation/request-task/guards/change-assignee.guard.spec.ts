import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { firstValueFrom } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '../store';
import { ChangeAssigneeGuard } from './change-assignee.guard';

describe('ChangeAssigneeGuard', () => {
  let store: RequestTaskStore;
  let guard: ChangeAssigneeGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ChangeAssigneeGuard],
    });

    store = TestBed.inject(RequestTaskStore);
    guard = TestBed.inject(ChangeAssigneeGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should initialize store reassigned state on activate', async () => {
    await firstValueFrom(guard.canActivate());
    const isTaskReassigned = await firstValueFrom(store.pipe(requestTaskQuery.selectIsTaskReassigned));
    const taskReassignedTo = await firstValueFrom(store.pipe(requestTaskQuery.selectTaskReassignedTo));
    expect(isTaskReassigned).toEqual(false);
    expect(taskReassignedTo).toBeNull();
  });

  it('should restore reassigned state when aborting reassignment', async () => {
    store.setTaskReassignedTo('TEST1');
    store.setIsTaskReassigned(true);
    await firstValueFrom(guard.canActivate());
    await firstValueFrom(guard.canDeactivate());
    const isTaskReassigned = await firstValueFrom(store.pipe(requestTaskQuery.selectIsTaskReassigned));
    const taskReassignedTo = await firstValueFrom(store.pipe(requestTaskQuery.selectTaskReassignedTo));
    expect(isTaskReassigned).toEqual(true);
    expect(taskReassignedTo).toEqual('TEST1');
  });
});
