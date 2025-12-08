import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { wasteQdrSubmitMockState } from '@tasks/waste-qdr/test';
import { BasePage } from '@testing';

import { WasteQdrTaskListComponent } from './task-list.component';

describe('TaskListComponent', () => {
  let component: WasteQdrTaskListComponent;
  let fixture: ComponentFixture<WasteQdrTaskListComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<WasteQdrTaskListComponent> {
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
      imports: [WasteQdrTaskListComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(wasteQdrSubmitMockState);

    fixture = TestBed.createComponent(WasteQdrTaskListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading).toEqual('Complete July to September 2025 quarterly data report');
    expect(page.taskList).toEqual(['Quarterly data report', 'Send to regulator']);
  });
});
