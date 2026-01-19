import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { bdrs2SubmitMockState } from '@tasks/bdrs2/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { BdrS2TaskSharedModule } from '../shared';
import { SubmitComponent } from './submit.component';

describe('SubmitComponent', () => {
  let component: SubmitComponent;
  let fixture: ComponentFixture<SubmitComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<SubmitComponent> {
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
      imports: [SubmitComponent],
      providers: [provideRouter([]), BdrS2TaskSharedModule],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(bdrs2SubmitMockState);

    fixture = TestBed.createComponent(SubmitComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading).toEqual('Complete 2026 stage 2 baseline data report');
    expect(page.taskList).toEqual(['Provide stage 2 baseline data report', 'Send report']);
  });
});
