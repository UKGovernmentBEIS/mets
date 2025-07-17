import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { TasksService } from 'pmrv-api';

import { mockBdrState } from '../submit/testing/mock-bdr-payload';
import { VerificationSubmitContainerComponent } from './verification-submit-container.component';

describe('VerificationSubmitContainerComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: VerificationSubmitContainerComponent;
  let fixture: ComponentFixture<VerificationSubmitContainerComponent>;

  class Page extends BasePage<VerificationSubmitContainerComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get operatorText(): HTMLDivElement {
      return this.query<HTMLDivElement>('app-task-header-info > div:nth-child(1)');
    }

    get daysRemainingText(): HTMLDivElement {
      return this.query<HTMLDivElement>('app-task-header-info > div:nth-child(2)');
    }

    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerificationSubmitContainerComponent],
      providers: [provideRouter([]), TasksService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockBdrState);
    fixture = TestBed.createComponent(VerificationSubmitContainerComponent);
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
    expect(page.heading1.textContent.trim()).toEqual('Verify 2025 baseline data report');
    expect(page.operatorText.textContent.trim()).toEqual('Assigned to: Operator1 England');
    expect(page.daysRemainingText.textContent.trim()).toEqual('Days Remaining: Overdue');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual([
      'Baseline data report and details',
      'BDR verification opinion statement',
      'Overall decision',
      'Send report',
    ]);
  });
});
