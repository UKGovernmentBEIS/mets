import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../../../../testing';
import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../testing/mock-state';
import { CategoryTierGuard } from './category-tier.guard';

describe('CategoryTierGuard', () => {
  let guard: CategoryTierGuard;
  let store: PermitApplicationStore<PermitApplicationState>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        CategoryTierGuard,
        { provide: TasksService, useValue: mockClass(TasksService) },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });
    store = TestBed.inject(PermitApplicationStore);
    guard = TestBed.inject(CategoryTierGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate source stream category view', async () => {
    store.setState(mockState);

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '23', index: '0' }))),
    ).resolves.toEqual(true);

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '23', index: '1' }))),
    ).resolves.toEqual(true);

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          FALLBACK: {},
        },
      }),
    });
    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '23', index: '0' }))),
    ).resolves.toEqual(true);
  });

  it('should redirect to FALLBACK view', async () => {
    store.setState(mockState);

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '23', index: '2' }))),
    ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-issuance/23/fall-back'));

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          FALLBACK: {},
        },
      }),
    });
    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '23', index: '1' }))),
    ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-issuance/23/fall-back'));
  });
});
