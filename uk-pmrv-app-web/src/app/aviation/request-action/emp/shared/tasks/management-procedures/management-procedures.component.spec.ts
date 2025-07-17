import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { EmpRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage } from '@testing';

import { ManagementProceduresComponent } from './management-procedures.component';

class Page extends BasePage<ManagementProceduresComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('ManagementProceduresComponent', () => {
  let component: ManagementProceduresComponent;
  let fixture: ComponentFixture<ManagementProceduresComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestActionTaskComponent, EmpReviewDecisionGroupSummaryComponent, ManagementProceduresComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED',
        creationDate: '2022-11-29T12:12:48.469862Z',
        payload: {
          empAttachments: {
            '4c7478d9-ae7d-420f-968e-4647c91c841c': 'diagramAttachment.png',
            'd492b44e-6c1b-4530-9770-9375dd0e561e': 'riskAssessmentFile.png',
          } as { [key: string]: string },
          emissionsMonitoringPlan: {
            managementProcedures: {
              monitoringReportingRoles: {
                monitoringReportingRoles: [
                  {
                    jobTitle: 'My Job title',
                    mainDuties: 'My Main duties',
                  },
                ],
              },
              recordKeepingAndDocumentation: {
                locationOfRecords: 'Intranet 1',
                procedureReference: 'Reference 1',
                procedureDescription: 'Description 1',
                procedureDocumentName: 'Document 1',
                responsibleDepartmentOrRole: 'Department or role 1',
              },
              assignmentOfResponsibilities: {
                locationOfRecords: 'Intranet 2',
                procedureReference: 'Reference 2',
                procedureDescription: 'Description 2',
                procedureDocumentName: 'Document 2',
                responsibleDepartmentOrRole: 'Department or role 2',
              },
              monitoringPlanAppropriateness: {
                locationOfRecords: 'Intranet 3',
                procedureReference: 'Reference 3',
                procedureDescription: 'Description 3',
                procedureDocumentName: 'Document 3',
                responsibleDepartmentOrRole: 'Department or role 3',
              },
              dataFlowActivities: {
                processingSteps: 'Processing Steps',
                locationOfRecords: 'Intranet 4',
                procedureReference: 'Reference 4',
                procedureDescription: 'Description 4',
                procedureDocumentName: 'Document 4',
                responsibleDepartmentOrRole: 'Department or role 4',
                primaryDataSources: 'Data Sources',
                diagramAttachmentId: '4c7478d9-ae7d-420f-968e-4647c91c841c',
              },
              qaMeteringAndMeasuringEquipment: {
                locationOfRecords: 'Intranet 5',
                procedureReference: 'Reference 5',
                procedureDescription: 'Description 5',
                procedureDocumentName: 'Document 5',
                responsibleDepartmentOrRole: 'Department or role 5',
              },
              dataValidation: {
                locationOfRecords: 'Intranet 6',
                procedureReference: 'Reference 6',
                procedureDescription: 'Description 6',
                procedureDocumentName: 'Document 6',
                responsibleDepartmentOrRole: 'Department or role 6',
              },
              correctionsAndCorrectiveActions: {
                locationOfRecords: 'Intranet 7',
                procedureReference: 'Reference 7',
                procedureDescription: 'Description 7',
                procedureDocumentName: 'Document 7',
                responsibleDepartmentOrRole: 'Department or role 7',
              },
              controlOfOutsourcedActivities: {
                locationOfRecords: 'Intranet 8',
                procedureReference: 'Reference 8',
                procedureDescription: 'Description 8',
                procedureDocumentName: 'Document 8',
                responsibleDepartmentOrRole: 'Department or role 8',
              },
              assessAndControlRisks: {
                locationOfRecords: 'Intranet 9',
                procedureReference: 'Reference 9',
                procedureDescription: 'Description 9',
                procedureDocumentName: 'Document 9',
                responsibleDepartmentOrRole: 'Department or role 9',
              },
              environmentalManagementSystem: {
                exist: true,
                certified: true,
                certificationStandard: 'BS EN ISO14001: 2015',
              },
              riskAssessmentFile: 'd492b44e-6c1b-4530-9770-9375dd0e561e',
            },
          },
        } as EmpRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(ManagementProceduresComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Management procedures');
    expect(page.summaryValues).toHaveLength(53);
    expect(page.summaryValues).toEqual([
      ['My Job title', 'My Main duties'],

      ['Procedure description', 'Description 1'],
      ['Name of the procedure document', 'Document 1'],
      ['Procedure reference', 'Reference 1'],
      ['Department or role responsible for data maintenance', 'Department or role 1'],
      ['Location of records', 'Intranet 1'],

      ['Procedure description', 'Description 2'],
      ['Name of the procedure document', 'Document 2'],
      ['Procedure reference', 'Reference 2'],
      ['Department or role responsible for data maintenance', 'Department or role 2'],
      ['Location of records', 'Intranet 2'],

      ['Procedure description', 'Description 3'],
      ['Name of the procedure document', 'Document 3'],
      ['Procedure reference', 'Reference 3'],
      ['Department or role responsible for data maintenance', 'Department or role 3'],
      ['Location of records', 'Intranet 3'],

      ['Procedure description', 'Description 4'],
      ['Name of the procedure document', 'Document 4'],
      ['Procedure reference', 'Reference 4'],
      ['Department or role responsible for data maintenance', 'Department or role 4'],
      ['Location of records', 'Intranet 4'],
      ['Primary data sources', 'Data Sources'],
      ['Processing steps for each data flow activity', 'Processing Steps'],
      ['Representation of the data flow for the calculation of emissions', 'diagramAttachment.png'],

      ['Procedure description', 'Description 5'],
      ['Name of the procedure document', 'Document 5'],
      ['Procedure reference', 'Reference 5'],
      ['Department or role responsible for data maintenance', 'Department or role 5'],
      ['Location of records', 'Intranet 5'],

      ['Procedure description', 'Description 6'],
      ['Name of the procedure document', 'Document 6'],
      ['Procedure reference', 'Reference 6'],
      ['Department or role responsible for data maintenance', 'Department or role 6'],
      ['Location of records', 'Intranet 6'],

      ['Procedure description', 'Description 7'],
      ['Name of the procedure document', 'Document 7'],
      ['Procedure reference', 'Reference 7'],
      ['Department or role responsible for data maintenance', 'Department or role 7'],
      ['Location of records', 'Intranet 7'],

      ['Procedure description', 'Description 8'],
      ['Name of the procedure document', 'Document 8'],
      ['Procedure reference', 'Reference 8'],
      ['Department or role responsible for data maintenance', 'Department or role 8'],
      ['Location of records', 'Intranet 8'],

      ['Procedure description', 'Description 9'],
      ['Name of the procedure document', 'Document 9'],
      ['Procedure reference', 'Reference 9'],
      ['Department or role responsible for data maintenance', 'Department or role 9'],
      ['Location of records', 'Intranet 9'],

      ['Risk assessment document', 'riskAssessmentFile.png'],

      ['Does the organisation have a documented environmental management system?', 'Yes'],
      ['Is the system externally certified?', 'Yes'],
      ['Standard to which the system is certified', 'BS EN ISO14001: 2015'],
    ]);
  });
});
