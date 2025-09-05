import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { ActivitySummaryTemplateComponent } from '@shared/components/alr';
import { alrMockReviewState } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { ActivityReviewComponent } from './activity-review.component';

describe('ActivityReviewComponent', () => {
  let component: ActivityReviewComponent;
  let fixture: ComponentFixture<ActivityReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<ActivityReviewComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get baselineSummary() {
      return this.query<HTMLDivElement>('app-alr-activity-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivitySummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(alrMockReviewState);

    fixture = TestBed.createComponent(ActivityReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Review the activity level report and details');
    expect(page.baselineSummary.textContent).toBeTruthy();
  });
});
