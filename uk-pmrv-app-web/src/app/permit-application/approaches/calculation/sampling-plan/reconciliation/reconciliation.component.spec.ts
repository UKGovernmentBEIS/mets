import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CalculationOfCO2MonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { PermitIssuanceStore } from '../../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { CalculationModule } from '../../calculation.module';
import { ReconciliationComponent } from './reconciliation.component';

describe('ReconciliationComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: ReconciliationComponent;
  let fixture: ComponentFixture<ReconciliationComponent>;

  const tasksService = mockClass(TasksService);
  const mockCalculation = mockState.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      taskKey: 'monitoringApproaches.CALCULATION_CO2.samplingPlan.details.yearEndReconciliation',
      statusKey: 'CALCULATION_CO2_Plan',
    },
  );

  class Page extends BasePage<ReconciliationComponent> {
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

    get existRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
    }

    get description(): string {
      return this.getInputValue('#procedureForm.procedureDescription');
    }
    set description(value: string) {
      this.setInputValue('#procedureForm.procedureDescription', value);
    }

    get name(): string {
      return this.getInputValue('#procedureForm.procedureDocumentName');
    }
    set name(value: string) {
      this.setInputValue('#procedureForm.procedureDocumentName', value);
    }

    get reference(): string {
      return this.getInputValue('#procedureForm.procedureReference');
    }
    set reference(value: string) {
      this.setInputValue('#procedureForm.procedureReference', value);
    }

    get department(): string {
      return this.getInputValue('#procedureForm.responsibleDepartmentOrRole');
    }
    set department(value: string) {
      this.setInputValue('#procedureForm.responsibleDepartmentOrRole', value);
    }

    get location(): string {
      return this.getInputValue('#procedureForm.locationOfRecords');
    }
    set location(value: string) {
      this.setInputValue('#procedureForm.locationOfRecords', value);
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ReconciliationComponent);
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

  describe('for new reconciliation', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            CALCULATION_CO2: {
              ...mockCalculation,
              samplingPlan: {
                exist: true,
                details: {
                  ...mockCalculation.samplingPlan.details,
                  yearEndReconciliation: {},
                },
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

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select yes or no']);

      page.existRadios[0].click();
      fixture.detectChanges();

      expect(page.errorSummaryErrorList).toEqual([
        'Enter a brief description of the procedure',
        'Enter the name of the procedure document',
        'Enter a procedure reference',
        'Enter the name of the department or role responsible',
        'Enter the location of the records',
      ]);

      page.description = 'Reconciliation description';
      page.name = 'Reconciliation name';
      page.reference = 'Reconciliation reference';
      page.department = 'Reconciliation department';
      page.location = 'Reconciliation location';

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
                ...mockCalculation,
                samplingPlan: {
                  ...mockCalculation.samplingPlan,
                  details: {
                    ...mockCalculation.samplingPlan.details,
                    yearEndReconciliation: {
                      exist: true,
                      procedureForm: {
                        appliedStandards: null,
                        diagramReference: null,
                        itSystemUsed: null,
                        procedureDescription: 'Reconciliation description',
                        procedureDocumentName: 'Reconciliation name',
                        procedureReference: 'Reconciliation reference',
                        responsibleDepartmentOrRole: 'Reconciliation department',
                        locationOfRecords: 'Reconciliation location',
                      },
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
      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });
  });

  describe('for changing reconciliation', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill form from store', () => {
      const mockPlan = mockCalculation.samplingPlan;
      expect(page.description).toEqual(mockPlan.details.yearEndReconciliation.procedureForm.procedureDescription);
      expect(page.name).toEqual(mockPlan.details.yearEndReconciliation.procedureForm.procedureDocumentName);
      expect(page.reference).toEqual(mockPlan.details.yearEndReconciliation.procedureForm.procedureReference);
      expect(page.department).toEqual(mockPlan.details.yearEndReconciliation.procedureForm.responsibleDepartmentOrRole);
      expect(page.location).toEqual(mockPlan.details.yearEndReconciliation.procedureForm.locationOfRecords);
    });

    it('should post a valid form and navigate back to answers', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const mockPlan = (mockState.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
        .samplingPlan;

      page.description = 'Reconciliation edited description';
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
                  ...mockPlan,
                  details: {
                    ...mockPlan.details,
                    yearEndReconciliation: {
                      ...mockPlan.details.yearEndReconciliation,
                      procedureForm: {
                        ...mockPlan.details.yearEndReconciliation.procedureForm,
                        procedureDescription: 'Reconciliation edited description',
                      },
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
      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });

    it('should not post if not dirty', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.submitButton.click();

      expect(tasksService.processRequestTaskAction).not.toHaveBeenCalled();
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });
  });
});
