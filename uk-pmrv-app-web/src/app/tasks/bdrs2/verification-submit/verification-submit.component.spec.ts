import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { TasksService } from 'pmrv-api';

import { mockBdrS2State } from '../submit/testing/mock-bdrs2-payload';
import { VerificationSubmitComponent } from './verification-submit.component';

describe('VerificationSubmitComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: VerificationSubmitComponent;
  let fixture: ComponentFixture<VerificationSubmitComponent>;

  class Page extends BasePage<VerificationSubmitComponent> {
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
      imports: [VerificationSubmitComponent],
      providers: [provideRouter([]), TasksService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockBdrS2State);
    fixture = TestBed.createComponent(VerificationSubmitComponent);
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
    expect(page.heading1.textContent.trim()).toEqual('Verify 2026 stage 2 baseline data report');
    expect(page.operatorText.textContent.trim()).toEqual('Assigned to: Operator1 England');
    expect(page.daysRemainingText.textContent.trim()).toEqual('Days Remaining: Overdue');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual([
      'Stage 2 baseline data report',
      'Stage 2 BDR verification opinion statement',
      'Overall decision',
      'Send report',
    ]);
  });
});
