import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { ActivitySummaryTemplateComponent } from '@shared/components/alr';
import { alrMockReviewState } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { AlrOpinionStatementReviewComponent } from './opinion-statement-review.component';

describe('AlrOpinionStatementReviewComponent', () => {
  let component: AlrOpinionStatementReviewComponent;
  let fixture: ComponentFixture<AlrOpinionStatementReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<AlrOpinionStatementReviewComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get opinionStatementSummary() {
      return this.query<HTMLDivElement>('app-opinion-statement-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivitySummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(alrMockReviewState);

    fixture = TestBed.createComponent(AlrOpinionStatementReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Review the activity level report verification opinion statement');
    expect(page.opinionStatementSummary.textContent).toBeTruthy();
  });
});
