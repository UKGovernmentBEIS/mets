import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { mockNerPostBuild, mockNerSubmitStateBuild } from '@tasks/ner/test';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { NerApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { NerDetailsSummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: NerDetailsSummaryComponent;
  let fixture: ComponentFixture<NerDetailsSummaryComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const currentPayload = {
    ner: {
      nerFiles: {
        file: '22222222-2222-4222-a222-222222222222',
        supportingFiles: ['11111111-1111-4111-a111-111111111111'],
      },
      mmpFiles: {
        file: '33333333-3333-4222-a222-333333333333',
        supportingFiles: ['44444444-4444-4111-a111-444444444444'],
      },
      notes: 'A note',
    },
    nerAttachments: {
      '11111111-1111-4111-a111-111111111111': 'test1.txt',
      '22222222-2222-4222-a222-222222222222': 'test2.txt',
      '33333333-3333-4222-a222-333333333333': 'test3.txt',
      '44444444-4444-4111-a111-444444444444': 'test4.txt',
    },
    nerSectionsCompleted: { details: false },
  } as NerApplicationSubmitRequestTaskPayload;

  const activatedRoute = new ActivatedRouteStub({ taskId: 1 });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<NerDetailsSummaryComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NerDetailsSummaryComponent],
      providers: [
        CapitalizeFirstPipe,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockNerSubmitStateBuild(currentPayload));

    fixture = TestBed.createComponent(NerDetailsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements ', () => {
    expect(page.pageContents).toEqual([
      'Uploaded new entrant reserve',
      'test2.txt',
      'Change  uploaded new entrant reserve file',
      'Uploaded supporting files',
      'test1.txt',
      'Change  uploaded supporting files for new entrant reserve',
      'Notes',
      'A note',
      'Change  notes for new entrant reserve',
      'Uploaded monitoring methodology plan',
      'test3.txt',
      'Change  uploaded monitoring methodology plan file',
      'Uploaded supporting files',
      'test4.txt',
      'Change  uploaded supporting files for monitoring methodology plan',
    ]);
  });

  it('should submit and navigate to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockNerPostBuild(
        {
          ner: { ...currentPayload.ner },
        },
        {
          details: true,
        },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../../'], { relativeTo: activatedRoute });
  });
});
