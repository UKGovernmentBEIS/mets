import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { CommonTasksState } from '../../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { mockAerApplyPayload, mockState } from '../../testing/mock-aer-apply-action';
import { AmendSummaryGuard } from './amend-summary.guard';

describe('AmendSummaryGuard', () => {
  let guard: AmendSummaryGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1, section: 'FUELS_AND_EQUIPMENT' };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });

    guard = TestBed.inject(AmendSummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect if task status not complete', async () => {
    store.setState(mockState);
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/aer/submit/amend/FUELS_AND_EQUIPMENT'));
  });

  it('should allow if status is complete', async () => {
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestTask: {
          ...mockState.requestTaskItem.requestTask,
          payload: {
            ...mockAerApplyPayload,
            permitType: 'GHGE',
            verificationPerformed: true,
            aerSectionsCompleted: {
              AMEND_FUELS_AND_EQUIPMENT: [true],
            },
          },
        },
      },
    } as CommonTasksState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
