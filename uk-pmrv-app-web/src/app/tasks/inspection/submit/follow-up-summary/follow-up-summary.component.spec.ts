import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { InspectionSubmitRequestTaskPayload } from '@tasks/inspection/shared/inspection';
import { inspectionSubmitMockState } from '@tasks/inspection/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';

import { FollowUpSummaryComponent } from './follow-up-summary.component';

describe('FollowUpSummaryComponent', () => {
  let page: Page;
  let component: FollowUpSummaryComponent;
  let fixture: ComponentFixture<FollowUpSummaryComponent>;
  let store: CommonTasksStore;

  class Page extends BasePage<FollowUpSummaryComponent> {
    get header(): string {
      return this.query('app-page-heading').textContent.trim();
    }

    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FollowUpSummaryComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }, TaskTypeToBreadcrumbPipe],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(inspectionSubmitMockState);

    fixture = TestBed.createComponent(FollowUpSummaryComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);

    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toBeTruthy();
    expect(page.summaryValues).toEqual([
      ['Do you want to add follow-up actions for the operator?', 'Yes'],
      ['Action type', 'Non-conformity'],
      ['Explanation', 'Reiciendis nulla qua'],
      ['Uploaded files', 'aircraftdata2 1.csv'],
      ['Action type', 'Non-conformity'],
      ['Explanation', 'Vitae facere est as'],
      ['Response deadline', '20 February 2026'],
    ]);
  });

  it('should show task details for required false', () => {
    store.setState({
      ...inspectionSubmitMockState,
      requestTaskItem: {
        ...inspectionSubmitMockState.requestTaskItem,
        requestTask: {
          ...inspectionSubmitMockState.requestTaskItem.requestTask,
          payload: {
            ...inspectionSubmitMockState.requestTaskItem.requestTask.payload,
            installationInspection: {
              ...(inspectionSubmitMockState.requestTaskItem.requestTask.payload as any)?.installationInspection,
              followUpActionsRequired: false,
              followUpActionsOmissionFiles: [],
              followUpActionsOmissionJustification: 'text',
              followUpActions: [],
            },
          } as InspectionSubmitRequestTaskPayload,
        },
      },
    });
    fixture.detectChanges();

    expect(page.header).toBeTruthy();
    expect(page.summaryValues).toEqual([
      ['Do you want to add follow-up actions for the operator?', 'No'],
      ['Justification', 'text'],
    ]);
  });

  it('should show task details for required false', () => {
    store.setState({
      ...inspectionSubmitMockState,
      requestTaskItem: {
        ...inspectionSubmitMockState.requestTaskItem,
        requestTask: {
          ...inspectionSubmitMockState.requestTaskItem.requestTask,
          payload: {
            ...inspectionSubmitMockState.requestTaskItem.requestTask.payload,
            installationInspection: {
              ...(inspectionSubmitMockState.requestTaskItem.requestTask.payload as any)?.installationInspection,
              followUpActionsRequired: true,
              followUpActionsOmissionFiles: [],
              followUpActionsOmissionJustification: null,
            },
          } as InspectionSubmitRequestTaskPayload,
        },
      },
    });
    fixture.detectChanges();

    expect(page.header).toBeTruthy();
    expect(page.summaryValues).toEqual([
      ['Do you want to add follow-up actions for the operator?', 'Yes'],
      ['Action type', 'Non-conformity'],
      ['Explanation', 'Reiciendis nulla qua'],
      ['Uploaded files', 'aircraftdata2 1.csv'],
      ['Action type', 'Non-conformity'],
      ['Explanation', 'Vitae facere est as'],
      ['Response deadline', '20 February 2026'],
    ]);
  });
});
