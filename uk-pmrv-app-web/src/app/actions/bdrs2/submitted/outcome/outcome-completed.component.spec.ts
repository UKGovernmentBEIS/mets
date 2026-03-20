import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { bdrs2SubmittedRequestActionPayload } from '@actions/bdrs2/testing/mock-bdrs2-submitted';
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
        type: 'BDRS2_APPLICATION_COMPLETED',
        submitter: '123',
        payload: {
          ...bdrs2SubmittedRequestActionPayload,
          regulatorReviewOutcome: {
            freeAllocationOpinion: 'SENT_TO_AUTHORITY',
            freeAllocationReviewNotes: {
              operatorNotes: 'The free allocation application meets the eligibility criteria.',
              internalNotes: 'No issues identified during regulator review.',
            },
            covidAdjustmentsOpinion: 'SENT_TO_AUTHORITY',
            covidAdjustmentsReviewNotes: {
              operatorNotes: 'COVID adjustments are justified and correctly calculated.',
              internalNotes: 'Checked against supporting evidence.',
            },
            installationSectorOpinion: 'IN_SCOPE_OF_CBAM',
            installationSectorReviewNotes: {
              operatorNotes: 'Installation falls within CBAM scope.',
              internalNotes: 'Sector classification confirmed.',
            },
            cbamSplitOpinion: 'SENT_TO_AUTHORITY',
            cbamSplitReviewNotes: {
              operatorNotes: 'Additional CBAM sub-installation splits are required and correctly defined.',
              internalNotes: 'Splits align with CBAM guidance.',
            },
            file: '22222222-2222-4222-a222-222222222222',
            supportingFiles: ['11111111-1111-4111-a111-111111111111'],
          },
          regulatorReviewAttachments: { 'ff4afbde-d513-4cfc-8ea5-fa9f2b68a1c3': 'test.txt' },
          payloadType: 'BDRS2_APPLICATION_COMPLETED_PAYLOAD',
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
