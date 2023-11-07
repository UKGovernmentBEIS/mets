import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitVariationStore } from '../../../permit-variation/store/permit-variation.store';
import { mockPermitVariationSubmitOperatorLedPayload } from '../../../permit-variation/testing/mock';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { AmendSummaryTemplateComponent } from './amend-summary-template.component';

describe('AmendSummaryTemplateComponent', () => {
  let component: AmendSummaryTemplateComponent;
  let fixture: ComponentFixture<AmendSummaryTemplateComponent>;
  let page: Page;

  class Page extends BasePage<AmendSummaryTemplateComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading');
    }

    get summaryLists() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AmendSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  describe('issuance', () => {
    const route = new ActivatedRouteStub({ section: 'monitoring-approaches' });
    let store: PermitIssuanceStore;
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [AmendSummaryTemplateComponent],
        imports: [RouterTestingModule, SharedModule, SharedPermitModule],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
        ],
      }).compileComponents();
    });

    beforeEach(async () => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState({
        ...mockState,
        permitSectionsCompleted: {
          ...mockState.permitSectionsCompleted,
          'AMEND_monitoring-approaches': [true],
        },
        reviewGroupDecisions: {
          DEFINE_MONITORING_APPROACHES: {
            type: 'OPERATOR_AMENDS_NEEDED',
            details: {
              requiredChanges: [
                {
                  reason: 'Changes required for monitoring approaches',
                  files: ['44de2d0f-643f-42ca-b413-1c0633cdbc5c'],
                },
              ],
            },
          },
          CALCULATION_CO2: {
            type: 'OPERATOR_AMENDS_NEEDED',
            details: {
              requiredChanges: [
                {
                  reason: 'Changes required for calculation approach',
                  files: ['24f9bc23-5608-46d9-99c7-5f61fb53b72c'],
                },
              ],
            },
          },
        },
        reviewAttachments: {
          '44de2d0f-643f-42ca-b413-1c0633cdbc5c': '16.pdf',
          '24f9bc23-5608-46d9-99c7-5f61fb53b72c': '15.pdf',
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display appropriate information', () => {
      expect(page.heading.textContent.trim()).toEqual('Amends needed for monitoring approaches');

      expect(page.summaryLists).toHaveLength(2);
      expect(page.summaryLists[0].querySelector('dt').textContent.trim()).toEqual('Changes required');
      expect(page.summaryLists[0].querySelector('dd').textContent.trim()).toEqual(
        '1. Changes required for monitoring approaches 16.pdf',
      );

      expect(page.summaryLists[1].querySelector('dt').textContent.trim()).toEqual('Changes required');
      expect(page.summaryLists[1].querySelector('dd').textContent.trim()).toEqual(
        '1. Changes required for calculation approach 15.pdf',
      );
    });
  });

  describe('variation', () => {
    let store: PermitVariationStore;
    const route = new ActivatedRouteStub({ taskId: '1', section: 'about-variation' });
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [AmendSummaryTemplateComponent],
        imports: [RouterTestingModule, SharedModule, SharedPermitModule],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
        ],
      }).compileComponents();
    });

    beforeEach(async () => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationSubmitOperatorLedPayload,
        payloadType: 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
        requestTaskType: 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT',
        permitVariationDetailsReviewDecision: {
          type: 'OPERATOR_AMENDS_NEEDED',
          details: {
            notes: 'notes',
            requiredChanges: [{ reason: 'reason' }],
          } as any,
        },
        permitVariationDetailsAmendCompleted: true,
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display appropriate information', () => {
      expect(page.heading.textContent.trim()).toEqual('Amends needed for about the variation');

      expect(page.summaryLists).toHaveLength(1);
      expect(page.summaryLists[0].querySelector('dt').textContent.trim()).toEqual('Changes required');
      expect(page.summaryLists[0].querySelector('dd').textContent.trim()).toEqual('1. reason');
    });
  });
});
