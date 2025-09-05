import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { hsetiSubmittedRequestActionPayload } from '@actions/hseti/testing/mock-hseti-submitted';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { OverallDecisionSummaryTemplateComponent } from '@tasks/hseti/shared/components/overall-decision-summary-template/overall-decision-summary-template.component';
import { BasePage } from '@testing';

import { HsetiOverallDecisionSubmittedComponent } from './overall-decision.component';

describe('HsetiOverallDecisionSubmittedComponent', () => {
  let component: HsetiOverallDecisionSubmittedComponent;
  let fixture: ComponentFixture<HsetiOverallDecisionSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<HsetiOverallDecisionSubmittedComponent> {
    get detailsSummaryTemplate() {
      return this.query('app-overall-decision-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OverallDecisionSummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'HSE_TI_COMPLETED',
        submitter: '123',
        payload: {
          ...hsetiSubmittedRequestActionPayload,
          overallDecision: {
            type: 'APPROVED',
            reason: '123',
          },
          payloadType: 'HSE_TI_COMPLETED',
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(HsetiOverallDecisionSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary HTMLElement', () => {
    expect(page.detailsSummaryTemplate).toBeTruthy();
  });
});
