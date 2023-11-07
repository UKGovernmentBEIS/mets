import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService, TransferredCO2AndN2OMonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../testing/mock-state';
import { TransferredCO2Module } from '../transferred-co2.module';
import { DeductionsToAmountComponent } from './deductions-to-amount.component';

describe('DeductionsToAmountFormComponent', () => {
  let page: Page;
  let router: Router;
  let component: DeductionsToAmountComponent;
  let store: PermitApplicationStore<PermitApplicationState>;
  let fixture: ComponentFixture<DeductionsToAmountComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2_N2O.deductionsToAmountOfTransferredCO2',
      statusKey: 'transferredCo2Deductions',
    },
  );
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<DeductionsToAmountComponent> {
    get existRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
    }

    set appliedStandards(value: string) {
      this.setInputValue('#procedureForm.appliedStandards', value);
    }

    set diagramReference(value: string) {
      this.setInputValue('#procedureForm.diagramReference', value);
    }

    set itSystemUsed(value: string) {
      this.setInputValue('#procedureForm.itSystemUsed', value);
    }

    set description(value: string) {
      this.setInputValue('#procedureForm.procedureDescription', value);
    }

    set documentName(value: string) {
      this.setInputValue('#procedureForm.procedureDocumentName', value);
    }

    set procedureReference(value: string) {
      this.setInputValue('#procedureForm.procedureReference', value);
    }

    set department(value: string) {
      this.setInputValue('#procedureForm.responsibleDepartmentOrRole', value);
    }

    set location(value: string) {
      this.setInputValue('#procedureForm.locationOfRecords', value);
    }

    get submitButton() {
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, TransferredCO2Module],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(DeductionsToAmountComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for new procedure', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            ...mockPermitApplyPayload.permit.monitoringApproaches,
            TRANSFERRED_CO2_N2O: {
              ...mockPermitApplyPayload.permit.monitoringApproaches.TRANSFERRED_CO2_N2O,
              deductionsToAmountOfTransferredCO2: undefined,
            } as TransferredCO2AndN2OMonitoringApproach,
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
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummaryErrorList).toEqual([
        'Enter a brief description of the procedure',
        'Enter the name of the procedure document',
        'Enter a procedure reference',
        'Enter the name of the department or role responsible',
        'Enter the location of the records',
      ]);

      page.description = 'procedureDescriptionValue';
      page.documentName = 'procedureDocumentNameValue';
      page.procedureReference = 'procedureReferenceValue';
      page.department = 'responsibleDepartmentOrRoleValue';
      page.location = 'locationOfRecordsValue';

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...store.getState().permit,
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              TRANSFERRED_CO2_N2O: {
                ...store.getState().permit.monitoringApproaches.TRANSFERRED_CO2_N2O,
                deductionsToAmountOfTransferredCO2: {
                  exist: true,
                  procedureForm: {
                    procedureDescription: 'procedureDescriptionValue',
                    procedureDocumentName: 'procedureDocumentNameValue',
                    procedureReference: 'procedureReferenceValue',
                    responsibleDepartmentOrRole: 'responsibleDepartmentOrRoleValue',
                    locationOfRecords: 'locationOfRecordsValue',
                    appliedStandards: null,
                    diagramReference: null,
                    itSystemUsed: null,
                  },
                },
              } as TransferredCO2AndN2OMonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            transferredCo2Deductions: [true],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });
  });

  describe('for existing procedure', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);

      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.existRadios[0].click();
      fixture.detectChanges();

      page.appliedStandards = 'appliedStandards';
      page.diagramReference = 'diagramReference';
      page.itSystemUsed = 'itSystemUsed';
      page.description = 'newDescr';
      page.documentName = 'procedureDocumentName';
      page.procedureReference = 'procedureReference';
      page.department = 'responsibleDepartmentOrRole';
      page.location = 'locationOfRecords';

      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...store.getState().permit,
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              TRANSFERRED_CO2_N2O: {
                ...store.getState().permit.monitoringApproaches.TRANSFERRED_CO2_N2O,
                deductionsToAmountOfTransferredCO2: {
                  exist: true,
                  procedureForm: {
                    appliedStandards: 'appliedStandards',
                    diagramReference: 'diagramReference',
                    itSystemUsed: 'itSystemUsed',
                    procedureDescription: 'newDescr',
                    procedureDocumentName: 'procedureDocumentName',
                    procedureReference: 'procedureReference',
                    responsibleDepartmentOrRole: 'responsibleDepartmentOrRole',
                    locationOfRecords: 'locationOfRecords',
                  },
                },
              } as TransferredCO2AndN2OMonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            transferredCo2Deductions: [true],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });
  });
});
