import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../testing/mock-state';
import { ConnectionDetailsGuard } from './connection-details.guard';

describe('ConnectionDetailsGuard', () => {
  let guard: ConnectionDetailsGuard;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        ConnectionDetailsGuard,
        { provide: TasksService, useValue: {} },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });

    guard = TestBed.inject(ConnectionDetailsGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitIssuanceStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should not activate if no data available', async () => {
    store.setState(mockState);

    await expect(lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 1 })))).resolves.toEqual(
      true,
    );

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({ taskId: 1 }, null, {
            permitTask: 'monitoringMethodologyPlans',
          }),
        ),
      ),
    ).resolves.toEqual(true);
  });

  it('should activate if connections > 10', async () => {
    store.setState(
      mockStateBuild({
        monitoringMethodologyPlans: {
          exist: true,
          plans: ['e227ea8a-778b-4208-9545-e108ea66c114'],
          digitizedPlan: {
            installationDescription: {
              description: 'description',
              flowDiagrams: ['e227ea8a-778b-4208-9545-e108ea66c113'],
              connections: [
                {
                  entityType: 'ETS_INSTALLATION',
                  phoneNumber: '1234567890',
                  connectionNo: '0',
                  emailAddress: '1@1',
                  flowDirection: 'IMPORT',
                  connectionType: 'MEASURABLE_HEAT',
                  installationId: '123',
                  contactPersonName: '1',
                  installationOrEntityName: 'name1',
                },
                {
                  entityType: 'ETS_INSTALLATION',
                  phoneNumber: '1234567890',
                  connectionNo: '1',
                  emailAddress: '1@1',
                  flowDirection: 'IMPORT',
                  connectionType: 'MEASURABLE_HEAT',
                  installationId: '123',
                  contactPersonName: '1',
                  installationOrEntityName: 'name1',
                },
                {
                  entityType: 'ETS_INSTALLATION',
                  phoneNumber: '1234567890',
                  connectionNo: '2',
                  emailAddress: '1@1',
                  flowDirection: 'IMPORT',
                  connectionType: 'MEASURABLE_HEAT',
                  installationId: '123',
                  contactPersonName: '1',
                  installationOrEntityName: 'name1',
                },
                {
                  entityType: 'ETS_INSTALLATION',
                  phoneNumber: '1234567890',
                  connectionNo: '3',
                  emailAddress: '1@1',
                  flowDirection: 'IMPORT',
                  connectionType: 'MEASURABLE_HEAT',
                  installationId: '123',
                  contactPersonName: '1',
                  installationOrEntityName: 'name1',
                },
                {
                  entityType: 'ETS_INSTALLATION',
                  phoneNumber: '1234567890',
                  connectionNo: '4',
                  emailAddress: '1@1',
                  flowDirection: 'IMPORT',
                  connectionType: 'MEASURABLE_HEAT',
                  installationId: '123',
                  contactPersonName: '1',
                  installationOrEntityName: 'name1',
                },
                {
                  entityType: 'ETS_INSTALLATION',
                  phoneNumber: '1234567890',
                  connectionNo: '5',
                  emailAddress: '1@1',
                  flowDirection: 'IMPORT',
                  connectionType: 'MEASURABLE_HEAT',
                  installationId: '123',
                  contactPersonName: '1',
                  installationOrEntityName: 'name1',
                },
                {
                  entityType: 'ETS_INSTALLATION',
                  phoneNumber: '1234567890',
                  connectionNo: '6',
                  emailAddress: '1@1',
                  flowDirection: 'IMPORT',
                  connectionType: 'MEASURABLE_HEAT',
                  installationId: '123',
                  contactPersonName: '1',
                  installationOrEntityName: 'name1',
                },
                {
                  entityType: 'ETS_INSTALLATION',
                  phoneNumber: '1234567890',
                  connectionNo: '7',
                  emailAddress: '1@1',
                  flowDirection: 'IMPORT',
                  connectionType: 'MEASURABLE_HEAT',
                  installationId: '123',
                  contactPersonName: '1',
                  installationOrEntityName: 'name1',
                },
                {
                  entityType: 'ETS_INSTALLATION',
                  phoneNumber: '1234567890',
                  connectionNo: '8',
                  emailAddress: '1@1',
                  flowDirection: 'IMPORT',
                  connectionType: 'MEASURABLE_HEAT',
                  installationId: '123',
                  contactPersonName: '1',
                  installationOrEntityName: 'name1',
                },
                {
                  entityType: 'ETS_INSTALLATION',
                  phoneNumber: '1234567890',
                  connectionNo: '9',
                  emailAddress: '1@1',
                  flowDirection: 'IMPORT',
                  connectionType: 'MEASURABLE_HEAT',
                  installationId: '123',
                  contactPersonName: '1',
                  installationOrEntityName: 'name1',
                },
              ],
            },
          },
        },
      }),
    );

    await expect(lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 1 })))).resolves.toEqual(
      router.parseUrl('/permit-issuance/1/mmp-installation-description/summary'),
    );

    await expect(
      lastValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({ taskId: 1 }, null, {
            permitTask: 'monitoringMethodologyPlans',
          }),
        ),
      ),
    ).resolves.toEqual(router.parseUrl('/permit-issuance/1/mmp-installation-description/summary'));
  });
});
