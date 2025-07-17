import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { PermitApplicationModule } from '@permit-application/permit-application.module';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { mockPostBuild, mockState, mockStateBuild } from '../../testing/mock-state';
import { MmpProceduresSummaryComponent } from './mmp-procedures-summary.component';

describe('MmpProceduresSummaryComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: MmpProceduresSummaryComponent;
  let fixture: ComponentFixture<MmpProceduresSummaryComponent>;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ subInstallationNo: '0' }, {}, { permitTask: 'monitoringMethodologyPlans' });

  class Page extends BasePage<MmpProceduresSummaryComponent> {
    get heading() {
      return this.query<HTMLElement>('.govuk-heading-l');
    }
    get confirmButton() {
      return this.queryAll<HTMLButtonElement>('.govuk-button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, SharedPermitModule, PermitApplicationModule, MmpProceduresSummaryComponent],
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
    fixture = TestBed.createComponent(MmpProceduresSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit task status', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.confirmButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
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
          mmpProcedures: [true],
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../../'], { relativeTo: route, state: { notification: true } });
  });
});
