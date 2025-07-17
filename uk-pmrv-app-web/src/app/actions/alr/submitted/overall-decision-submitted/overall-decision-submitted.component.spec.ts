import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { alrSubmittedRequestActionPayload } from '@actions/alr/testing/mock-alr-submitted';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { AlrOverallDecisionSubmittedComponent } from './overall-decision-submitted.component';

describe('OverallDecisionSubmittedComponent', () => {
  let component: AlrOverallDecisionSubmittedComponent;
  let fixture: ComponentFixture<AlrOverallDecisionSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<AlrOverallDecisionSubmittedComponent> {
    get summaryTemplate() {
      return this.query('app-shared-overall-decision-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrOverallDecisionSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'ALR_APPLICATION_VERIFICATION_SUBMITTED',
        submitter: '123',
        payload: {
          ...alrSubmittedRequestActionPayload,
          payloadType: 'ALR_APPLICATION_SENT_TO_VERIFIER_PAYLOAD',
          verificationReport: { overallAssessment: { type: 'VERIFIED_AS_SATISFACTORY' } },
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(AlrOverallDecisionSubmittedComponent);
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
