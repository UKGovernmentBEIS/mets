import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CalculationOfCO2MonitoringApproach, RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '../../../../../../../testing';
import { PermitIssuanceStore } from '../../../../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../../../../shared/shared.module';
import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockStateBuild } from '../../../../../testing/mock-state';
import { CalculationModule } from '../../../calculation.module';
import { JustificationComponent } from './justification.component';

describe('JustificationComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: JustificationComponent;
  let fixture: ComponentFixture<JustificationComponent>;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.CALCULATION_CO2.sourceStreamCategoryAppliedTiers',
    statusKey: 'CALCULATION_CO2_Activity_Data',
  });
  const sourceStreamCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach
  ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;
  const technicalInfeasibilityExplanation = 'technicalInfeasibilityExplanation';

  class Page extends BasePage<JustificationComponent> {
    get unreasonableCost() {
      return this.query<HTMLInputElement>('#justification-0');
    }
    get technicalInfeasibility() {
      return this.query<HTMLInputElement>('#justification-1');
    }
    get technicalInfeasibilityExplanation() {
      return this.getInputValue('#technicalInfeasibilityExplanation');
    }
    set technicalInfeasibilityExplanation(value: string) {
      this.setInputValue('#technicalInfeasibilityExplanation', value);
    }
    set fileValue(value: File[]) {
      this.setInputValue('input[type="file"]', value);
    }
    get continueButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(JustificationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalculationModule, RouterTestingModule, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  describe('for new justification', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              CALCULATION_CO2: {
                type: 'CALCULATION_CO2',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: sourceStreamCategory,
                    activityData: {
                      measurementDevicesOrMethods: ['16236817394240.1574963093314663'],
                      uncertainty: 'LESS_OR_EQUAL_5_0',
                      tier: 'TIER_1',
                      isHighestRequiredTier: false,
                    },
                  },
                ],
              },
            },
          },
          {
            CALCULATION_CO2_Category: [true],
            CALCULATION_CO2_Activity_Data: [false],
          },
        ),
      );
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      attachmentService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: 'e227ea8a-778b-4208-9545-e108ea66c114' } })),
      );
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select a justification']);

      page.technicalInfeasibility.click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual([
        'Explain why it is not technically feasible to meet the highest tier, as set out in Article 17 of the Monitoring and Reporting Regulations (MRR)',
      ]);

      page.technicalInfeasibilityExplanation = technicalInfeasibilityExplanation;
      page.fileValue = [new File(['file'], 'file.txt')];
      fixture.detectChanges();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              CALCULATION_CO2: {
                ...store.getState().permit.monitoringApproaches.CALCULATION_CO2,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: sourceStreamCategory,
                    activityData: {
                      measurementDevicesOrMethods: ['16236817394240.1574963093314663'],
                      uncertainty: 'LESS_OR_EQUAL_5_0',
                      tier: 'TIER_1',
                      isHighestRequiredTier: false,
                      noHighestRequiredTierJustification: {
                        isCostUnreasonable: false,
                        isTechnicallyInfeasible: true,
                        technicalInfeasibilityExplanation,
                        files: ['e227ea8a-778b-4208-9545-e108ea66c114'],
                      },
                    },
                  },
                ],
              },
            },
          },
          {
            ...store.getState().permitSectionsCompleted,
            CALCULATION_CO2_Activity_Data: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });
  });

  describe('for existing justification', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              CALCULATION_CO2: {
                type: 'CALCULATION_CO2',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: sourceStreamCategory,
                    activityData: {
                      measurementDevicesOrMethods: [''],
                      uncertainty: 'LESS_OR_EQUAL_1_5',
                      tier: 'TIER_1',
                      isHighestRequiredTier: false,
                      noHighestRequiredTierJustification: {
                        isTechnicallyInfeasible: true,
                        technicalInfeasibilityExplanation,
                      },
                    },
                  },
                ],
              },
            },
          },
          {
            CALCULATION_CO2_Category: [true],
            CALCULATION_CO2_Activity_Data: [false],
          },
        ),
      );
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      expect(page.unreasonableCost.checked).toBeFalsy();
      expect(page.technicalInfeasibility.checked).toBeTruthy();

      page.unreasonableCost.click();
      page.technicalInfeasibility.click();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              CALCULATION_CO2: {
                ...store.getState().permit.monitoringApproaches.CALCULATION_CO2,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: sourceStreamCategory,
                    activityData: {
                      measurementDevicesOrMethods: [''],
                      uncertainty: 'LESS_OR_EQUAL_1_5',
                      tier: 'TIER_1',
                      isHighestRequiredTier: false,
                      noHighestRequiredTierJustification: {
                        isCostUnreasonable: true,
                        isTechnicallyInfeasible: false,
                        files: [],
                      },
                    },
                  },
                ],
              },
            },
          },
          {
            ...store.getState().permitSectionsCompleted,
            CALCULATION_CO2_Activity_Data: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });
  });
});
