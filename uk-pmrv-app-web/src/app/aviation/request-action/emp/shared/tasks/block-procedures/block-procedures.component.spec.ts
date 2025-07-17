import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { BlockProceduresComponent } from '@aviation/request-action/emp/shared/tasks/block-procedures/block-procedures.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { EmpRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage } from '@testing';

class Page extends BasePage<BlockProceduresComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('BlockProceduresComponent', () => {
  let component: BlockProceduresComponent;
  let fixture: ComponentFixture<BlockProceduresComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestActionTaskComponent, EmpReviewDecisionGroupSummaryComponent, BlockProceduresComponent],
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
            blockOnBlockOffMethodProcedures: {
              fuelConsumptionPerFlight: {
                locationOfRecords: 'Intranet',
                procedureReference: 'Reference',
                procedureDescription: 'Description',
                procedureDocumentName: 'Document',
                responsibleDepartmentOrRole: 'Department or role',
              },
            },
          },
        } as EmpRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(BlockProceduresComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Block-off / Block-on procedures');
    expect(page.summaryValues).toHaveLength(5);
    expect(page.summaryValues).toEqual([
      ['Procedure description', 'Description'],
      ['Name of the procedure document', 'Document'],
      ['Procedure reference', 'Reference'],
      ['Department or role responsible for data maintenance', 'Department or role'],
      ['Location of records', 'Intranet'],
    ]);
  });
});
