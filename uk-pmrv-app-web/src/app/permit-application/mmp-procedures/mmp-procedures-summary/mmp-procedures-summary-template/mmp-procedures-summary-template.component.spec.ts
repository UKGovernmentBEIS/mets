import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { PermitApplicationModule } from '@permit-application/permit-application.module';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockState, mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { MmpProceduresSummaryTemplateComponent } from './mmp-procedures-summary-template.component';

describe('MmpProceduresSummaryTemplateComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: MmpProceduresSummaryTemplateComponent;
  let fixture: ComponentFixture<MmpProceduresSummaryTemplateComponent>;
  let page: Page;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ subInstallationNo: '0' }, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<MmpProceduresSummaryTemplateComponent> {
    get heading() {
      return this.query<HTMLElement>('.govuk-heading-l');
    }
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, SharedPermitModule, PermitApplicationModule, MmpProceduresSummaryTemplateComponent],
      providers: [
        provideRouter([]),
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitIssuanceStore);
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: {
              procedures: {
                ASSIGNMENT_OF_RESPONSIBILITIES: {
                  procedureName: 'procedure name',
                  procedureReference: 'procedure reference',
                  dataMaintenanceResponsibleEntity: null,
                  diagramReference: null,
                  itSystemUsed: null,
                  locationOfRecords: null,
                  procedureDescription: null,
                  standardsAppliedList: null,
                },
                MONITORING_PLAN_APPROPRIATENESS: {
                  procedureName: 'procedure name',
                  procedureReference: 'procedure reference',
                  dataMaintenanceResponsibleEntity: null,
                  diagramReference: null,
                  itSystemUsed: null,
                  locationOfRecords: null,
                  procedureDescription: null,
                  standardsAppliedList: null,
                },
                DATA_FLOW_ACTIVITIES: {
                  procedureName: 'procedure name',
                  procedureReference: 'procedure reference',
                  dataMaintenanceResponsibleEntity: null,
                  diagramReference: null,
                  itSystemUsed: null,
                  locationOfRecords: null,
                  procedureDescription: null,
                  standardsAppliedList: null,
                },
                CONTROL_ACTIVITIES: {
                  procedureName: 'procedure name',
                  procedureReference: 'procedure reference',
                  dataMaintenanceResponsibleEntity: null,
                  diagramReference: null,
                  itSystemUsed: null,
                  locationOfRecords: null,
                  procedureDescription: null,
                  standardsAppliedList: null,
                },
              },
            },
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          mmpProcedures: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(MmpProceduresSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary for procedures', () => {
    expect(page.answers).toEqual([
      ['Name of the procedure', 'procedure name'],
      ['Procedure reference', 'procedure reference'],
      ['Diagram reference', ''],
      ['Procedure description', ''],
      ['Department or role responsible for data maintenance', ''],
      ['Location of records', ''],
      ['IT system used', ''],
      ['List of EN or other standards applied', ''],
      ['Name of the procedure', 'procedure name'],
      ['Procedure reference', 'procedure reference'],
      ['Diagram reference', ''],
      ['Procedure description', ''],
      ['Department or role responsible for data maintenance', ''],
      ['Location of records', ''],
      ['IT system used', ''],
      ['List of EN or other standards applied', ''],
      ['Name of the procedure', 'procedure name'],
      ['Procedure reference', 'procedure reference'],
      ['Diagram reference', ''],
      ['Procedure description', ''],
      ['Department or role responsible for data maintenance', ''],
      ['Location of records', ''],
      ['IT system used', ''],
      ['List of EN or other standards applied', ''],
      ['Name of the procedure', 'procedure name'],
      ['Procedure reference', 'procedure reference'],
      ['Diagram reference', ''],
      ['Procedure description', ''],
      ['Department or role responsible for data maintenance', ''],
      ['Location of records', ''],
      ['IT system used', ''],
      ['List of EN or other standards applied', ''],
    ]);
  });
});
