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

import { mockDigitizedPlanImportedExportedMeasurableHeat, mockDigitizedPlanWasteGasBalance } from '../testing/mock';
import { WasteGasBalanceComponent } from './waste-gas-balance.component';

describe('WasteGasBalanceComponent', () => {
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: WasteGasBalanceComponent;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let fixture: ComponentFixture<WasteGasBalanceComponent>;

  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const route = new ActivatedRouteStub({ subInstallationNo: '0' }, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<WasteGasBalanceComponent> {
    get wasteGasActivityTypes() {
      return this.query<HTMLInputElement>('#wasteGasActivities-0');
    }
    get dataSourcesValue1() {
      return this.getInputValue('select[name="dataSources.0.WASTE_GAS_PRODUCED.entity"]');
    }
    set dataSourcesValue1(value: string) {
      this.setInputValue('[name="dataSources.0.WASTE_GAS_PRODUCED.entity"]', value);
    }
    get dataSourcesMethodologyAppliedDescriptionValue() {
      return this.getInputValue('#dataSourcesMethodologyAppliedDescription');
    }
    set dataSourcesMethodologyAppliedDescriptionValue(value: string) {
      this.setInputValue('#dataSourcesMethodologyAppliedDescription', value);
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
    fixture = TestBed.createComponent(WasteGasBalanceComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    activatedRoute = TestBed.inject(ActivatedRoute);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WasteGasBalanceComponent],
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
              digitizedPlan: mockDigitizedPlanImportedExportedMeasurableHeat,
            },
          },
          {
            monitoringMethodologyPlans: [true],
            MMP_SUB_INSTALLATION_Product_Benchmark: [false],
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
        asyncData<any>(new HttpResponse({ body: { uuid: 'e227ea8a-778b-4208-9545-e108ea66c120' } })),
      );
      fixture.detectChanges();
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.wasteGasActivityTypes.click();
      fixture.detectChanges();

      page.dataSourcesValue1 = 'METHOD_MONITORING_PLAN';
      fixture.detectChanges();
      page.dataSourcesMethodologyAppliedDescriptionValue = 'description';
      page.followed = true;
      fixture.detectChanges();
      page.fileValue = [new File(['file'], 'file.txt')];
      fixture.detectChanges();

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
              digitizedPlan: mockDigitizedPlanWasteGasBalance,
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            monitoringMethodologyPlans: [true],
            MMP_SUB_INSTALLATION_Product_Benchmark: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../summary'], {
        relativeTo: activatedRoute,
      });
    });
  });

  describe('with subInstallationType equal to REFINERY_PRODUCTS', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(
        mockStateBuild(
          {
            monitoringMethodologyPlans: {
              exist: true,
              plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
              digitizedPlan: {
                subInstallations: [
                  {
                    ...mockDigitizedPlanImportedExportedMeasurableHeat.subInstallations[0],
                    subInstallationType: 'REFINERY_PRODUCTS',
                  },
                ],
              },
            },
          },
          {
            monitoringMethodologyPlans: [true],
            MMP_SUB_INSTALLATION_Product_Benchmark: [false],
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
        asyncData<any>(new HttpResponse({ body: { uuid: 'e227ea8a-778b-4208-9545-e108ea66c120' } })),
      );
      fixture.detectChanges();
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.wasteGasActivityTypes.click();
      fixture.detectChanges();

      page.dataSourcesValue1 = 'METHOD_MONITORING_PLAN';
      fixture.detectChanges();
      page.dataSourcesMethodologyAppliedDescriptionValue = 'description';
      page.followed = true;
      fixture.detectChanges();
      page.fileValue = [new File(['file'], 'file.txt')];
      fixture.detectChanges();

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
                subInstallations: [
                  {
                    ...mockDigitizedPlanWasteGasBalance.subInstallations[0],
                    subInstallationType: 'REFINERY_PRODUCTS',
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            monitoringMethodologyPlans: [true],
            MMP_SUB_INSTALLATION_Product_Benchmark: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledTimes(1);

      expect(navigateSpy).toHaveBeenCalledWith(['../calculation-refinery-products'], {
        relativeTo: activatedRoute,
      });
    });
  });
});
