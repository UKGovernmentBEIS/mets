import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { ALRRegulatorReviewReturnedForAmendsRequestActionPayload } from 'pmrv-api';

import { AlrReturnedForAmendsComponent } from './returned-for-amends.component';

describe('ReturnedForAmendsComponent', () => {
  let component: AlrReturnedForAmendsComponent;
  let fixture: ComponentFixture<AlrReturnedForAmendsComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<AlrReturnedForAmendsComponent> {
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
      imports: [AlrReturnedForAmendsComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'ALR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS',
        submitter: '123',
        payload: {
          payloadType: 'ALR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS_PAYLOAD',
          regulatorReviewGroupDecisions: {
            ALR: {
              reviewDataType: 'ALR_DATA',
              type: 'OPERATOR_AMENDS_NEEDED',
              details: {
                requiredChanges: [
                  { reason: 'Reason 1', files: ['65092804-17c9-41a8-9ee0-4e728046bb3d'] },
                  { reason: 'Reason 2' },
                ],
                verificationRequired: true,
              },
            },
          },
          regulatorReviewAttachments: { '65092804-17c9-41a8-9ee0-4e728046bb3d': 'testFile.txt' },
        } as ALRRegulatorReviewReturnedForAmendsRequestActionPayload,
      },
    });

    fixture = TestBed.createComponent(AlrReturnedForAmendsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Activity level report returned to operator');
    expect(page.summaryListValues).toHaveLength(2);
    expect(page.summaryListValues).toEqual([
      ['Does the operator need to send the amendments to the verifier?', 'Yes'],
      ['Changes required', '1. Reason 1  testFile.txt  2. Reason 2'],
    ]);
  });
});
