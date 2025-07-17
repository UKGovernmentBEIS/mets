import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { MethodBProceduresComponent } from '@aviation/request-action/emp/shared/tasks/method-b-procedures/method-b-procedures.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { EmpRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage } from '@testing';

class Page extends BasePage<MethodBProceduresComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('MethodBProceduresComponent', () => {
  let component: MethodBProceduresComponent;
  let fixture: ComponentFixture<MethodBProceduresComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestActionTaskComponent, EmpReviewDecisionGroupSummaryComponent, MethodBProceduresComponent],
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
          emissionsMonitoringPlan: {
            methodBProcedures: {
              fuelConsumptionPerFlight: {
                locationOfRecords: 'Intranet 1',
                procedureReference: 'Reference 1',
                procedureDescription: 'Description 1',
                procedureDocumentName: 'Document 1',
                responsibleDepartmentOrRole: 'Department or role 1',
              },
              fuelDensity: {
                locationOfRecords: 'Intranet 2',
                procedureReference: 'Reference 2',
                procedureDescription: 'Description 2',
                procedureDocumentName: 'Document 2',
                responsibleDepartmentOrRole: 'Department or role 2',
              },
            },
          },
        } as EmpRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(MethodBProceduresComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Method B procedures');
    expect(page.summaryValues).toHaveLength(10);
    expect(page.summaryValues).toEqual([
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
    ]);
  });
});
