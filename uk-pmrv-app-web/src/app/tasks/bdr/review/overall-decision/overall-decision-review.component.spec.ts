import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { BaselineSummaryTemplateComponent } from '@shared/components/bdr/baseline-summary-template/baseline-summary-template.component';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { BdrOverallDecisionReviewComponent } from '@tasks/bdr/review/overall-decision/overall-decision-review.component';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

describe('BdrOverallDecisionReviewComponent', () => {
  let component: BdrOverallDecisionReviewComponent;
  let fixture: ComponentFixture<BdrOverallDecisionReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<BdrOverallDecisionReviewComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get overallDecisionSummary() {
      return this.query<HTMLDivElement>('app-shared-overall-decision-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BaselineSummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(BdrOverallDecisionReviewComponent);
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
