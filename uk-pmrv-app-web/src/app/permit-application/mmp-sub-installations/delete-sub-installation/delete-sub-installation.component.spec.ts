import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitApplicationModule } from '@permit-application/permit-application.module';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockPostBuild, mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { DigitizedPlan, TasksService } from 'pmrv-api';

import { mockDigitizedPlanDetails } from '../testing/mock';
import { DeleteSubInstallationComponent } from './delete-sub-installation.component';

describe('DeleteSubInstallationComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let activatedRoute: ActivatedRoute;
  let component: DeleteSubInstallationComponent;
  let fixture: ComponentFixture<DeleteSubInstallationComponent>;

  const route = new ActivatedRouteStub({ subInstallationNo: '0' }, {}, { permitTask: 'monitoringMethodologyPlans' });
  const tasksService = mockClass(TasksService);

  const digitizedPlan = {
    subInstallations: mockDigitizedPlanDetails.subInstallations,
  } as DigitizedPlan;

  class Page extends BasePage<DeleteSubInstallationComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeleteSubInstallationComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule, PermitApplicationModule],

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
            digitizedPlan,
          },
        },
        {
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(DeleteSubInstallationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the item name', () => {
    expect(page.header.textContent.trim()).toEqual(`Are you sure you want to delete Sub-installation 1?`);
  });

  it('should delete product benchmark and navigate to list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: {
              subInstallations: [],
            },
          },
        },
        {
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [],
        },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../../'], { relativeTo: activatedRoute });
  });

  it('should delete fallback approach and navigate to list', () => {
    mockDigitizedPlanDetails.subInstallations[0].subInstallationType = 'HEAT_BENCHMARK_CL';
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan,
          },
        },
        {
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Fallback_Approach: [false],
        },
      ),
    );
    fixture.detectChanges();

    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: {
              subInstallations: [],
            },
          },
        },
        {
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Fallback_Approach: [],
        },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../../'], { relativeTo: activatedRoute });
  });
});
