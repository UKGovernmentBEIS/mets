import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { bdrSubmittedRequestActionPayload } from '@actions/bdr/testing/mock-bdr-submitted';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { BaselineSubmittedComponent } from './baseline-submitted.component';

describe('BaselineSubmittedComponent', () => {
  let component: BaselineSubmittedComponent;
  let fixture: ComponentFixture<BaselineSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<BaselineSubmittedComponent> {
    get baselineSummaryTemplate() {
      return this.query('app-bdr-baseline-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BaselineSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'BDR_APPLICATION_SENT_TO_VERIFIER',
        submitter: '123',
        payload: {
          ...bdrSubmittedRequestActionPayload,
          payloadType: 'BDR_APPLICATION_SENT_TO_VERIFIER_PAYLOAD',
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(BaselineSubmittedComponent);
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
