import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { DetailsSummaryTemplateComponent } from '@shared/components/hseti';
import { hsetiMockReviewState } from '@tasks/hseti/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';

import { DetailsReviewComponent } from './details-review.component';

describe('DetailsReviewComponent', () => {
  let component: DetailsReviewComponent;
  let fixture: ComponentFixture<DetailsReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<DetailsReviewComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get detailsSummary() {
      return this.query<HTMLDivElement>('app-hseti-details-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsSummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(hsetiMockReviewState);

    fixture = TestBed.createComponent(DetailsReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Review HSE target increase details');
    expect(page.detailsSummary.textContent).toBeTruthy();
  });
});
