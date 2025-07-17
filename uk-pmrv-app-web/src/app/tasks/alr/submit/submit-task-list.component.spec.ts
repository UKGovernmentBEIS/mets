import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { alrSubmitMockState } from '@tasks/alr/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { AlrTaskListComponent } from './submit-task-list.component';

describe('TaskListComponent', () => {
  let component: AlrTaskListComponent;
  let fixture: ComponentFixture<AlrTaskListComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<AlrTaskListComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get taskList() {
      return this.queryAll('.govuk-grid-column-full > ul.app-task-list__items .app-task-list__task-name').map((el) =>
        el.textContent.trim(),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrTaskListComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(alrSubmitMockState);

    fixture = TestBed.createComponent(AlrTaskListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading).toEqual('Complete 2022 activity level report');
    expect(page.taskList).toEqual(['Provide the activity level report', 'Send report']);
  });
});
