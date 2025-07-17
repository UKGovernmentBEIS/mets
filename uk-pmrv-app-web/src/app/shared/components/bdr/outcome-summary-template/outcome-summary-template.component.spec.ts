import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { OutcomeSummaryComponent } from '../../../../tasks/bdr/review/outcome/outcome-summary/outcome-summary.component';
import { mockState } from '../../../../tasks/bdr/review/testing/mock-state';

describe('OutcomeSummaryTemplateComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: OutcomeSummaryComponent;
  let fixture: ComponentFixture<OutcomeSummaryComponent>;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 1 });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<OutcomeSummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BdrTaskSharedModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestTask: {
          ...mockState.requestTaskItem.requestTask,
          payload: {
            ...mockState.requestTaskItem.requestTask.payload,
            regulatorReviewGroupDecisions: {},
            regulatorReviewSectionsCompleted: { outcome: false },
            regulatorReviewOutcome: {
              freeAllocationNotes: 'My notes',
              freeAllocationNotesOperator: null,
              bdrFile: '22222222-2222-4222-a222-222222222222',
              files: ['11111111-1111-4111-a111-111111111111'],
            },
            regulatorReviewAttachments: {
              '11111111-1111-4111-a111-111111111111': 'file1.txt',
              '22222222-2222-4222-a222-222222222222': 'file2.jpg',
            },
          } as BDRApplicationRegulatorReviewSubmitRequestTaskPayload,
        },
      },
    });
    fixture = TestBed.createComponent(OutcomeSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('Check your answers');
    expect(page.summaryListValues).toBeTruthy();
    expect(page.submitButton).toBeTruthy();
  });

  it('should display the necessary content', () => {
    expect(page.summaryListValues).toEqual([
      ['Is the operator applying for free allocation?', 'No'],
      ['Do you agree with the application?', 'No free allocation application'],
      ['Review notes (not visible to the operator)', 'My notes'],
      ['Is the operator applying for HSE?', 'No'],
      [
        'What is your opinion on the hospital and small emitter application?',
        'No hospital and small emitter application',
      ],
      ['Is the operator applying for USE?', 'No'],
      ['What is your opinion on the ultra small emitter application?', 'No ultra small emitter application'],
      ['Uploaded baseline data report', 'file2.jpg'],
      ['Uploaded supporting files', 'file1.txt'],
    ]);
  });
});
