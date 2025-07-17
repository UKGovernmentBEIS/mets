import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockStateBuild } from '../../testing/mock-state';
import { MMPInstallationDescriptionSummaryGuard } from './mmp-installation-description-summary-guard';

describe('MMPInstallationDescriptionSummaryGuard', () => {
  let guard: MMPInstallationDescriptionSummaryGuard;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        MMPInstallationDescriptionSummaryGuard,
        { provide: TasksService, useValue: {} },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });

    guard = TestBed.inject(MMPInstallationDescriptionSummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitIssuanceStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should not activate if data model is complete', async () => {
    store.setState(
      mockStateBuild({
        monitoringMethodologyPlans: {
          exist: true,
          plans: ['e227ea8a-778b-4208-9545-e108ea66c114'],
          digitizedPlan: {
            installationDescription: {
              description: 'description',
              flowDiagrams: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            },
          },
        },
      }),
    );

    await expect(
      firstValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({ taskId: 1 }, null, {
            permitTask: 'monitoringMethodologyPlans',
          }),
        ),
      ),
    ).resolves.toEqual(true);
  });

  it('should activate if  data model is wrong', async () => {
    store.setState(
      mockStateBuild({
        monitoringMethodologyPlans: {
          exist: true,
          plans: ['e227ea8a-778b-4208-9545-e108ea66c114'],
          digitizedPlan: {
            installationDescription: {
              description: 'description',
            },
          },
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 1 })))).resolves.toEqual(
      router.parseUrl('/permit-issuance/1/mmp-installation-description'),
    );
  });
});
