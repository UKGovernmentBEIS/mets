import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockState, mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, ActivatedRouteStub } from '../../../../testing';
import { MMPProceduresStepGuard } from './mmp-procedures-step-guard';

describe('MMPProceduresStepGuard', () => {
  let guard: MMPProceduresStepGuard;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  const route = new ActivatedRouteStub({ subInstallationNo: '0' }, {}, { permitTask: 'monitoringMethodologyPlans' });

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        MMPProceduresStepGuard,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: {} },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });

    guard = TestBed.inject(MMPProceduresStepGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitIssuanceStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if data model is not complete', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: {
              procedures: {
                CONTROL_ACTIVITIES: {
                  procedureName: 'procedure name',
                  procedureReference: 'procedure reference',
                  dataMaintenanceResponsibleEntity: null,
                  diagramReference: null,
                  itSystemUsed: null,
                  locationOfRecords: null,
                  procedureDescription: null,
                  standardsAppliedList: null,
                },
              },
            },
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          mmpProcedures: [false],
        },
      ),
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

  it('should activate and go to summary if data model is complete', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: {
              procedures: {
                ASSIGNMENT_OF_RESPONSIBILITIES: {
                  procedureName: 'procedure name',
                  procedureReference: 'procedure reference',
                  dataMaintenanceResponsibleEntity: null,
                  diagramReference: null,
                  itSystemUsed: null,
                  locationOfRecords: null,
                  procedureDescription: null,
                  standardsAppliedList: null,
                },
                MONITORING_PLAN_APPROPRIATENESS: {
                  procedureName: 'procedure name',
                  procedureReference: 'procedure reference',
                  dataMaintenanceResponsibleEntity: null,
                  diagramReference: null,
                  itSystemUsed: null,
                  locationOfRecords: null,
                  procedureDescription: null,
                  standardsAppliedList: null,
                },
                DATA_FLOW_ACTIVITIES: {
                  procedureName: 'procedure name',
                  procedureReference: 'procedure reference',
                  dataMaintenanceResponsibleEntity: null,
                  diagramReference: null,
                  itSystemUsed: null,
                  locationOfRecords: null,
                  procedureDescription: null,
                  standardsAppliedList: null,
                },
                CONTROL_ACTIVITIES: {
                  procedureName: 'procedure name',
                  procedureReference: 'procedure reference',
                  dataMaintenanceResponsibleEntity: null,
                  diagramReference: null,
                  itSystemUsed: null,
                  locationOfRecords: null,
                  procedureDescription: null,
                  standardsAppliedList: null,
                },
              },
            },
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          mmpProcedures: [false],
        },
      ),
    );

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 1 })))).resolves.toEqual(
      router.parseUrl('/permit-issuance/1/mmp-procedures/summary'),
    );
  });
});
