import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, Observable, of } from 'rxjs';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { AerService } from '@tasks/aer/core/aer.service';
import { ComplianceEtsComponent } from '@tasks/aer/verification-submit/compliance-ets/compliance-ets.component';
import { mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { mockVerificationApplyPayload } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { MockType } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { ComplianceEtsService } from './compliance-ets.service';

describe('ComplianceEtsService', () => {
  let complianceEtsService: ComplianceEtsService;
  let router: Router;
  let activatedRoute: ActivatedRoute;
  let store: CommonTasksStore;

  const fixedRoute = 'test';
  const aerService: MockType<AerService> = {
    getPayload: jest.fn().mockImplementation(() => {
      return of(mockVerificationApplyPayload);
    }),
    postVerificationTaskSave: jest.fn().mockImplementation(() => {
      return of(true);
    }),
    get isEditable$(): Observable<boolean> {
      return of(true);
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          {
            path: fixedRoute,
            data: { pageTitle: 'Monitoring plan requirements' },
            component: ComplianceEtsComponent,
            canDeactivate: [PendingRequestGuard],
          },
        ]),
        HttpClientTestingModule,
      ],
      providers: [
        KeycloakService,
        ComplianceEtsService,
        {
          provide: AerService,
          useValue: aerService,
        },
      ],
    });
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockStateBuild());
    complianceEtsService = TestBed.inject(ComplianceEtsService);
  });

  it('should be created', () => {
    expect(complianceEtsService).toBeTruthy();
  });

  it('should call isEditable$', async () => {
    await expect(lastValueFrom(complianceEtsService.isEditable$)).resolves.toEqual(true);
  });

  it('should call submit, navigate to a route when form is untouched', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const complianceSubmitSpy = jest.spyOn(complianceEtsService, 'onSubmit');

    complianceEtsService.onSubmit(fixedRoute, false, {
      monitoringPlanRequirementsMet: true,
    });

    expect(complianceSubmitSpy).toHaveBeenCalledTimes(1);
    expect(aerService.getPayload).toHaveBeenCalledTimes(0);
    expect(aerService.postVerificationTaskSave).toHaveBeenCalledTimes(0);
    expect(navigateSpy).toHaveBeenCalledWith([fixedRoute], { relativeTo: activatedRoute });
  });

  it('should call submit, call aerService and navigate to a route when form is dirty', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const complianceSubmitSpy = jest.spyOn(complianceEtsService, 'onSubmit');

    complianceEtsService.onSubmit(fixedRoute, true, {
      monitoringPlanRequirementsMet: false,
      monitoringPlanRequirementsNotMetReason: 'Test reason',
    });

    expect(complianceSubmitSpy).toHaveBeenCalledTimes(1);
    expect(aerService.getPayload).toHaveBeenCalledTimes(1);
    expect(aerService.postVerificationTaskSave).toHaveBeenCalledTimes(1);
    expect(aerService.postVerificationTaskSave).toHaveBeenCalledWith(
      {
        etsComplianceRules: {
          ...mockVerificationApplyPayload.verificationReport.etsComplianceRules,
          monitoringPlanRequirementsMet: false,
          monitoringPlanRequirementsNotMetReason: 'Test reason',
        },
      },
      false,
      'etsComplianceRules',
    );
    expect(navigateSpy).toHaveBeenCalledWith([fixedRoute], { relativeTo: activatedRoute });
  });
});
