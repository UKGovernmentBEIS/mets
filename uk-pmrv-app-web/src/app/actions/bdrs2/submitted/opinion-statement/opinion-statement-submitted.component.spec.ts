import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { bdrs2VerificationSubmittedRequestActionPayload } from '@actions/bdrs2/testing/mock-bdrs2-submitted';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { Bdrs2OpinionStatementSubmittedComponent } from './opinion-statement-submitted.component';

describe('Bdrs2OpinionStatementSubmittedComponent', () => {
  let component: Bdrs2OpinionStatementSubmittedComponent;
  let fixture: ComponentFixture<Bdrs2OpinionStatementSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<Bdrs2OpinionStatementSubmittedComponent> {
    get baselineSummaryTemplate() {
      return this.query('app-opinion-statement-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Bdrs2OpinionStatementSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'BDRS2_APPLICATION_VERIFICATION_SUBMITTED',
        submitter: '123',
        payload: {
          ...bdrs2VerificationSubmittedRequestActionPayload,
          payloadType: 'BDRS2_APPLICATION_SENT_TO_VERIFIER_PAYLOAD',
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(Bdrs2OpinionStatementSubmittedComponent);
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
