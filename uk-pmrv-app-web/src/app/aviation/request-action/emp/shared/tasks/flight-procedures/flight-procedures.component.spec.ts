import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { EmpRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage } from '@testing';

import { FlightProceduresComponent } from './flight-procedures.component';

class Page extends BasePage<FlightProceduresComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('FlightProceduresComponent', () => {
  let component: FlightProceduresComponent;
  let fixture: ComponentFixture<FlightProceduresComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestActionTaskComponent, EmpReviewDecisionGroupSummaryComponent, FlightProceduresComponent],
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
            flightAndAircraftProcedures: {
              aircraftUsedDetails: {
                locationOfRecords: 'Intranet 1',
                procedureReference: 'Reference 1',
                procedureDescription: 'Details about the aircraft in use',
                procedureDocumentName: 'Document 1',
                responsibleDepartmentOrRole: 'Department or role 1',
              },
              ukEtsFlightsCoveredDetails: {
                locationOfRecords: 'Intranet 2',
                procedureReference: 'Reference 2',
                procedureDescription: 'Details about flights covered by the UK ETS',
                procedureDocumentName: 'Document 2',
                responsibleDepartmentOrRole: 'Department or role 2',
              },
              flightListCompletenessDetails: {
                locationOfRecords: 'Intranet 3',
                procedureReference: 'Reference 3',
                procedureDescription: 'Details about the completeness of the flights list',
                procedureDocumentName: 'Document 3',
                responsibleDepartmentOrRole: 'Department or role 3',
              },
            },
          },
        } as EmpRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(FlightProceduresComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Flights and aircraft monitoring procedures');
    expect(page.summaryValues).toHaveLength(15);
    expect(page.summaryValues).toEqual([
      ['Procedure description', 'Details about the aircraft in use'],
      ['Name of the procedure document', 'Document 1'],
      ['Procedure reference', 'Reference 1'],
      ['Department or role responsible for data maintenance', 'Department or role 1'],
      ['Location of records', 'Intranet 1'],

      ['Procedure description', 'Details about the completeness of the flights list'],
      ['Name of the procedure document', 'Document 3'],
      ['Procedure reference', 'Reference 3'],
      ['Department or role responsible for data maintenance', 'Department or role 3'],
      ['Location of records', 'Intranet 3'],

      ['Procedure description', 'Details about flights covered by the UK ETS'],
      ['Name of the procedure document', 'Document 2'],
      ['Procedure reference', 'Reference 2'],
      ['Department or role responsible for data maintenance', 'Department or role 2'],
      ['Location of records', 'Intranet 2'],
    ]);
  });
});
