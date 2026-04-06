import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { nerSubmitMockState } from '@tasks/ner/test';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { NerTaskListComponent } from './task-list.component';

describe('TaskListComponent', () => {
  let component: NerTaskListComponent;
  let fixture: ComponentFixture<NerTaskListComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<NerTaskListComponent> {
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
      imports: [NerTaskListComponent],
      providers: [provideRouter([]), CapitalizeFirstPipe],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(nerSubmitMockState);

    fixture = TestBed.createComponent(NerTaskListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading).toEqual('Complete new entrant reserve');
    expect(page.taskList).toEqual(['New entrant reserve', 'Send application']);
  });
});
