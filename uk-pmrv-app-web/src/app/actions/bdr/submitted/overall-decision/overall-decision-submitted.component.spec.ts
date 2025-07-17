import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { bdrVerificationSubmittedRequestActionPayload } from '@actions/bdr/testing/mock-bdr-submitted';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { BdrOverallDecisionSubmittedComponent } from './overall-decision-submitted.component';

describe('OverallDecisionSubmittedComponent', () => {
  let component: BdrOverallDecisionSubmittedComponent;
  let fixture: ComponentFixture<BdrOverallDecisionSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<BdrOverallDecisionSubmittedComponent> {
    get baselineSummaryTemplate() {
      return this.query('app-shared-overall-decision-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BdrOverallDecisionSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'BDR_APPLICATION_VERIFICATION_SUBMITTED',
        submitter: '123',
        payload: {
          ...bdrVerificationSubmittedRequestActionPayload,
          payloadType: 'BDR_APPLICATION_SENT_TO_VERIFIER_PAYLOAD',
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(BdrOverallDecisionSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary HTMLElement', () => {
    expect(page.baselineSummaryTemplate).toBeTruthy();
  });
});
