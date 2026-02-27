import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { BDRS2BaselineSummaryTemplateComponent } from '@shared/components/bdrs2/baseline-summary-template/baseline-summary-template.component';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { BaselineReviewComponent } from './baseline-review.component';

describe('BaselineReviewComponent', () => {
  let component: BaselineReviewComponent;
  let fixture: ComponentFixture<BaselineReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<BaselineReviewComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get baselineSummary() {
      return this.query<HTMLDivElement>('app-bdrs2-baseline-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BDRS2BaselineSummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(BaselineReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Review the stage 2 baseline data report and details');
    expect(page.baselineSummary.textContent).toBeTruthy();
  });
});
