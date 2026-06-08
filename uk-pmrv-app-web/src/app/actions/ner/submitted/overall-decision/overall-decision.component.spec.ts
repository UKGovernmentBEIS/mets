import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { nerSubmittedRequestActionPayload } from '@actions/ner/testing';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { NerOverallDecisionSubmittedComponent } from './overall-decision.component';

describe('OverallDecisionComponent', () => {
  let component: NerOverallDecisionSubmittedComponent;
  let fixture: ComponentFixture<NerOverallDecisionSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<NerOverallDecisionSubmittedComponent> {
    get summaryTemplate() {
      return this.query('app-shared-overall-decision-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NerOverallDecisionSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'NER_APPLICATION_VERIFICATION_SUBMITTED',
        submitter: '123',
        payload: {
          ...nerSubmittedRequestActionPayload,
          payloadType: 'NER_APPLICATION_SENT_TO_VERIFIER_PAYLOAD',
          verificationReport: { overallAssessment: { type: 'VERIFIED_AS_SATISFACTORY' } },
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(NerOverallDecisionSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary HTMLElement', () => {
    expect(page.summaryTemplate).toBeTruthy();
  });
});
