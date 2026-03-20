import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { BDRS2BaselineSummaryTemplateComponent } from '@shared/components/bdrs2/baseline-summary-template/baseline-summary-template.component';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { BdrS2OverallDecisionReviewComponent } from '@tasks/bdrs2/review/overall-decision/overall-decision-review.component';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

describe('BdrS2OverallDecisionReviewComponent', () => {
  let component: BdrS2OverallDecisionReviewComponent;
  let fixture: ComponentFixture<BdrS2OverallDecisionReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<BdrS2OverallDecisionReviewComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get overallDecisionSummary() {
      return this.query<HTMLDivElement>('app-shared-overall-decision-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BDRS2BaselineSummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(BdrS2OverallDecisionReviewComponent);
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
