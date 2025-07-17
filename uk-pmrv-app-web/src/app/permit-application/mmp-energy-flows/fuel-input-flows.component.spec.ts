import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { IncludeAnswerDetailsComponent } from '@permit-application/mmp-sub-installations/shared/include-answer-details.component';
import { PermitApplicationModule } from '@permit-application/permit-application.module';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { FuelInputFlowsComponent } from './fuel-input-flows.component';
import { energyFlowsDigitizedPlan } from './testing/energy-flows-testing.mock';

describe('FuelInputFlowsComponent', () => {
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: FuelInputFlowsComponent;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let fixture: ComponentFixture<FuelInputFlowsComponent>;

  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const route = new ActivatedRouteStub({ subInstallationNo: '0' }, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<FuelInputFlowsComponent> {
    get fuelInputValue() {
      return this.getInputValue('select[name="fuelInputDataSources.0.fuelInput"]');
    }
    set fuelInputValue(value: string) {
      this.setInputValue('[name="fuelInputDataSources.0.fuelInput"]', value);
    }

    get energyContentValue() {
      return this.getInputValue('select[name="fuelInputDataSources.0.energyContent"]');
    }
    set energyContentValue(value: string) {
      this.setInputValue('[name="fuelInputDataSources.0.energyContent"]', value);
    }

    get methodologyAppliedDescriptionValue() {
      return this.getInputValue('#methodologyAppliedDescription');
    }
    set methodologyAppliedDescriptionValue(value: string) {
      this.setInputValue('#methodologyAppliedDescription', value);
    }

    set followed(value: boolean) {
      this.setInputValue('#hierarchicalOrder.followed-option0', value);
    }

    set fileValue(value: File[]) {
      this.setInputValue('input[type="file"]', value);
    }

    get filesText() {
      return this.queryAll<HTMLSpanElement>('.moj-multi-file-upload__filename');
    }

    get continueButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
  }

  const tasksService = mockClass(TasksService);
  const createComponent = () => {
    fixture = TestBed.createComponent(FuelInputFlowsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    activatedRoute = TestBed.inject(ActivatedRoute);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SharedModule,
        SharedPermitModule,
        PermitApplicationModule,
        FuelInputFlowsComponent,
        IncludeAnswerDetailsComponent,
      ],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();
  });

  describe('for new energy flows', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(
        mockStateBuild(
          {
            monitoringMethodologyPlans: {
              exist: true,
              plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
              digitizedPlan: {},
            },
          },
          {
            monitoringMethodologyPlans: [true],
            mmpEnergyFlows: [false],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form', () => {
      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      attachmentService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: 'e227ea8a-778b-4208-9545-e108ea66c116' } })),
      );
      fixture.detectChanges();
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.fuelInputValue = 'METHOD_MONITORING_PLAN';
      page.energyContentValue = 'CALCULATION_METHOD_MONITORING_PLAN';
      page.methodologyAppliedDescriptionValue = 'description';
      page.followed = true;
      page.fileValue = [new File(['file'], 'file.txt')];

      page.continueButton.click();
      fixture.detectChanges();
      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringMethodologyPlans: {
              exist: true,
              plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
              digitizedPlan: {
                energyFlows: {
                  fuelInputFlows: energyFlowsDigitizedPlan.energyFlows.fuelInputFlows,
                },
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            monitoringMethodologyPlans: [true],
            mmpEnergyFlows: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['./measurable-heat-flows'], {
        relativeTo: activatedRoute,
      });
    });
  });
});
