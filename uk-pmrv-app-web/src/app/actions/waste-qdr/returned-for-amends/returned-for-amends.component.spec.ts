import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { wasteQdrReturnedForAmendsRequestActionPayload } from '../testing/mock-waste-qdr-submitted';
import { WasteQdrActionReturnedForAmendsComponent } from './returned-for-amends.component';

describe('ReturnedForAmendsComponent', () => {
  let component: WasteQdrActionReturnedForAmendsComponent;
  let fixture: ComponentFixture<WasteQdrActionReturnedForAmendsComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<WasteQdrActionReturnedForAmendsComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WasteQdrActionReturnedForAmendsComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'WASTE_QDR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS',
        submitter: '123',
        payload: {
          ...wasteQdrReturnedForAmendsRequestActionPayload,
          payloadType: 'WASTE_QDR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS_PAYLOAD',
          regulatorReviewAttachments: { '65092804-17c9-41a8-9ee0-4e728046bb3d': 'testFile.txt' },
        },
      },
    });

    fixture = TestBed.createComponent(WasteQdrActionReturnedForAmendsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Quarterly data report returned to operator');
    expect(page.summaryListValues).toHaveLength(1);
    expect(page.summaryListValues).toEqual([['Changes required', '1. Reason 1  testFile.txt  2. Reason 2']]);
  });
});
