import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { BaselineSummaryTemplateComponent } from '@shared/components/bdr/baseline-summary-template/baseline-summary-template.component';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { BdrOpinionStatementReviewComponent } from '@tasks/bdr/review/opinion-statement/opinion-statement-review.component';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

describe('BdrOpinionStatementReviewComponent', () => {
  let component: BdrOpinionStatementReviewComponent;
  let fixture: ComponentFixture<BdrOpinionStatementReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<BdrOpinionStatementReviewComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get opinionStatementSummary() {
      return this.query<HTMLDivElement>('app-opinion-statement-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BaselineSummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(BdrOpinionStatementReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Review the BDR verification opinion statement');
    expect(page.opinionStatementSummary.textContent).toBeTruthy();
  });
});
