import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { SummaryDetailsComponent } from '../../about-variation/summary/summary-details.component';
import { PermitVariationStore } from '../../store/permit-variation.store';
import {
  mockPermitVariationRegulatorLedPayload,
  mockPermitVariationReviewOperatorLedPayload,
} from '../../testing/mock';
import { AboutVariationGroupKey } from '../../variation-types';
import { AboutVariationComponent } from './about-variation.component';

describe('AboutVariationComponent', () => {
  let component: AboutVariationComponent;
  let fixture: ComponentFixture<AboutVariationComponent>;
  let hostElement: HTMLElement;
  let page: Page;
  let store: PermitVariationStore;

  @Component({
    selector: 'app-variation-regulator-led-review-group-decision',
    template: `<div>
      Review group decision regulator led component.
      <p>Key:{{ groupKey }}</p>
    </div>`,
  })
  class MockDecisionRegulatorLedComponent {
    @Input() groupKey: AboutVariationGroupKey;
    @Output() readonly notification = new EventEmitter<boolean>();
  }

  @Component({
    selector: 'app-variation-operator-led-review-group-decision',
    template: `<div>
      Review group decision operator led component.
      <p>Key:{{ groupKey }}</p>
    </div>`,
  })
  class MockDecisionOperatorLedComponent {
    @Input() groupKey: AboutVariationGroupKey;
    @Output() readonly notification = new EventEmitter<boolean>();
  }

  class Page extends BasePage<AboutVariationComponent> {
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        AboutVariationComponent,
        MockDecisionRegulatorLedComponent,
        MockDecisionOperatorLedComponent,
        SummaryDetailsComponent,
      ],
      imports: [SharedModule, SharedPermitModule, RouterTestingModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitVariationStore,
        },
      ],
    }).compileComponents();
  });

  describe('for regulator that reviews permit variation', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        permitVariationDetails: {
          reason: 'Reason',
          modifications: [
            {
              type: 'INSTALLATION_NAME',
            },
            {
              type: 'NEW_SOURCE_STREAMS',
            },
            {
              type: 'DEFAULT_VALUE_OR_ESTIMATION_METHOD',
            },
          ],
        },
        permitVariationDetailsCompleted: true,
      });

      fixture = TestBed.createComponent(AboutVariationComponent);
      component = fixture.componentInstance;
      hostElement = fixture.nativeElement as HTMLElement;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display summary list', () => {
      expect(page.answers).toEqual([
        ['Description of changes', 'Reason'],
        [
          'Non-significant changes to the Monitoring Plan or the Monitoring Methodology Plan',
          'Change of installation name',
        ],
        ['Significant modifications to the Monitoring Plan', 'The introduction of new source streams'],
        [
          'Significant modifications to the Monitoring Methodology Plan',
          'The change of a default value or estimation method laid down in the monitoring methodology plan.',
        ],
      ]);
    });

    it('should display review decision details', () => {
      expect(
        hostElement.querySelector('app-variation-operator-led-review-group-decision').textContent.trim(),
      ).toContain('Review group decision operator led component.');
    });
  });

  describe('for regulator that submits permit variation', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationRegulatorLedPayload,
        permitVariationDetails: {
          reason: 'Reason',
          modifications: [
            {
              type: 'INSTALLATION_NAME',
            },
            {
              type: 'NEW_SOURCE_STREAMS',
            },
            {
              type: 'DEFAULT_VALUE_OR_ESTIMATION_METHOD',
            },
          ],
        },
        permitVariationDetailsCompleted: true,
      });

      fixture = TestBed.createComponent(AboutVariationComponent);
      component = fixture.componentInstance;
      hostElement = fixture.nativeElement as HTMLElement;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display summary list', () => {
      expect(page.answers).toEqual([
        ['Description of changes', 'Reason'],
        [
          'Non-significant changes to the Monitoring Plan or the Monitoring Methodology Plan',
          'Change of installation name',
        ],
        ['Significant modifications to the Monitoring Plan', 'The introduction of new source streams'],
        [
          'Significant modifications to the Monitoring Methodology Plan',
          'The change of a default value or estimation method laid down in the monitoring methodology plan.',
        ],
      ]);
    });

    it('should display regulator led review decision details', () => {
      expect(hostElement.textContent).toContain('Review group decision regulator led component.');
    });
  });
});
