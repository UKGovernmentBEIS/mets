import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { nerSubmittedRequestActionPayload } from '@actions/ner/testing';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { NerActionOutcomeComponent } from './outcome.component';

describe('OutcomeComponent', () => {
  let component: NerActionOutcomeComponent;
  let fixture: ComponentFixture<NerActionOutcomeComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<NerActionOutcomeComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NerActionOutcomeComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'NER_APPLICATION_DEEMED_WITHDRAWN',
        submitter: '123',
        payload: {
          ...nerSubmittedRequestActionPayload,
          payloadType: 'NER_APPLICATION_DEEM_WITHDRAWN_PAYLOAD',
          regulatorReviewOutcome: {
            notes: 'A note',
            opinion: 'PROCEED_TO_AUTHORITY',
            nerFile: '22222222-2222-4222-a222-222222222222',
            supportingFiles: ['11111111-1111-4111-a111-111111111111'],
          },
          regulatorReviewAttachments: {
            '11111111-1111-4111-a111-111111111111': 'Test1.txt',
            '22222222-2222-4222-a222-222222222222': 'Test2.txt',
          },
          regulatorReviewSectionsCompleted: { OUTCOME: false },
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(NerActionOutcomeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all html elements ', () => {
    expect(page.pageContents).toEqual([
      'What is your opinion on new entrant reserve application?',
      'Regulator will send NER application to the UK ETS Authority for final assessment',
      'Review notes (not visible to the operator)',
      'A note',
      'Uploaded new entrant reserve',
      'Test2.txt',
      'Uploaded supporting files',
      'Test1.txt',
    ]);
  });
});
