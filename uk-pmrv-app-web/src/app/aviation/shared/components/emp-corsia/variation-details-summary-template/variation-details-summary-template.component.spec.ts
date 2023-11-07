import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { VariationDetailsSummaryTemplateComponent } from './variation-details-summary-template.component';

describe('VariationDetailsSummaryTemplateComponent', () => {
  let page: Page;
  let component: VariationDetailsSummaryTemplateComponent;
  let fixture: ComponentFixture<VariationDetailsSummaryTemplateComponent>;

  class Page extends BasePage<VariationDetailsSummaryTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VariationDetailsSummaryTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(VariationDetailsSummaryTemplateComponent);
    component = fixture.componentInstance;
  };

  describe('show details for variation', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.variationDetails = {
        changes: [
          'STATUS_FOR_THRESHOLDS',
          'RECOMMENDATIONS_IN_VERIFICATION_IMPROVEMENT_REPORT',
          'AEROPLANE_OPERATOR_NAME',
        ],
        reason: 'My reason',
      };
      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary details', () => {
      expect(page.summaryValues).toHaveLength(4);
      expect(page.summaryValues).toEqual([
        [
          'Material or significant changes',
          'Changing your status for one of the thresholds specified in article 22(7) to (12) of the Air Navigation Order',
        ],
        [
          'Other changes requiring a variation',
          'Changes to account for recommendations in a verification improvement report',
        ],
        ['Non-material changes', 'Changing your aeroplane operator name'],
        ['Summarise what you are changing and the reasons for the changes', 'My reason'],
      ]);
    });
  });

  describe('show details for variation regulator led', () => {
    beforeEach(createComponent);

    beforeEach(() => {
      component.variationDetails = {
        changes: ['STATUS_FOR_THRESHOLDS', 'AEROPLANE_OPERATOR_NAME'],
        reason: 'My reason',
      };
      component.variationRegulatorLedReason =
        'Aircraft operator failed to comply with a requirement in the plan, or to apply in accordance with conditions';
      page = new Page(fixture);
      fixture.detectChanges();
      jest.clearAllMocks();
    });

    it('should show summary details', () => {
      expect(page.summaryValues).toHaveLength(4);
      expect(page.summaryValues).toEqual([
        [
          'Material or significant changes',
          'Changing your status for one of the thresholds specified in article 22(7) to (12) of the Air Navigation Order',
        ],
        ['Non-material changes', 'Changing your aeroplane operator name'],
        ['Summarise what you are changing and the reasons for the changes', 'My reason'],
        [
          'Reason to include in the notice',
          'Aircraft operator failed to comply with a requirement in the plan, or to apply in accordance with conditions',
        ],
      ]);
    });
  });
});
