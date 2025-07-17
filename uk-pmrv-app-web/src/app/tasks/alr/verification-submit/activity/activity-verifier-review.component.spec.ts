import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { ActivitySummaryTemplateComponent } from '@shared/components/alr/activity-summary-template/activity-summary-template.component';
import { alrMockVerificationState } from '@tasks/alr/test/mock-verifier';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { ActivityVerifierReviewComponent } from './activity-verifier-review.component';

describe('ActivityVerifierReviewComponent', () => {
  let component: ActivityVerifierReviewComponent;
  let fixture: ComponentFixture<ActivityVerifierReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<ActivityVerifierReviewComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get activitySummary() {
      return this.query<HTMLDivElement>('app-alr-activity-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivitySummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(alrMockVerificationState);

    fixture = TestBed.createComponent(ActivityVerifierReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Activity level report');
    expect(page.activitySummary.textContent).toBeTruthy();
  });
});
