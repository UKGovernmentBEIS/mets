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

import { AvoidDoubleCountComponent } from './avoid-double-count.component';

describe('AvoidDoubleCountComponent', () => {
  let component: AvoidDoubleCountComponent;
  let fixture: ComponentFixture<AvoidDoubleCountComponent>;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;

  const route = new ActivatedRouteStub({}, {}, { permitTask: 'monitoringMethodologyPlans' });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AvoidDoubleCountComponent> {
    get avoidDoubleCount() {
      return this.getInputValue('#avoidDoubleCount');
    }

    set avoidDoubleCount(value: string) {
      this.setInputValue('#avoidDoubleCount', value);
    }

    get continueButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvoidDoubleCountComponent],
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
            ...mockDigitizedPlanDetails,
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

    fixture = TestBed.createComponent(AvoidDoubleCountComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit a valid form', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.avoidDoubleCount = 'avoid double count';

    page.continueButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: {
              ...store.permit.monitoringMethodologyPlans.digitizedPlan,
              methodTask: {
                ...store.permit.monitoringMethodologyPlans.digitizedPlan.methodTask,
                avoidDoubleCount: 'avoid double count',
              },
            } as DigitizedPlan,
          },
        },
        {
          mmpMethods: [false],
        },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../summary'], {
      relativeTo: activatedRoute,
    });
  });
});
