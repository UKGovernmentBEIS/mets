import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitApplicationModule } from '@permit-application/permit-application.module';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import {
  mockDigitizedPlanDirectlyAttributableEmissionsFA,
  mockDigitizedPlanFuelInputRelevantEmissionFactorFA,
  mockDigitizedPlanFuelInputRelevantEmissionFactorFAYes,
} from '../../testing/mock';
import { FuelInputRelevantEmissionFactorFAComponent } from './fuel-input-relevant-emission-factor.component';

describe('FuelInputRelevantEmissionFactorFAComponent', () => {
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: FuelInputRelevantEmissionFactorFAComponent;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let fixture: ComponentFixture<FuelInputRelevantEmissionFactorFAComponent>;

  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const route = new ActivatedRouteStub({ subInstallationNo: '0' }, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<FuelInputRelevantEmissionFactorFAComponent> {
    get existsInputYes() {
      return this.query<HTMLInputElement>('#exists-option0');
    }

    get existsInputNo() {
      return this.query<HTMLInputElement>('#exists-option1');
    }

    get wasteGasesInputYes() {
      return this.query<HTMLInputElement>('#wasteGasesInput-option0');
    }

    get wasteGasesInputNo() {
      return this.query<HTMLInputElement>('#wasteGasesInput-option1');
    }

    get dataSourcesValue1() {
      return this.getInputValue('select[dataSources.0.fuelInput"]');
    }
    set dataSourcesValue1(value: string) {
      this.setInputValue('[name="dataSources.0.fuelInput"]', value);
    }

    get dataSourcesValue2() {
      return this.getInputValue('select[name="dataSources.0.netCalorificValue"]');
    }
    set dataSourcesValue2(value: string) {
      this.setInputValue('[name="dataSources.0.netCalorificValue"]', value);
    }

    get dataSourcesValue3() {
      return this.getInputValue('select[name="dataSources.0.weightedEmissionFactor"]');
    }
    set dataSourcesValue3(value: string) {
      this.setInputValue('[name="dataSources.0.weightedEmissionFactor"]', value);
    }

    get dataSourcesValue4() {
      return this.getInputValue('select[dataSources.0.wasteGasFuelInput"]');
    }
    set dataSourcesValue4(value: string) {
      this.setInputValue('[name="dataSources.0.wasteGasFuelInput"]', value);
    }

    get dataSourcesValue5() {
      return this.getInputValue('select[name="dataSources.0.wasteGasNetCalorificValue"]');
    }
    set dataSourcesValue5(value: string) {
      this.setInputValue('[name="dataSources.0.wasteGasNetCalorificValue"]', value);
    }

    get dataSourcesValue6() {
      return this.getInputValue('select[name="dataSources.0.emissionFactor"]');
    }
    set dataSourcesValue6(value: string) {
      this.setInputValue('[name="dataSources.0.emissionFactor"]', value);
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
    fixture = TestBed.createComponent(FuelInputRelevantEmissionFactorFAComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    activatedRoute = TestBed.inject(ActivatedRoute);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FuelInputRelevantEmissionFactorFAComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule, PermitApplicationModule],
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

  describe('for existing sub installation', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(
        mockStateBuild(
          {
            monitoringMethodologyPlans: {
              exist: true,
              plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
              digitizedPlan: mockDigitizedPlanDirectlyAttributableEmissionsFA,
            },
          },
          {
            monitoringMethodologyPlans: [true],
            MMP_SUB_INSTALLATION_Fallback_Approach: [false],
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
        asyncData<any>(new HttpResponse({ body: { uuid: 'e227ea8a-778b-4208-9545-e108ea66c118' } })),
      );
      fixture.detectChanges();
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.existsInputYes.click();
      fixture.detectChanges();

      page.wasteGasesInputNo.click();
      fixture.detectChanges();
      page.dataSourcesValue1 = 'METHOD_MONITORING_PLAN';
      page.dataSourcesValue2 = 'CALCULATION_METHOD_MONITORING_PLAN';
      page.dataSourcesValue3 = 'CALCULATION_METHOD_MONITORING_PLAN';
      page.methodologyAppliedDescriptionValue = 'description';
      page.followed = true;
      fixture.detectChanges();
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
              digitizedPlan: mockDigitizedPlanFuelInputRelevantEmissionFactorFA,
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            monitoringMethodologyPlans: [true],
            MMP_SUB_INSTALLATION_Fallback_Approach: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['./../measurable-heat-produced'], {
        relativeTo: activatedRoute,
      });
    });
    it('should submit a valid form when FUEL_BENCHMARK_CL', () => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(
        mockStateBuild(
          {
            monitoringMethodologyPlans: {
              exist: true,
              plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
              digitizedPlan: {
                ...mockDigitizedPlanFuelInputRelevantEmissionFactorFA,
                subInstallations: [
                  {
                    ...mockDigitizedPlanDirectlyAttributableEmissionsFA.subInstallations[0],
                    subInstallationType: 'FUEL_BENCHMARK_CL',
                    fuelInputAndRelevantEmissionFactor: {
                      ...(mockDigitizedPlanDirectlyAttributableEmissionsFA as any).subInstallations[0]
                        .fuelInputAndRelevantEmissionFactor,
                      fuelInputAndRelevantEmissionFactorType: 'FALLBACK_APPROACH',
                    },
                  },
                ],
              },
            },
          },
          {
            monitoringMethodologyPlans: [true],
            MMP_SUB_INSTALLATION_Fallback_Approach: [false],
          },
        ),
      );
      createComponent();

      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      attachmentService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: 'e227ea8a-778b-4208-9545-e108ea66c118' } })),
      );
      fixture.detectChanges();
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.wasteGasesInputNo.click();
      fixture.detectChanges();
      page.dataSourcesValue1 = 'METHOD_MONITORING_PLAN';
      page.dataSourcesValue2 = 'CALCULATION_METHOD_MONITORING_PLAN';
      page.dataSourcesValue3 = 'CALCULATION_METHOD_MONITORING_PLAN';
      page.methodologyAppliedDescriptionValue = 'description';
      page.followed = true;
      fixture.detectChanges();
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
                ...mockDigitizedPlanFuelInputRelevantEmissionFactorFA,
                subInstallations: [
                  {
                    ...mockDigitizedPlanFuelInputRelevantEmissionFactorFA.subInstallations['0'],
                    subInstallationType: 'FUEL_BENCHMARK_CL',
                    fuelInputAndRelevantEmissionFactor: {
                      ...mockDigitizedPlanFuelInputRelevantEmissionFactorFA.subInstallations['0']
                        .fuelInputAndRelevantEmissionFactor,
                      fuelInputAndRelevantEmissionFactorType: 'FALLBACK_APPROACH',
                      exists: undefined,
                    },
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            monitoringMethodologyPlans: [true],
            MMP_SUB_INSTALLATION_Fallback_Approach: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['./../measurable-heat-exported'], {
        relativeTo: activatedRoute,
      });
    });

    it('should submit a valid form with wasteGases inputs activated', () => {
      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      attachmentService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: 'e227ea8a-778b-4208-9545-e108ea66c118' } })),
      );
      fixture.detectChanges();
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.existsInputYes.click();
      fixture.detectChanges();

      page.wasteGasesInputYes.click();
      fixture.detectChanges();
      page.dataSourcesValue1 = 'METHOD_MONITORING_PLAN';
      page.dataSourcesValue2 = 'CALCULATION_METHOD_MONITORING_PLAN';
      page.dataSourcesValue3 = 'CALCULATION_METHOD_MONITORING_PLAN';
      page.dataSourcesValue4 = 'METHOD_MONITORING_PLAN';
      page.dataSourcesValue5 = 'CALCULATION_METHOD_MONITORING_PLAN';
      page.dataSourcesValue6 = 'CALCULATION_METHOD_MONITORING_PLAN';

      page.methodologyAppliedDescriptionValue = 'description';
      page.followed = true;
      fixture.detectChanges();
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
              digitizedPlan: mockDigitizedPlanFuelInputRelevantEmissionFactorFAYes,
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            monitoringMethodologyPlans: [true],
            MMP_SUB_INSTALLATION_Fallback_Approach: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['./../measurable-heat-produced'], {
        relativeTo: activatedRoute,
      });
    });
  });
});
