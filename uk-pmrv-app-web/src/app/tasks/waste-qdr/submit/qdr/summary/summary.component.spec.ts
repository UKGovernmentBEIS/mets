import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockWasteQdrPostBuild, mockWasteQdrSubmitStateBuild } from '@tasks/waste-qdr/test';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { WasteQdrSummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: WasteQdrSummaryComponent;
  let fixture: ComponentFixture<WasteQdrSummaryComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const currentState = {
    qdr: {
      reportProvided: true,
      report: '22222222-2222-4222-a222-222222222222',
      supportingFiles: ['11111111-1111-4111-a111-111111111111'],
      notes: 'A note',
    },
    wasteQDRAttachments: {
      '11111111-1111-4111-a111-111111111111': 'test1.txt',
      '22222222-2222-4222-a222-222222222222': 'test2.txt',
    },
    wasteQDRSectionsCompleted: { qdr: false },
  };

  const activatedRoute = new ActivatedRouteStub({ taskId: 1 });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<WasteQdrSummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WasteQdrSummaryComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockWasteQdrSubmitStateBuild(currentState));

    fixture = TestBed.createComponent(WasteQdrSummaryComponent);
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
      'Will you provide a quarterly data report for',
      'Yes',
      'Change',
      'Completed quarterly report',
      'test2.txt',
      'Change',
      'Supporting data',
      'test1.txt',
      'Change',
      'Notes',
      'A note',
      'Change',
    ]);
  });

  it('should submit and navigate to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockWasteQdrPostBuild(
        {
          qdr: { ...currentState.qdr },
        },
        {
          qdr: true,
        },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../../'], { relativeTo: activatedRoute });
  });
});
