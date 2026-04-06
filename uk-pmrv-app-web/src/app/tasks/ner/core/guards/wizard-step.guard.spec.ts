import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { wizardStepGuard } from './wizard-step.guard';

describe('wizardStepGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => wizardStepGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
