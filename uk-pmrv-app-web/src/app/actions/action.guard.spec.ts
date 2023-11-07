import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable, of } from 'rxjs';

import { mockClass } from '@testing';

import { RequestActionsService } from 'pmrv-api';

import { ActionGuard } from './action.guard';
import { CommonActionsStore } from './store/common-actions.store';

describe('ActionGuard', () => {
  let store: CommonActionsStore;
  let guard: ActionGuard;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { actionId: 1 };

  const requestActionsService = mockClass(RequestActionsService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: RequestActionsService, useValue: requestActionsService }],
    });
    guard = TestBed.inject(ActionGuard);
    store = TestBed.inject(CommonActionsStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should set action store', async () => {
    requestActionsService.getRequestActionById.mockReturnValueOnce(of({}));

    store.setState({
      action: undefined,
      storeInitialized: false,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
