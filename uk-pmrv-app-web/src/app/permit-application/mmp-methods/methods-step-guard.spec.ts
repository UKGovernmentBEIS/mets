import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { mockDigitizedPlanDetails } from '@permit-application/mmp-sub-installations/testing/mock';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockState, mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { ActivatedRouteSnapshotStub, ActivatedRouteStub } from '@testing';

import { DigitizedPlan, SubInstallation, TasksService } from 'pmrv-api';

import { MMPMethodsStepGuard } from './methods-step-guard';

describe('MMPMethodsStepGuard', () => {
  let guard: MMPMethodsStepGuard;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;

  const route = new ActivatedRouteStub({}, {}, { permitTask: 'monitoringMethodologyPlans' });

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        MMPMethodsStepGuard,
        {
          provide: Router,
          useValue: {
            getCurrentNavigation: jest.fn().mockReturnValue(null),
            parseUrl: jest.fn().mockReturnValue(null),
          },
        },
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: {} },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });

    guard = TestBed.inject(MMPMethodsStepGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitIssuanceStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if data model is not complete and page is not summary', () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({
      extras: { state: { changing: false } },
      finalUrl: 'permit-issuance/1/mmp-methods',
    } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: {
              methodTask: { ...mockDigitizedPlanDetails.methodTask, avoidDoubleCountToggle: false },
              subInstallations: [
                ...mockDigitizedPlanDetails.subInstallations,
                {
                  subInstallationNo: '1',
                  subInstallationType: 'ADIPIC_ACID',
                  description: 'description 1',
                  supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c114'],
                } as SubInstallation,
              ],
            } as DigitizedPlan,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [false],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );

    expect(
      firstValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({ taskId: 1 }, null, {
            permitTask: 'monitoringMethodologyPlans',
          }),
        ),
      ),
    ).resolves.toEqual(true);
  });

  it('should go to summary if data model is complete and page is not summary', () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({
      extras: { state: { changing: false } },
      finalUrl: 'permit-issuance/1/mmp-methods',
    } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: {
              methodTask: mockDigitizedPlanDetails.methodTask,
              subInstallations: [
                ...mockDigitizedPlanDetails.subInstallations,
                {
                  subInstallationNo: '1',
                  subInstallationType: 'ADIPIC_ACID',
                  description: 'description 1',
                  supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c114'],
                } as SubInstallation,
              ],
            } as DigitizedPlan,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [false],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );

    expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 1 })))).resolves.toEqual(
      router.parseUrl('/permit-issuance/1/mmp-methods/summary'),
    );
  });

  it('should go to first page if data model is not complete and page is summary', () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({
      extras: { state: { changing: false } },
      finalUrl: 'permit-issuance/1/mmp-methods/summary',
    } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: {
              methodTask: { ...mockDigitizedPlanDetails.methodTask, avoidDoubleCountToggle: false },
              subInstallations: [
                ...mockDigitizedPlanDetails.subInstallations,
                {
                  subInstallationNo: '1',
                  subInstallationType: 'ADIPIC_ACID',
                  description: 'description 1',
                  supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c114'],
                } as SubInstallation,
              ],
            } as DigitizedPlan,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [false],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );

    expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 1 })))).resolves.toEqual(
      router.parseUrl('/permit-issuance/1/mmp-methods'),
    );
  });

  it('should activate if data model is complete and page is summary', () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({
      extras: { state: { changing: false } },
      finalUrl: 'permit-issuance/1/mmp-methods/summary',
    } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: {
              methodTask: mockDigitizedPlanDetails.methodTask,
              subInstallations: [
                ...mockDigitizedPlanDetails.subInstallations,
                {
                  subInstallationNo: '1',
                  subInstallationType: 'ADIPIC_ACID',
                  description: 'description 1',
                  supportingFiles: ['e227ea8a-778b-4208-9545-e108ea66c114'],
                } as SubInstallation,
              ],
            } as DigitizedPlan,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [false],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );

    expect(
      firstValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({ taskId: 1 }, null, {
            permitTask: 'monitoringMethodologyPlans',
          }),
        ),
      ),
    ).resolves.toEqual(true);
  });
});
