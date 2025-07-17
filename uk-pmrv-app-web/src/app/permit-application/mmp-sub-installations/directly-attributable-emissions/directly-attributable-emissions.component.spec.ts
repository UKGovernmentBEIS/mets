import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

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
  mockDigitizedPlanDirectlyAttributableEmissions,
  mockDigitizedPlanImportedMeasureableHeat,
} from '../testing/mock';
import { DirectlyAttributableEmissionsComponent } from './directly-attributable-emissions.component';

describe('DirectlyAttributableEmissionsComponent', () => {
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: DirectlyAttributableEmissionsComponent;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let fixture: ComponentFixture<DirectlyAttributableEmissionsComponent>;

  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const route = new ActivatedRouteStub({ subInstallationNo: '0' }, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<DirectlyAttributableEmissionsComponent> {
    get attributionValue() {
      return this.getInputValue('#attribution');
    }
    set attributionValue(value: string) {
      this.setInputValue('#attribution', value);
    }

    get furtherInternalSourceStreamsRelevant() {
      return this.query<HTMLInputElement>('#furtherInternalSourceStreamsRelevant-option0');
    }

    get dataSourcesValue() {
      return this.getInputValue('select[name="dataSources.0.amounts"]');
    }
    set dataSourcesValue(value: string) {
      this.setInputValue('[name="dataSources.0.amounts"]', value);
    }

    get methodologyAppliedDescriptionValue() {
      return this.getInputValue('#methodologyAppliedDescription');
    }
    set methodologyAppliedDescriptionValue(value: string) {
      this.setInputValue('#methodologyAppliedDescription', value);
    }

    get transferredCO2ImportedOrExportedRelevant() {
      return this.query<HTMLInputElement>('#transferredCO2ImportedOrExportedRelevant-option0');
    }

    get amountsMonitoringDescriptionValue() {
      return this.getInputValue('#amountsMonitoringDescription');
    }
    set amountsMonitoringDescriptionValue(value: string) {
      this.setInputValue('#amountsMonitoringDescription', value);
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
    fixture = TestBed.createComponent(DirectlyAttributableEmissionsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    activatedRoute = TestBed.inject(ActivatedRoute);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DirectlyAttributableEmissionsComponent],
      imports: [SharedModule, SharedPermitModule, PermitApplicationModule],
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
              digitizedPlan: mockDigitizedPlanImportedMeasureableHeat,
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
        asyncData<any>(new HttpResponse({ body: { uuid: 'e227ea8a-778b-4208-9545-e108ea66c117' } })),
      );
      fixture.detectChanges();
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.attributionValue = 'attribution';
      page.furtherInternalSourceStreamsRelevant.click();
      fixture.detectChanges();
      page.dataSourcesValue = 'METHOD_MONITORING_PLAN';
      page.methodologyAppliedDescriptionValue = 'description';
      page.transferredCO2ImportedOrExportedRelevant.click();
      fixture.detectChanges();
      page.amountsMonitoringDescriptionValue = 'description';
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
              digitizedPlan: mockDigitizedPlanDirectlyAttributableEmissions,
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
      expect(navigateSpy).toHaveBeenCalledWith(['../fuel-input-relevant-emission-factor'], {
        relativeTo: activatedRoute,
      });
    });
  });
});
