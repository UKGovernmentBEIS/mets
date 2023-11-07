import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { EmpRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage } from '@testing';

import { BlockHourProceduresComponent } from './block-hour-procedures.component';

class Page extends BasePage<BlockHourProceduresComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('BlockHourProceduresComponent', () => {
  let component: BlockHourProceduresComponent;
  let fixture: ComponentFixture<BlockHourProceduresComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestActionTaskComponent, EmpReviewDecisionGroupSummaryComponent, BlockHourProceduresComponent],
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
            blockHourMethodProcedures: {
              fuelBurnCalculationTypes: ['NOT_CLEAR_DISTINGUISHION'],
              notClearDistinguishionIcaoAircraftDesignators: ['All'],
              fuelDensity: {
                locationOfRecords: 'Intranet 1',
                procedureReference: 'Reference 1',
                procedureDescription: 'Measuring fuel density',
                procedureDocumentName: 'Document 1',
                responsibleDepartmentOrRole: 'Department or role 1',
              },
              blockHoursMeasurement: {
                locationOfRecords: 'Intranet 2',
                procedureReference: 'Reference 2',
                procedureDescription: 'Measurement of the block hours',
                procedureDocumentName: 'Document 2',
                responsibleDepartmentOrRole: 'Department or role 2',
              },
              fuelUpliftSupplierRecordType: 'FUEL_DELIVERY_NOTES',
            },
          },
        } as EmpRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(BlockHourProceduresComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Block-hour procedures');
    expect(page.summaryValues).toHaveLength(12);
    expect(page.summaryValues).toEqual([
      [
        'Aircraft types using block hour that you cannot clearly distinguish between international and national fuel uplifts on a flight by flight basis',
        'All',
      ],

      ['Procedure description', 'Measurement of the block hours'],
      ['Name of the procedure document', 'Document 2'],
      ['Procedure reference', 'Reference 2'],
      ['Department or role responsible for data maintenance', 'Department or role 2'],
      ['Location of records', 'Intranet 2'],

      ['Records used', 'Fuel delivery notes'],

      ['Procedure description', 'Measuring fuel density'],
      ['Name of the procedure document', 'Document 1'],
      ['Procedure reference', 'Reference 1'],
      ['Department or role responsible for data maintenance', 'Department or role 1'],
      ['Location of records', 'Intranet 1'],
    ]);
  });
});
