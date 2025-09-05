import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { ActivitySummaryTemplateComponent } from '@shared/components/alr';
import { alrMockReviewState } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { AlrOverallDecisionReviewComponent } from './overall-decision-review.component';

describe('AlrOverallDecisionReviewComponent', () => {
  let component: AlrOverallDecisionReviewComponent;
  let fixture: ComponentFixture<AlrOverallDecisionReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<AlrOverallDecisionReviewComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get overallDecisionSummary() {
      return this.query<HTMLDivElement>('app-shared-overall-decision-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivitySummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(alrMockReviewState);

    fixture = TestBed.createComponent(AlrOverallDecisionReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Review the overall decision');
    expect(page.overallDecisionSummary.textContent).toBeTruthy();
  });
});
