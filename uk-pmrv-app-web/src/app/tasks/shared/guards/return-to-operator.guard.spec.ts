import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { tasksReturnToOperatorGuard } from './return-to-operator.guard';

describe('tasksReturnToOperatorGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => tasksReturnToOperatorGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
