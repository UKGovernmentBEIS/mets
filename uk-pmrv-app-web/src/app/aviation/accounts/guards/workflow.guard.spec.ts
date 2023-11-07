import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteSnapshotStub } from '../../../../testing';
import { IncorporateHeaderStore } from '../../../shared/incorporate-header/store/incorporate-header.store';
import { WorkflowGuard } from './workflow.guard';

describe('WorkflowGuard', () => {
  let guard: WorkflowGuard;
  let store: IncorporateHeaderStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [WorkflowGuard],
    });
    guard = TestBed.inject(WorkflowGuard);
    store = TestBed.inject(IncorporateHeaderStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should set store', () => {
    const storeResetSpy = jest.spyOn(store, 'reset');
    expect(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: '1' }))).toBeTruthy();

    expect(storeResetSpy).toHaveBeenCalledTimes(1);
    expect(store.getState()).toEqual({ accountId: 1 });
  });
});
