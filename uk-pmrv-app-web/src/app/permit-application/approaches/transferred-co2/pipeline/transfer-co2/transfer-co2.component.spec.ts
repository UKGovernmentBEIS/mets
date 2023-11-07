import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService, TransferredCO2AndN2OMonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { PermitApplicationState } from '../../../../../permit-application/store/permit-application.state';
import { PermitIssuanceStore } from '../../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { TransferredCO2Module } from '../../transferred-co2.module';
import { TransferCo2Component } from './transfer-co2.component';

describe('TransferCo2Component', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: TransferCo2Component;
  let fixture: ComponentFixture<TransferCo2Component>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      taskKey:
        'monitoringApproaches.TRANSFERRED_CO2_N2O.transportCO2AndN2OPipelineSystems.proceduresForTransferredCO2AndN2O',
      statusKey: 'TRANSFERRED_CO2_Pipeline',
    },
  );
  const mockTransfer = (
    mockState.permit.monitoringApproaches.TRANSFERRED_CO2_N2O as TransferredCO2AndN2OMonitoringApproach
  ).transportCO2AndN2OPipelineSystems.proceduresForTransferredCO2AndN2O;

  class Page extends BasePage<TransferCo2Component> {
    get description(): string {
      return this.getInputValue('#proceduresForTransferredCO2AndN2O.procedureDescription');
    }
    set description(value: string) {
      this.setInputValue('#proceduresForTransferredCO2AndN2O.procedureDescription', value);
    }

    get name(): string {
      return this.getInputValue('#proceduresForTransferredCO2AndN2O.procedureDocumentName');
    }
    set name(value: string) {
      this.setInputValue('#proceduresForTransferredCO2AndN2O.procedureDocumentName', value);
    }

    get reference(): string {
      return this.getInputValue('#proceduresForTransferredCO2AndN2O.procedureReference');
    }
    set reference(value: string) {
      this.setInputValue('#proceduresForTransferredCO2AndN2O.procedureReference', value);
    }

    get department(): string {
      return this.getInputValue('#proceduresForTransferredCO2AndN2O.responsibleDepartmentOrRole');
    }
    set department(value: string) {
      this.setInputValue('#proceduresForTransferredCO2AndN2O.responsibleDepartmentOrRole', value);
    }

    get location(): string {
      return this.getInputValue('#proceduresForTransferredCO2AndN2O.locationOfRecords');
    }
    set location(value: string) {
      this.setInputValue('#proceduresForTransferredCO2AndN2O.locationOfRecords', value);
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

  const tasksService = mockClass(TasksService);
  const createComponent = () => {
    fixture = TestBed.createComponent(TransferCo2Component);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, TransferredCO2Module],
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

  describe('for new transfer', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: { TRANSFERRED_CO2_N2O: { transportCO2AndN2OPipelineSystems: undefined } },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form', () => {
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

      page.description = mockTransfer.procedureDescription;
      page.name = mockTransfer.procedureDocumentName;
      page.reference = mockTransfer.procedureReference;
      page.department = mockTransfer.responsibleDepartmentOrRole;
      page.location = mockTransfer.locationOfRecords;

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
              TRANSFERRED_CO2_N2O: {
                transportCO2AndN2OPipelineSystems: {
                  proceduresForTransferredCO2AndN2O: {
                    appliedStandards: null,
                    diagramReference: null,
                    itSystemUsed: null,
                    procedureDescription: mockTransfer.procedureDescription,
                    procedureDocumentName: mockTransfer.procedureDocumentName,
                    procedureReference: mockTransfer.procedureReference,
                    responsibleDepartmentOrRole: mockTransfer.responsibleDepartmentOrRole,
                    locationOfRecords: mockTransfer.locationOfRecords,
                  },
                },
              },
            },
          },
          {
            TRANSFERRED_CO2_Pipeline: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });
  });
});
