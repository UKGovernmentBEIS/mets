import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { BDRS2BaselineSummaryTemplateComponent } from '@shared/components/bdrs2/baseline-summary-template/baseline-summary-template.component';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { BdrS2OpinionStatementReviewComponent } from '@tasks/bdrs2/review/opinion-statement/opinion-statement-review.component';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

describe('BdrS2OpinionStatementReviewComponent', () => {
  let component: BdrS2OpinionStatementReviewComponent;
  let fixture: ComponentFixture<BdrS2OpinionStatementReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<BdrS2OpinionStatementReviewComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get opinionStatementSummary() {
      return this.query<HTMLDivElement>('app-opinion-statement-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BDRS2BaselineSummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(BdrS2OpinionStatementReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Review the stage 2 BDR verification opinion statement');
    expect(page.opinionStatementSummary.textContent).toBeTruthy();
  });
});
