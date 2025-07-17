import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { mockDigitizedPlanDetails } from '@permit-application/mmp-sub-installations/testing/mock';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockPostBuild, mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { DigitizedPlan, SubInstallation, TasksService } from 'pmrv-api';

import { MethodsToAssignPartsComponent } from './methods-to-assign-parts.component';

describe('MethodsToAssignPartsComponent', () => {
  let component: MethodsToAssignPartsComponent;
  let fixture: ComponentFixture<MethodsToAssignPartsComponent>;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;

  const route = new ActivatedRouteStub({}, {}, { permitTask: 'monitoringMethodologyPlans' });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<MethodsToAssignPartsComponent> {
    get assignParts() {
      return this.getInputValue('#assignParts');
    }

    set assignParts(value: string) {
      this.setInputValue('#assignParts', value);
    }

    get continueButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary')?.querySelectorAll('a') ?? []).map((item) =>
        item.textContent.trim(),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MethodsToAssignPartsComponent],
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
      mockStateBuild({
        monitoringMethodologyPlans: {
          exist: true,
          plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
          digitizedPlan: {
            ...mockDigitizedPlanDetails.methodTask,
            subInstallations: [
              ...mockDigitizedPlanDetails.subInstallations,
              {
                subInstallationNo: '1',
                subInstallationType: 'ADIPIC_ACID',
              } as SubInstallation,
            ],
          } as DigitizedPlan,
        },
      }),
    );

    fixture = TestBed.createComponent(MethodsToAssignPartsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display error and then submit a valid form', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.continueButton.click();
    fixture.detectChanges();

    expect(page.errorSummaryLinks).toEqual([
      'Enter a description of the methods used to assign parts of installations and their emissions to sub-installations',
    ]);

    page.assignParts = 'new assign part';

    page.continueButton.click();
    fixture.detectChanges();

    expect(page.errorSummaryLinks).toEqual([]);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild({
        monitoringMethodologyPlans: {
          exist: true,
          plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
          digitizedPlan: {
            ...store.permit.monitoringMethodologyPlans.digitizedPlan,
            methodTask: {
              ...store.permit.monitoringMethodologyPlans.digitizedPlan.methodTask,
              assignParts: 'new assign part',
            },
          } as DigitizedPlan,
        },
      }),
    );

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../avoid-double-count'], {
      relativeTo: activatedRoute,
    });
  });
});
