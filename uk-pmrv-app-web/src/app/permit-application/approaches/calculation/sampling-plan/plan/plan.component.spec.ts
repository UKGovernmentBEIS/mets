import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { CalculationOfCO2MonitoringApproach, TasksService } from 'pmrv-api';

import { PermitIssuanceStore } from '../../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { CalculationModule } from '../../calculation.module';
import { PlanComponent } from './plan.component';

describe('PlanComponent', () => {
  let component: PlanComponent;
  let fixture: ComponentFixture<PlanComponent>;
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.CALCULATION_CO2.samplingPlan.details', statusKey: 'CALCULATION_CO2_Plan' },
  );
  const mockSamplingPlan = (mockState.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
    .samplingPlan;

  class Page extends BasePage<PlanComponent> {
    get submitButton(): HTMLButtonElement {
      return this.query('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get description(): string {
      return this.getInputValue('#procedurePlan.procedureDescription');
    }
    set description(value: string) {
      this.setInputValue('#procedurePlan.procedureDescription', value);
    }

    get name(): string {
      return this.getInputValue('#procedurePlan.procedureDocumentName');
    }
    set name(value: string) {
      this.setInputValue('#procedurePlan.procedureDocumentName', value);
    }

    get reference(): string {
      return this.getInputValue('#procedurePlan.procedureReference');
    }
    set reference(value: string) {
      this.setInputValue('#procedurePlan.procedureReference', value);
    }

    get department(): string {
      return this.getInputValue('#procedurePlan.responsibleDepartmentOrRole');
    }
    set department(value: string) {
      this.setInputValue('#procedurePlan.responsibleDepartmentOrRole', value);
    }

    get location(): string {
      return this.getInputValue('#procedurePlan.locationOfRecords');
    }
    set location(value: string) {
      this.setInputValue('#procedurePlan.locationOfRecords', value);
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(PlanComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalculationModule, RouterTestingModule],
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

  describe('for new procedure plan', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            CALCULATION_CO2: {
              ...mockState.permit.monitoringApproaches.CALCULATION_CO2,
              samplingPlan: {
                exist: true,
                details: {},
              },
            },
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to next step of wizard', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual([
        'Enter a brief description of the procedure',
        'Enter the name of the procedure document',
        'Enter a procedure reference',
        'Enter the name of the department or role responsible',
        'Enter the location of the records',
      ]);

      page.description = mockSamplingPlan.details.procedurePlan.procedureDescription;
      page.name = mockSamplingPlan.details.procedurePlan.procedureDocumentName;
      page.reference = mockSamplingPlan.details.procedurePlan.procedureReference;
      page.department = mockSamplingPlan.details.procedurePlan.responsibleDepartmentOrRole;
      page.location = mockSamplingPlan.details.procedurePlan.locationOfRecords;

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              CALCULATION_CO2: {
                ...mockState.permit.monitoringApproaches.CALCULATION_CO2,
                samplingPlan: {
                  exist: true,
                  details: {
                    procedurePlan: {
                      appliedStandards: null,
                      diagramReference: null,
                      itSystemUsed: null,
                      procedureDescription: mockSamplingPlan.details.procedurePlan.procedureDescription,
                      procedureDocumentName: mockSamplingPlan.details.procedurePlan.procedureDocumentName,
                      procedureReference: mockSamplingPlan.details.procedurePlan.procedureReference,
                      responsibleDepartmentOrRole: mockSamplingPlan.details.procedurePlan.responsibleDepartmentOrRole,
                      locationOfRecords: mockSamplingPlan.details.procedurePlan.locationOfRecords,
                      procedurePlanIds: [],
                    },
                  },
                },
              },
            },
          },
          {
            CALCULATION_CO2_Plan: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../appropriateness'], { relativeTo: route });
    });
  });

  describe('for changing procedure plan', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill form from store', () => {
      expect(page.description).toEqual(mockSamplingPlan.details.procedurePlan.procedureDescription);
      expect(page.name).toEqual(mockSamplingPlan.details.procedurePlan.procedureDocumentName);
      expect(page.reference).toEqual(mockSamplingPlan.details.procedurePlan.procedureReference);
      expect(page.department).toEqual(mockSamplingPlan.details.procedurePlan.responsibleDepartmentOrRole);
      expect(page.location).toEqual(mockSamplingPlan.details.procedurePlan.locationOfRecords);
    });

    it('should post a valid form and navigate to next step of wizard', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.description = 'New description';
      page.submitButton.click();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...mockState.permit.monitoringApproaches,
              CALCULATION_CO2: {
                ...mockState.permit.monitoringApproaches.CALCULATION_CO2,
                samplingPlan: {
                  ...mockSamplingPlan,
                  details: {
                    ...mockSamplingPlan.details,
                    procedurePlan: {
                      ...mockSamplingPlan.details.procedurePlan,
                      procedureDescription: 'New description',
                    },
                  },
                },
              },
            },
          },
          {
            CALCULATION_CO2_Plan: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../appropriateness'], { relativeTo: route });
    });

    it('should not post if not dirty', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.submitButton.click();

      expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../appropriateness'], { relativeTo: route });
    });
  });
});
