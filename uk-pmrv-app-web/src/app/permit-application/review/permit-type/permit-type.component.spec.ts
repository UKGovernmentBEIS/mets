import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitVariationStore } from '../../../permit-variation/store/permit-variation.store';
import {
  mockPermitVariationRegulatorLedPayload,
  mockPermitVariationReviewOperatorLedPayload,
} from '../../../permit-variation/testing/mock';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockReviewState } from '../../testing/mock-state';
import { PermitTypeComponent } from './permit-type.component';

describe('PermitTypeComponent', () => {
  let component: PermitTypeComponent;
  let fixture: ComponentFixture<PermitTypeComponent>;
  let page: Page;
  let hostElement: HTMLElement;
  let store: PermitApplicationStore<PermitApplicationState>;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'PERMIT_TYPE',
    },
  );

  @Component({
    selector: 'app-review-group-decision-container',
    template: `<div>
      Review group decision component.
      <div>Key:{{ groupKey }}</div>
      <div>Can edit:{{ canEdit }}</div>
    </div>`,
  })
  class MockDecisionComponent {
    @Input() groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'];
    @Input() canEdit = true;
    @Output() readonly notification = new EventEmitter<boolean>();
  }

  class Page extends BasePage<PermitTypeComponent> {
    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
    get reviewSections() {
      return this.queryAll<HTMLLIElement>('li[app-task-item]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(PermitTypeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    hostElement = fixture.nativeElement;
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('permit issuance', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [SharedModule, SharedPermitModule, RouterTestingModule],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
        ],
        declarations: [PermitTypeComponent, MockDecisionComponent],
      }).compileComponents();
    });

    describe('without review group decision', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockReviewState,
          permitType: undefined,
          reviewGroupDecisions: {
            PERMIT_TYPE: null,
          },
          reviewSectionsCompleted: {
            PERMIT_TYPE: false,
          },
        });
      });

      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should display appropriate sections for review', () => {
        expect(
          page.reviewSections.map((section) => [
            section.querySelector('a').textContent.trim(),
            section.querySelector('govuk-tag').textContent.trim(),
          ]),
        ).toEqual([['Permit type', 'not started']]);
      });
    });

    describe('with review group decision summary', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockReviewState,
          reviewGroupDecisions: {
            PERMIT_TYPE: {
              type: 'ACCEPTED',
              details: { notes: 'notes' },
            },
          },
          reviewSectionsCompleted: {
            PERMIT_TYPE: true,
          },
        });
      });

      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should display appropriate sections for review', () => {
        expect(
          page.reviewSections.map((section) => [
            section.querySelector('a').textContent.trim(),
            section.querySelector('govuk-tag').textContent.trim(),
          ]),
        ).toEqual([['Permit type', 'completed']]);
      });
    });
  });

  describe('permit variation', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [SharedModule, SharedPermitModule, RouterTestingModule],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
        ],
        declarations: [PermitTypeComponent, MockDecisionComponent],
      }).compileComponents();
    });

    describe('should be readonly for variation task operator led', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockPermitVariationReviewOperatorLedPayload,
          reviewGroupDecisions: {
            PERMIT_TYPE: {
              type: 'ACCEPTED',
              details: { notes: 'notes' },
            },
          },
          reviewSectionsCompleted: {
            PERMIT_TYPE: true,
          },
        });
      });

      beforeEach(createComponent);

      it('should not display link to edit permit type', () => {
        expect(hostElement.querySelector('ul li[linktext="Permit type"] a')).toBeNull();
      });
    });

    describe('should be editable for variation task regulator led', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockPermitVariationRegulatorLedPayload,
          reviewGroupDecisions: {
            PERMIT_TYPE: {
              INSTALLATION_DETAILS: { notes: 'notes', variationScheduleItems: [] },
            },
          },
          reviewSectionsCompleted: {
            PERMIT_TYPE: true,
          },
        });
      });

      beforeEach(createComponent);

      it('should not display link to edit permit type', () => {
        expect(hostElement.querySelector('ul li[linktext="Permit type"] a')).not.toBeNull();
      });
    });
  });
});
