import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { MeasurementOfCO2MonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { PermitIssuanceStore } from '../../../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { MeasurementModule } from '../../measurement.module';
import { AnalysisMethodComponent } from './analysis-method.component';

describe('AnalysisMethodComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: AnalysisMethodComponent;
  let fixture: ComponentFixture<AnalysisMethodComponent>;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.MEASUREMENT_CO2.emissionPointCategoryAppliedTiers',
    statusKey: 'MEASUREMENT_CO2_Biomass_Fraction',
  });

  const emissionPointCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach
  ).emissionPointCategoryAppliedTiers[0].emissionPointCategory;

  class Page extends BasePage<AnalysisMethodComponent> {
    get frequencyMeetsMinRequirementsRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="frequencyMeetsMinRequirements"]');
    }
    get laboratoryAccreditedRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="laboratoryAccredited"]');
    }
    get analysis() {
      return this.getInputValue('#analysis');
    }
    set analysis(value: string) {
      this.setInputValue('#analysis', value);
    }
    get samplingFrequency(): string {
      return this.query('select').value;
    }
    set samplingFrequency(value: string) {
      this.setInputValue('select', value);
    }
    get laboratoryName(): string {
      return this.getInputValue('#laboratoryName');
    }
    set laboratoryName(value: string) {
      this.setInputValue('#laboratoryName', value);
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
    fixture = TestBed.createComponent(AnalysisMethodComponent);
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
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  describe('for new analysis', () => {
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
                      oneThirdRule: false,
                      tier: 'TIER_1',
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
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual([
        'Enter a method of analysis',
        'Select a sampling frequency',
        'Select yes or no',
        'Enter laboratory name',
        'Select yes or no',
      ]);

      page.analysis = 'analyze';
      page.samplingFrequency = 'WEEKLY';
      page.frequencyMeetsMinRequirementsRadios[1].click();
      page.laboratoryAccreditedRadios[1].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter laboratory name']);

      page.laboratoryName = 'lab';
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
                      oneThirdRule: false,
                      tier: 'TIER_1',
                      defaultValueApplied: undefined,
                      analysisMethodUsed: true,
                      analysisMethods: [
                        {
                          analysis: 'analyze',
                          files: [],
                          frequencyMeetsMinRequirements: false,
                          laboratoryAccredited: false,
                          laboratoryName: 'lab',
                          samplingFrequency: 'WEEKLY',
                          subParameter: null,
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
      expect(navigateSpy).toHaveBeenCalledWith(['./sampling-justification'], { relativeTo: route });
    });
  });

  describe('for existing analysis', () => {
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
                      oneThirdRule: false,
                      tier: 'TIER_1',
                      isHighestRequiredTier: true,
                      defaultValueApplied: undefined,
                      analysisMethodUsed: true,
                      analysisMethods: [
                        {
                          analysis: 'analyze',
                          files: [],
                          frequencyMeetsMinRequirements: false,
                          laboratoryAccredited: false,
                          laboratoryName: 'lab',
                          samplingFrequency: 'WEEKLY',
                          subParameter: null,
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

    it('should fill the form', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.analysis).toEqual('analyze');
      expect(page.samplingFrequency).toEqual('WEEKLY');
      expect(page.frequencyMeetsMinRequirementsRadios[0].checked).toBeFalsy();
      expect(page.frequencyMeetsMinRequirementsRadios[1].checked).toBeTruthy();
      expect(page.laboratoryAccreditedRadios[0].checked).toBeFalsy();
      expect(page.laboratoryAccreditedRadios[1].checked).toBeTruthy();
      expect(page.laboratoryName).toEqual('lab');
    });
  });
});
