import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { MeasurementOfCO2MonitoringApproach, RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '../../../../../../testing';
import { PermitIssuanceStore } from '../../../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { MeasurementModule } from '../../measurement.module';
import { SamplingJustificationComponent } from './sampling-justification.component';

describe('SamplingJustificationComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: SamplingJustificationComponent;
  let fixture: ComponentFixture<SamplingJustificationComponent>;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.MEASUREMENT_CO2.emissionPointCategoryAppliedTiers',
    statusKey: 'MEASUREMENT_CO2_Biomass_Fraction',
  });

  const emissionPointCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach
  ).emissionPointCategoryAppliedTiers[0].emissionPointCategory;

  class Page extends BasePage<SamplingJustificationComponent> {
    get isCostUnreasonable() {
      return this.query<HTMLInputElement>('#justification-0');
    }
    get frequencyLessThanRequired() {
      return this.query<HTMLInputElement>('#justification-1');
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
    fixture = TestBed.createComponent(SamplingJustificationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MeasurementModule, RouterTestingModule, SharedModule],
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
              MEASUREMENT_CO2: {
                type: 'MEASUREMENT_CO2',
                emissionPointCategoryAppliedTiers: [
                  {
                    emissionPointCategory,
                    biomassFraction: {
                      exist: true,
                      tier: 'TIER_3',
                      analysisMethods: [
                        {
                          analysis: 'asdas',
                          frequencyMeetsMinRequirements: false,
                          laboratoryAccreditationEvidence: 'fdfd',
                          laboratoryAccredited: false,
                          laboratoryName: 'adasd',
                          samplingFrequency: 'WEEKLY',
                        },
                      ],
                    },
                  },
                ],
              },
            },
          },
          {
            MEASUREMENT_CO2_Biomass_Fraction: [false],
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
      expect(page.errorSummaryErrorList).toEqual(['Select a reason']);

      page.isCostUnreasonable.click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              MEASUREMENT_CO2: {
                type: 'MEASUREMENT_CO2',
                emissionPointCategoryAppliedTiers: [
                  {
                    emissionPointCategory,
                    biomassFraction: {
                      exist: true,
                      tier: 'TIER_3',
                      analysisMethods: [
                        {
                          analysis: 'asdas',
                          frequencyMeetsMinRequirements: false,
                          laboratoryAccreditationEvidence: 'fdfd',
                          laboratoryAccredited: false,
                          laboratoryName: 'adasd',
                          samplingFrequency: 'WEEKLY',
                          reducedSamplingFrequencyJustification: {
                            files: [],
                            isCostUnreasonable: true,
                            isOneThirdRuleAndSampling: false,
                          },
                        },
                      ],
                    },
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            MEASUREMENT_CO2_Biomass_Fraction: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../../../analysis-method-list'], { relativeTo: route });
    });
  });

  describe('for existing justification', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              MEASUREMENT_CO2: {
                type: 'MEASUREMENT_CO2',
                emissionPointCategoryAppliedTiers: [
                  {
                    emissionPointCategory,
                    biomassFraction: {
                      exist: true,
                      tier: 'TIER_3',
                      analysisMethods: [
                        {
                          analysis: 'asdas',
                          frequencyMeetsMinRequirements: false,
                          laboratoryAccreditationEvidence: 'fdfd',
                          laboratoryAccredited: false,
                          laboratoryName: 'adasd',
                          samplingFrequency: 'WEEKLY',
                          reducedSamplingFrequencyJustification: {
                            files: [],
                            isCostUnreasonable: true,
                            isOneThirdRuleAndSampling: false,
                          },
                        },
                      ],
                    },
                  },
                ],
              },
            },
          },
          {
            MEASUREMENT_CO2_Category: [true],
            MEASUREMENT_CO2_Biomass_Fraction: [false],
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

    it('should fill the form', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.isCostUnreasonable.checked).toBeTruthy();
      expect(page.frequencyLessThanRequired.checked).toBeFalsy();
    });
  });
});
