import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { inspectionRespondMockState, mockInspectionRespondRequestTaskPayload } from '@tasks/inspection/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';

import { TaskListResponseContainerComponent } from './task-list-response-container.component';

describe('TaskListResponseContainerComponent', () => {
  let component: TaskListResponseContainerComponent;
  let fixture: ComponentFixture<TaskListResponseContainerComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const pageTitle = 'Page title 1';
  const activatedRoute = new ActivatedRouteStub({ taskId: 1, type: 'audit', id: '0' }, null, {
    pageTitle,
  });

  class Page extends BasePage<TaskListResponseContainerComponent> {
    get heading(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get operatorText(): HTMLDivElement {
      return this.query<HTMLDivElement>('app-task-header-info > div:nth-child(1)');
    }

    get taskList() {
      return this.queryAll('#taskList li')
        .map((row) => [
          row.querySelector('a') || row.querySelector('.app-task-list__task-name span'),
          row.querySelector('govuk-tag'),
          row.querySelector('p'),
        ])
        .map((pair) => pair.map((element) => element?.textContent?.trim() || ''));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskListResponseContainerComponent],
      providers: [{ provide: ActivatedRoute, useValue: activatedRoute }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(inspectionRespondMockState);

    fixture = TestBed.createComponent(TaskListResponseContainerComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading).toBeTruthy();
    expect(page.heading.textContent.trim()).toEqual(pageTitle);
    expect(page.operatorText.textContent.trim()).toEqual(
      'Assigned to: ' + inspectionRespondMockState.requestTaskItem.requestTask.assigneeFullName,
    );
    expect(page.taskList).toHaveLength(3);
    expect(page.taskList).toEqual([
      [
        'Follow-up action 1',
        'completed',
        mockInspectionRespondRequestTaskPayload.installationInspection.followUpActions[0].explanation,
      ],
      [
        'Follow-up action 2',
        'in progress',
        mockInspectionRespondRequestTaskPayload.installationInspection.followUpActions[1].explanation,
      ],
      ['Send to the regulator', 'cannot start yet', ''],
    ]);
  });
});
