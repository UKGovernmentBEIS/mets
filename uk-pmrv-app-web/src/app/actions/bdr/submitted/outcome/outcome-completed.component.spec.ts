import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { bdrSubmittedRequestActionPayload } from '@actions/bdr/testing/mock-bdr-submitted';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { OutcomeCompletedComponent } from './outcome-completed.component';

describe('OutcomeCompletedComponent', () => {
  let component: OutcomeCompletedComponent;
  let fixture: ComponentFixture<OutcomeCompletedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<OutcomeCompletedComponent> {
    get outcomeSummaryTemplate() {
      return this.query('app-outcome-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OutcomeCompletedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'BDR_APPLICATION_COMPLETED',
        submitter: '123',
        payload: {
          ...bdrSubmittedRequestActionPayload,
          regulatorReviewOutcome: {
            bdrFile: 'ff4afbde-d513-4cfc-8ea5-fa9f2b68a1c3',
            freeAllocationNotes: 'This cannot be viewed by the operator',
            freeAllocationNotesOperator: 'This can be viewed by the operator',
            hasRegulatorSentFreeAllocation: true,
            hasRegulatorSentHSE: true,
            hasRegulatorSentUSE: false,
            useHseNotes: 'This cannot be viewed by the operator',
            useHseNotesOperator: 'This can be viewed by the operator',
          },
          regulatorReviewAttachments: { 'ff4afbde-d513-4cfc-8ea5-fa9f2b68a1c3': 'test.txt' },
          payloadType: 'BDR_APPLICATION_COMPLETED_PAYLOAD',
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(OutcomeCompletedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary HTMLElement', () => {
    expect(page.outcomeSummaryTemplate).toBeTruthy();
  });
});
