import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { mockNerReviewPostBuild, mockNerSubmitStateBuild } from '@tasks/ner/test';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { NerReviewOutcomeSummaryComponent } from './review-outcome-summary.component';

describe('SummaryComponent', () => {
  let component: NerReviewOutcomeSummaryComponent;
  let fixture: ComponentFixture<NerReviewOutcomeSummaryComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub({ taskId: 1 });
  const initiateProperties = () => {
    component.isEditable = true;
    component.hideSubmit = false;
    component.outcome = { notes: 'A note', opinion: 'PROCEED_TO_AUTHORITY' };
    component.nerFile = { downloadUrl: '/tasks/1/file-download/', fileName: 'Test1.txt' };
    component.supportingFiles = [{ downloadUrl: '/tasks/1/file-download/', fileName: 'Test2.txt' }];
  };

  class Page extends BasePage<NerReviewOutcomeSummaryComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NerReviewOutcomeSummaryComponent],
      providers: [
        CapitalizeFirstPipe,
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockNerSubmitStateBuild({
        regulatorReviewOutcome: {
          notes: 'A note',
          opinion: 'PROCEED_TO_AUTHORITY',
          nerFile: '22222222-2222-4222-a222-222222222222',
          supportingFiles: ['11111111-1111-4111-a111-111111111111'],
        },
        regulatorReviewAttachments: {
          '11111111-1111-4111-a111-111111111111': 'Test1.txt',
          '22222222-2222-4222-a222-222222222222': 'Test2.txt',
        },
        regulatorReviewSectionsCompleted: { OUTCOME: false },
      }),
    );

    fixture = TestBed.createComponent(NerReviewOutcomeSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    initiateProperties();
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements ', () => {
    expect(page.pageContents).toEqual([
      'What is your opinion on new entrant reserve application?',
      'Regulator will send NER application to the UK ETS Authority for final assessment',
      'Change  review opinion for new entrant reserve review outcome',
      'Review notes (not visible to the operator)',
      'A note',
      'Change  review notes for new entrant reserve review outcome',
      'Uploaded new entrant reserve',
      'Test1.txt',
      'Change  uploaded new entrant reserve file',
      'Uploaded supporting files',
      'Test2.txt',
      'Change  uploaded supporting files for new entrant reserve',
    ]);
  });

  it('should submit and navigate to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockNerReviewPostBuild(
        {
          regulatorReviewOutcome: {
            notes: 'A note',
            opinion: 'PROCEED_TO_AUTHORITY',
            nerFile: '22222222-2222-4222-a222-222222222222',
            supportingFiles: ['11111111-1111-4111-a111-111111111111'],
          },
        },
        {
          OUTCOME: true,
        },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
  });
});
