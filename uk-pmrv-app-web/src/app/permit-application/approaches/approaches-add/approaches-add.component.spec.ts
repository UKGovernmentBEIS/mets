import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { mockPermitApplyPayload } from '@permit-application/testing/mock-permit-apply-action';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { BasePage, mockClass } from '@testing';

import { CalculationOfCO2MonitoringApproach, TasksService } from 'pmrv-api';

import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPostBuild, mockState } from '../../testing/mock-state';
import { ApproachesAddComponent } from './approaches-add.component';

describe('ApproachesAddComponent', () => {
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: ApproachesAddComponent;
  let fixture: ComponentFixture<ApproachesAddComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ApproachesAddComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get monitoringApproaches() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__input');
    }

    get hasTransferCalculationCO2() {
      return this.queryAll<HTMLInputElement>('input[name$="hasTransferCalculationCO2"]');
    }

    get hasTransferCalculationCO2YesValue() {
      return this.query<HTMLInputElement>('#hasTransferCalculationCO2-option0');
    }

    get hasTransferCalculationCO2NoValue() {
      return this.query<HTMLInputElement>('#hasTransferCalculationCO2-option1');
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ApproachesAddComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(ApproachesAddComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('selecting monitoring approaches for first time', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockState,
        permit: {
          ...mockState.permit,
          monitoringApproaches: undefined,
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all available monitoring approaches', () => {
      expect(page.monitoringApproaches.length).toEqual(6);
    });

    it('should submit a valid form, update the store and navigate correctly', () => {
      expect(page.monitoringApproaches.length).toEqual(6);
      page.monitoringApproaches.forEach((item) => expect(item.checked).toBe(false));
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select a monitoring approach']);

      page.monitoringApproaches[0].click();
      fixture.detectChanges();
      page.hasTransferCalculationCO2[0].click();
      fixture.detectChanges();

      fixture.detectChanges();
      expect(page.monitoringApproaches[0].checked).toBeTruthy();

      expect(page.errorSummary).toBeTruthy();

      tasksService.processRequestTaskAction.mockReturnValue(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.submitButton.click();
      fixture.detectChanges();

      const selectedMonitoringApproaches = {
        CALCULATION_CO2: {
          type: 'CALCULATION_CO2',
          hasTransfer: true,
        },
        TRANSFERRED_CO2_N2O: {
          type: 'TRANSFERRED_CO2_N2O',
        },
      };

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(2);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild({ monitoringApproaches: selectedMonitoringApproaches }, { monitoringApproaches: [false] }),
      );
      expect(store.permit.monitoringApproaches).toEqual(selectedMonitoringApproaches);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
    });
  });

  describe('uncheck selected monitoring approach', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockState,
        permit: {
          ...mockState.permit,
          monitoringApproaches: {
            CALCULATION_CO2: {
              type: 'CALCULATION_CO2',
            },
            CALCULATION_PFC: {
              type: 'CALCULATION_PFC',
            },
          },
        },
        permitSectionsCompleted: {
          ...mockState.permitSectionsCompleted,
          CALCULATION_CO2_Category: [true],
          CALCULATION_CO2_Calorific: [true],
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all available monitoring approaches', () => {
      expect(page.monitoringApproaches.length).toEqual(6);
    });

    it('should submit a valid form and update the store', () => {
      page.monitoringApproaches[0].click();

      fixture.detectChanges();
      expect(page.monitoringApproaches[0].checked).toBeFalsy();
      expect(page.errorSummary).toBeFalsy();

      tasksService.processRequestTaskAction.mockReturnValue(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      const selectedMonitoringApproaches = {
        CALCULATION_PFC: {
          type: 'CALCULATION_PFC',
        },
      };
      const updatedPermitSectionsCompleted = {
        sourceStreams: [false],
        assignmentOfResponsibilities: [true],
        estimatedAnnualEmissions: [true],
        environmentalPermitsAndLicences: [true],
        installationDescription: [true],
        regulatedActivities: [true],
        monitoringApproaches: [false],
      };

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(2);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild({ monitoringApproaches: selectedMonitoringApproaches }, { monitoringApproaches: [false] }),
      );
      expect(store.permit.monitoringApproaches).toEqual(selectedMonitoringApproaches);
      expect(store.payload.permitSectionsCompleted).toEqual(updatedPermitSectionsCompleted);
    });
  });

  describe('select Yes transfer to saved monitoring approach without transfer', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockState,
        permit: {
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            CALCULATION_CO2: {
              ...mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION_CO2,
              hasTransfer: false,
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: {},
                },
              ],
            } as CalculationOfCO2MonitoringApproach,
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should set entryAccountingForTransfer to false', () => {
      page.hasTransferCalculationCO2YesValue.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      tasksService.processRequestTaskAction.mockReturnValue(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      const selectedMonitoringApproaches = {
        CALCULATION_CO2: {
          ...mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION_CO2,
          hasTransfer: true,
          sourceStreamCategoryAppliedTiers: [
            {
              sourceStreamCategory: {
                transfer: {
                  entryAccountingForTransfer: false,
                  transferType: 'TRANSFER_CO2',
                },
              },
            },
          ],
        } as CalculationOfCO2MonitoringApproach,
        TRANSFERRED_CO2_N2O: {
          type: 'TRANSFERRED_CO2_N2O',
        },
      };

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(2);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild({ monitoringApproaches: selectedMonitoringApproaches }, { monitoringApproaches: [false] }),
      );
    });
  });

  describe('change transfer checkbox to No to an approach with transfer', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockState,
        permit: {
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            CALCULATION_CO2: {
              ...mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION_CO2,
              hasTransfer: true,
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: {
                    transfer: {
                      entryAccountingForTransfer: false,
                    },
                  },
                },
              ],
            } as CalculationOfCO2MonitoringApproach,
            TRANSFERRED_CO2_N2O: {
              type: 'TRANSFERRED_CO2_N2O',
            },
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should delete TRANSFERRED_CO2_N2O approach and transfer from Calculation approach, submit a valid form, update the store', () => {
      page.hasTransferCalculationCO2NoValue.click();
      fixture.detectChanges();

      tasksService.processRequestTaskAction.mockReturnValue(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      const selectedMonitoringApproaches = {
        CALCULATION_CO2: {
          ...mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION_CO2,
          hasTransfer: false,
          sourceStreamCategoryAppliedTiers: [
            {
              sourceStreamCategory: {},
            },
          ],
        } as CalculationOfCO2MonitoringApproach,
      };

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(2);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild({ monitoringApproaches: selectedMonitoringApproaches }, { monitoringApproaches: [false] }),
      );
    });
  });
});
