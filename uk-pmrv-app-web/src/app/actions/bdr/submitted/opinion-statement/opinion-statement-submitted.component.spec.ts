import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { bdrVerificationSubmittedRequestActionPayload } from '@actions/bdr/testing/mock-bdr-submitted';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { BdrOpinionStatementSubmittedComponent } from './opinion-statement-submitted.component';

describe('OpinionStatementSubmittedComponent', () => {
  let component: BdrOpinionStatementSubmittedComponent;
  let fixture: ComponentFixture<BdrOpinionStatementSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<BdrOpinionStatementSubmittedComponent> {
    get baselineSummaryTemplate() {
      return this.query('app-opinion-statement-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BdrOpinionStatementSubmittedComponent],
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

    fixture = TestBed.createComponent(BdrOpinionStatementSubmittedComponent);
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
