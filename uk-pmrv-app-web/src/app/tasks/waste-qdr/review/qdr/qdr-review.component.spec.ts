import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SummaryTemplateComponent } from '@shared/components/waste-qdr/summary-template/summary-template.component';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { wasteQdrMockReviewState } from '@tasks/waste-qdr/test/mock-review';
import { BasePage } from '@testing';

import { QdrReviewComponent } from './qdr-review.component';

describe('QdrReviewComponent', () => {
  let component: QdrReviewComponent;
  let fixture: ComponentFixture<QdrReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<QdrReviewComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get baselineSummary() {
      return this.query<HTMLDivElement>('app-waste-qdr-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(wasteQdrMockReviewState);

    fixture = TestBed.createComponent(QdrReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Quarterly data report');
    expect(page.baselineSummary.textContent).toBeTruthy();
  });
});
