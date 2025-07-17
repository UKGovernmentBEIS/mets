import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';

import { InspectionModule } from '../../inspection.module';
import { inspectionSubmitMockState } from '../../test/mock';
import { TaskListSubmitContainerComponent } from './task-list-submit-container.component';

describe('SubmitContainerComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: TaskListSubmitContainerComponent;
  let fixture: ComponentFixture<TaskListSubmitContainerComponent>;

  const pageTitle = 'Page title 1';
  const activatedRoute = new ActivatedRouteStub({ taskId: 1, type: 'audit', id: '0' }, null, {
    pageTitle: pageTitle,
  });

  class Page extends BasePage<TaskListSubmitContainerComponent> {
    get heading(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get operatorText(): HTMLDivElement {
      return this.query<HTMLDivElement>('app-task-header-info > div:nth-child(1)');
    }

    get taskList() {
      return this.queryAll('.govuk-grid-column-full > ul.app-task-list__items li span a').map((el) =>
        el.textContent.trim(),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InspectionModule],
      providers: [{ provide: ActivatedRoute, useValue: activatedRoute }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(inspectionSubmitMockState);

    fixture = TestBed.createComponent(TaskListSubmitContainerComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading).toBeTruthy();
    expect(page.heading.textContent.trim()).toEqual(pageTitle);
    expect(page.operatorText.textContent.trim()).toEqual(
      'Assigned to: ' + inspectionSubmitMockState.requestTaskItem.requestTask.assigneeFullName,
    );
    expect(page.taskList).toEqual(['Add 2022 audit report details', 'Follow-up actions for the operator']);
  });
});
