import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockState } from '../../testing/mock-state';
import { EmissionPointDetailsGuard } from './emission-point-details.guard';

describe('EmissionPointDetailsGuard', () => {
  let guard: EmissionPointDetailsGuard;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        EmissionPointDetailsGuard,
        { provide: TasksService, useValue: {} },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });
    guard = TestBed.inject(EmissionPointDetailsGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitIssuanceStore);
    store.setState(mockState);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to task list if no data available', async () => {
    await expect(lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 1 })))).resolves.toEqual(
      router.parseUrl('/permit-issuance/1/emission-points/summary'),
    );

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 1,
            emissionPointId: 'nonexisitng',
          }),
        ),
      ),
    ).resolves.toEqual(router.parseUrl('/permit-issuance/1/emission-points/summary'));
  });

  it('should activate if data exist', async () => {
    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({
            taskId: 23,
            emissionPointId: mockPermitApplyPayload.permit.emissionPoints[0].id,
          }),
        ),
      ),
    ).resolves.toBeTruthy();

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 23, emissionPointId: 'notexisting' }))),
    ).resolves.toEqual(router.parseUrl('/permit-issuance/23/emission-points/summary'));
  });
});
