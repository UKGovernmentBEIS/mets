import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { PermitVariationStore } from '../../../store/permit-variation.store';
import {
  mockPermitVariationRegulatorLedPayload,
  mockPermitVariationReviewOperatorLedPayload,
} from '../../../testing/mock';
import { SummaryDetailsComponent } from './summary-details.component';

describe('SummaryDetailsComponent', () => {
  let component: SummaryDetailsComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;
  let store: PermitVariationStore;

  @Component({
    template: `
      <app-permit-variation-determination-summary-details></app-permit-variation-determination-summary-details>
    `,
  })
  class TestComponent {}

  class Page extends BasePage<TestComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(SummaryDetailsComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule, SharedModule],
      declarations: [TestComponent, SummaryDetailsComponent],
    }).compileComponents();

    store = TestBed.inject(PermitVariationStore);
  });

  describe('for determination GRANTED', () => {
    beforeEach(() => {
      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        determination: {
          type: 'GRANTED',
          reason: 'requirements are fulfilled',
          activationDate: new Date('2030-01-01T00:00:00Z').toISOString(),
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the determination summary', () => {
      expect(page.summaryDefinitions).toEqual([
        'Approve',
        'Change',
        'requirements are fulfilled',
        'Change',
        '1 Jan 2030',
        'Change',
      ]);
    });
  });

  describe('for variation regulator led determination', () => {
    beforeEach(() => {
      store.setState({
        ...mockPermitVariationRegulatorLedPayload,
        permitVariationDetails: {
          reason: 'reason',
          modifications: [{ type: 'CALCULATION_TO_MEASUREMENT_METHODOLOGIES' }],
        },
        permitVariationDetailsCompleted: true,
        determination: {
          reason: 'requirements are fulfilled',
          activationDate: new Date('2030-01-01T00:00:00Z').toISOString(),
          reasonTemplate: 'WHERE_OPERATOR_FAILED_TO_APPLY_IN_ACCORDANCE_WITH_CONDITIONS',
          logChanges: 'logChanges',
        },
        reviewSectionsCompleted: { determination: true },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the determination summary', () => {
      expect(page.summaryDefinitions).toEqual([
        'requirements are fulfilled',
        'Change',
        '1 Jan 2030',
        'Change',
        'Where the operator failed to apply in accordance with conditions',
        'Change',
        'logChanges',
        'Change',
      ]);
    });
  });
});
