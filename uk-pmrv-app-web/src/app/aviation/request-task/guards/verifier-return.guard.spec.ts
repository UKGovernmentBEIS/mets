import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { firstValueFrom } from 'rxjs';

import { RequestTaskStore } from '../store';
import { VerifierReturnGuard } from './verifier-return.guard';

describe('VerifierReturnGuard', () => {
  let store: RequestTaskStore;
  let guard: VerifierReturnGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1, index: 0 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [VerifierReturnGuard],
    });

    store = TestBed.inject(RequestTaskStore);

    store.setState({
      ...store.getState(),
      requestTaskItem: {
        ...store.getState().requestTaskItem,
        allowedRequestTaskActions: ['AVIATION_AER_UKETS_VERIFICATION_RETURN_TO_OPERATOR'],
      },
    });

    router = TestBed.inject(Router);
    guard = TestBed.inject(VerifierReturnGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if prerequisites are met', async () => {
    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot))).resolves.toEqual(true);
  });

  it('should not activate if action is not allowed', async () => {
    store.setState({
      ...store.getState(),
      requestTaskItem: {
        ...store.getState().requestTaskItem,
        allowedRequestTaskActions: ['AVIATION_AER_UKETS_SAVE_APPLICATION_VERIFICATION'],
      },
    });

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot))).resolves.toEqual(
      router.parseUrl(`/aviation/tasks/${activatedRouteSnapshot.params.taskId}`),
    );
  });
});
