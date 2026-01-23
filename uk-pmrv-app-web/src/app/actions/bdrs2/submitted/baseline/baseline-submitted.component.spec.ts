import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { bdrs2SubmittedRequestActionPayload } from '@actions/bdrs2/testing/mock-bdrs2-submitted';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { Bdrs2BaselineSubmittedComponent } from './baseline-submitted.component';

describe('Bdrs2BaselineSubmittedComponent', () => {
  let component: Bdrs2BaselineSubmittedComponent;
  let fixture: ComponentFixture<Bdrs2BaselineSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<Bdrs2BaselineSubmittedComponent> {
    get baselineSummaryTemplate() {
      return this.query('app-bdrs2-baseline-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Bdrs2BaselineSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'BDRS2_APPLICATION_SENT_TO_VERIFIER',
        submitter: '123',
        payload: {
          ...bdrs2SubmittedRequestActionPayload,
          payloadType: 'BDRS2_APPLICATION_SENT_TO_VERIFIER_PAYLOAD',
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(Bdrs2BaselineSubmittedComponent);
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
