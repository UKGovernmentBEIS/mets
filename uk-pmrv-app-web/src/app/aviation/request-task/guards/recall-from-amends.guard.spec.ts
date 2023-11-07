import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { firstValueFrom } from 'rxjs';

import { RequestTaskStore } from '../store';
import { RecallFromAmendsGuard } from './recall-from-amends.guard';

describe('RecallFromAmendsGuard', () => {
  let store: RequestTaskStore;
  let guard: RecallFromAmendsGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 276, index: 0 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [RecallFromAmendsGuard],
    });

    store = TestBed.inject(RequestTaskStore);

    router = TestBed.inject(Router);
    guard = TestBed.inject(RecallFromAmendsGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if prerequisites are met', async () => {
    store.setState({
      ...store.getState(),
      requestTaskItem: {
        ...store.getState().requestTaskItem,
        allowedRequestTaskActions: ['EMP_ISSUANCE_UKETS_RECALL_FROM_AMENDS'],
      },
    });

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot))).resolves.toEqual(true);

    store.setState({
      ...store.getState(),
      requestTaskItem: {
        ...store.getState().requestTaskItem,
        allowedRequestTaskActions: ['EMP_VARIATION_UKETS_RECALL_FROM_AMENDS'],
      },
    });

    store.setState({
      ...store.getState(),
      requestTaskItem: {
        ...store.getState().requestTaskItem,
        allowedRequestTaskActions: ['EMP_VARIATION_CORSIA_RECALL_FROM_AMENDS'],
      },
    });

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot))).resolves.toEqual(true);

    store.setState({
      ...store.getState(),
      requestTaskItem: {
        ...store.getState().requestTaskItem,
        allowedRequestTaskActions: ['EMP_ISSUANCE_CORSIA_RECALL_FROM_AMENDS'],
      },
    });

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot))).resolves.toEqual(true);
  });

  it('should not activate if action is not allowed', async () => {
    store.setState({
      ...store.getState(),
      requestTaskItem: {
        ...store.getState().requestTaskItem,
        allowedRequestTaskActions: ['EMP_ISSUANCE_UKETS_SAVE_APPLICATION_REVIEW'],
      },
    });

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot))).resolves.toEqual(
      router.parseUrl(`/aviation/tasks/${activatedRouteSnapshot.params.taskId}`),
    );
  });
});
