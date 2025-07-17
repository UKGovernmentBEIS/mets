import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { mockDigitizedPlanDetails } from '@permit-application/mmp-sub-installations/testing/mock';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { DigitizedPlan, TasksService } from 'pmrv-api';

import { MethodsSummaryComponent } from './methods-summary.component';

describe('MethodsSummaryComponent', () => {
  let component: MethodsSummaryComponent;
  let fixture: ComponentFixture<MethodsSummaryComponent>;
  let router: Router;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;

  const route = new ActivatedRouteStub({}, {}, { permitTask: 'monitoringMethodologyPlans' });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<MethodsSummaryComponent> {
    get confirmButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MethodsSummaryComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();

    store = TestBed.inject(PermitIssuanceStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: {
              ...mockDigitizedPlanDetails,
              subInstallations: [
                {
                  subInstallationNo: '0',
                  subInstallationType: 'AMMONIA',
                },
                {
                  subInstallationNo: '1',
                  subInstallationType: 'ADIPIC_ACID',
                },
              ],
            } as DigitizedPlan,
          },
        },
        {
          MMP_SUB_INSTALLATION_Fallback_Approach: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [true],
        },
      ),
    );

    fixture = TestBed.createComponent(MethodsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit task status', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.confirmButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        {
          monitoringMethodologyPlans: { ...store.permit.monitoringMethodologyPlans },
        },
        {
          ...mockState.permitSectionsCompleted,
          mmpMethods: [true],
          MMP_SUB_INSTALLATION_Fallback_Approach: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [true],
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../../'], { relativeTo: route });
  });
});
