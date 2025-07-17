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
import { EntryStepComponent } from './entry-step.component';

describe('EntryStepComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: EntryStepComponent;
  let fixture: ComponentFixture<EntryStepComponent>;

  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.MEASUREMENT_CO2.emissionPointCategoryAppliedTiers',
    statusKey: 'MEASUREMENT_CO2_Biomass_Fraction',
  });

  class Page extends BasePage<EntryStepComponent> {
    get paragraphs() {
      return this.queryAll<HTMLParagraphElement>('p');
    }

    get existRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
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
    fixture = TestBed.createComponent(EntryStepComponent);
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

  describe('task prerequisites not completed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            MEASUREMENT_CO2: {
              emissionPointCategoryAppliedTiers: [],
            },
          },
        }),
      );
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the can not start page', () => {
      expect(
        page.paragraphs.find((p) => p.textContent.trim() === 'Other tasks must be completed before you can start:'),
      ).toBeTruthy();
    });
  });

  describe('for new biomass fraction value', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {},
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

    it('should require a yes or a no', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select yes or no']);

      page.existRadios[0].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...mockState.permit.monitoringApproaches,
              MEASUREMENT_CO2: {
                ...mockState.permit.monitoringApproaches.MEASUREMENT_CO2,
                emissionPointCategoryAppliedTiers: [
                  {
                    emissionPointCategory: (
                      mockState.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach
                    ).emissionPointCategoryAppliedTiers[0].emissionPointCategory,
                    appliedStandard: {
                      appliedStandard: 'standard1',
                      deviationFromAppliedStandardDetails: 'deviation1',
                      deviationFromAppliedStandardExist: true,
                      laboratoryAccreditationEvidence: 'labEvidence',
                      laboratoryAccredited: false,
                      laboratoryName: 'lab1',
                      parameter: 'param1',
                    },
                    biomassFraction: {
                      exist: true,
                    },
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            MEASUREMENT_CO2_Category: [true],
            MEASUREMENT_CO2_Biomass_Fraction: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['./tier'], { relativeTo: route });
    });
  });

  describe('for existing biomass fraction', () => {
    beforeEach(() => {
      const emissionPointCategory = (
        mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach
      ).emissionPointCategoryAppliedTiers[0].emissionPointCategory;

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
                      exist: false,
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

      expect(page.existRadios.length).toEqual(2);
      expect(page.existRadios[0].checked).toBeFalsy();
      expect(page.existRadios[1].checked).toBeTruthy();
    });
  });
});
