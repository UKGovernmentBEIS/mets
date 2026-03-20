import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { OutcomeSummaryComponent } from '@tasks/bdrs2/review/outcome/outcome-summary/outcome-summary.component';
import { mockState } from '@tasks/bdrs2/review/testing/mock-state';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared/bdrs2-task-shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

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
      imports: [BdrS2TaskSharedModule],
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
              freeAllocationOpinion: 'SENT_TO_AUTHORITY',
              freeAllocationReviewNotes: {
                operatorNotes: 'The free allocation application meets the eligibility criteria.',
                internalNotes: 'No issues identified during regulator review.',
              },
              covidAdjustmentsOpinion: 'SENT_TO_AUTHORITY',
              covidAdjustmentsReviewNotes: {
                operatorNotes: 'COVID adjustments are justified and correctly calculated.',
                internalNotes: 'Checked against supporting evidence.',
              },
              installationSectorOpinion: 'IN_SCOPE_OF_CBAM',
              installationSectorReviewNotes: {
                operatorNotes: 'Installation falls within CBAM scope.',
                internalNotes: 'Sector classification confirmed.',
              },
              cbamSplitOpinion: 'SENT_TO_AUTHORITY',
              cbamSplitReviewNotes: {
                operatorNotes: 'Additional CBAM sub-installation splits are required and correctly defined.',
                internalNotes: 'Splits align with CBAM guidance.',
              },
              file: '22222222-2222-4222-a222-222222222222',
              supportingFiles: ['11111111-1111-4111-a111-111111111111'],
            },
            regulatorReviewAttachments: {
              '11111111-1111-4111-a111-111111111111': 'file1.txt',
              '22222222-2222-4222-a222-222222222222': 'file2.jpg',
            },
          } as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload,
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
      [
        'Do you want to continue with your application for free allocation?',
        'Yes, I hold a GHGE permit and want to continue my application for free allocation as a main scheme participant, or I currently hold HSE status and want to become a main scheme participant from 2027 to 2030',
      ],
      [
        'What is your opinion on the free allocation application?',
        'Regulator has sent the free allocation application and explanations to UK ETS authority for assessment',
      ],
      ['Review notes (visible to the operator)', 'The free allocation application meets the eligibility criteria.'],
      ['Review notes (not visible to the operator)', 'No issues identified during regulator review.'],
      ['Are you making COVID adjustments?', 'No'],
      [
        'What is your opinion on the COVID adjustments?',
        'Regulator has sent COVID adjustments to the UK ETS authority for final assessment',
      ],
      ['Review notes (visible to the operator)', 'COVID adjustments are justified and correctly calculated.'],
      ['Review notes (not visible to the operator)', 'Checked against supporting evidence.'],
      ['Is your installation in the aluminium, cement, fertiliser, hydrogen, iron or steel sector?', 'Yes'],
      ['What is your opinion on the installation sector?', 'In scope of CBAM'],
      ['Review notes (visible to the operator)', 'Installation falls within CBAM scope.'],
      ['Review notes (not visible to the operator)', 'Sector classification confirmed.'],
      ['Are additional sub-installation splits required because of the UK CBAM?', 'No'],
      [
        'What is your opinion on sub-installation splits required because of the UK CBAM?',
        'Regulator has sent CBAM classifications to the UK ETS authority for final assessment',
      ],
      [
        'Review notes (visible to the operator)',
        'Additional CBAM sub-installation splits are required and correctly defined.',
      ],
      ['Review notes (not visible to the operator)', 'Splits align with CBAM guidance.'],
      ['Uploaded stage 2 baseline data report', 'file2.jpg'],
      ['Uploaded supporting files', 'file1.txt'],
    ]);
  });
});
