import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { alrSubmittedRequestActionPayload } from '@actions/alr/testing/mock-alr-submitted';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { AlrOpinionStatementSubmittedComponent } from './opinion-statement-submitted.component';

describe('OpinionStatementSubmittedComponent', () => {
  let component: AlrOpinionStatementSubmittedComponent;
  let fixture: ComponentFixture<AlrOpinionStatementSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<AlrOpinionStatementSubmittedComponent> {
    get summaryTemplate() {
      return this.query('app-opinion-statement-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrOpinionStatementSubmittedComponent],
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
          verificationReport: {
            opinionStatement: { opinionStatementFiles: ['32882ee4-4b22-4411-bea9-ebdc9662f5d5'], notes: 'Notes' },
          },
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(AlrOpinionStatementSubmittedComponent);
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
