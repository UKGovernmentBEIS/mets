import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { nerSubmittedRequestActionPayload } from '@actions/ner/testing';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { NerOpinionStatementSubmittedComponent } from './opinion-statement.component';

describe('OpinionStatementComponent', () => {
  let component: NerOpinionStatementSubmittedComponent;
  let fixture: ComponentFixture<NerOpinionStatementSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<NerOpinionStatementSubmittedComponent> {
    get summaryTemplate() {
      return this.query('app-opinion-statement-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NerOpinionStatementSubmittedComponent],
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
          verificationReport: {
            opinionStatement: { opinionStatementFiles: ['32882ee4-4b22-4411-bea9-ebdc9662f5d5'], notes: 'Notes' },
          },
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(NerOpinionStatementSubmittedComponent);
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
