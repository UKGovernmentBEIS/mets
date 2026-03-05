import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { PermitVariationStore } from '../../../../permit-variation/store/permit-variation.store';
import {
  mockPermitVariationRegulatorLedPayload,
  mockPermitVariationReviewOperatorLedPayload,
} from '../../../../permit-variation/testing/mock';
import { SharedModule } from '../../../../shared/shared.module';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockState } from '../../../testing/mock-state';
import { DecisionContainerComponent } from './decision-container.component';

describe('DecisionContainerComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostElement: HTMLElement;
  let store: PermitApplicationStore<PermitApplicationState>;

  @Component({
    template: `
      <app-review-group-decision-container
        [groupKey]="groupKey"
        [canEdit]="canEdit"
        (notification)="notification = true"></app-review-group-decision-container>
    `,
  })
  class TestComponent {
    canEdit = true;
    groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'] = 'MONITORING_METHODOLOGY_PLAN';
    notification = false;
  }

  @Component({
    selector: 'app-variation-regulator-led-review-group-decision',
    template: `
      <div>
        Review group decision regulator led component.
        <p>Key:{{ groupKey }}</p>
        <p>Can edit:{{ canEdit }}</p>
      </div>
    `,
  })
  class MockDecisionRegulatorLedComponent {
    @Input() groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'];
    @Input() canEdit = true;
    @Output() readonly notification = new EventEmitter<boolean>();
  }

  @Component({
    selector: 'app-variation-operator-led-review-group-decision',
    template: `
      <div>
        Review group decision operator led component.
        <p>Key:{{ groupKey }}</p>
        <p>Can edit:{{ canEdit }}</p>
      </div>
    `,
  })
  class MockDecisionOperatorLedComponent {
    @Input() groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'];
    @Input() canEdit = true;
    @Output() readonly notification = new EventEmitter<boolean>();
  }

  @Component({
    selector: 'app-review-group-decision',
    template: `
      <div>
        Review group decision component.
        <p>Key:{{ groupKey }}</p>
        <p>Can edit:{{ canEdit }}</p>
      </div>
    `,
  })
  class MockDecisionComponent {
    @Input() groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'];
    @Input() canEdit = true;
    @Output() readonly notification = new EventEmitter<boolean>();
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TestComponent);
    hostElement = fixture.nativeElement as HTMLElement;
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  describe('permit issuance', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [SharedModule, RouterTestingModule],
        declarations: [
          TestComponent,
          DecisionContainerComponent,
          MockDecisionRegulatorLedComponent,
          MockDecisionComponent,
        ],
        providers: [
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
        ],
      }).compileComponents();
    });

    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState({
        ...mockState,
        requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_REVIEW',
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display decision info', () => {
      expect(hostElement.textContent).toContain('Review group decision component.');
      expect(hostElement.textContent).toContain('Key:MONITORING_METHODOLOGY_PLAN');
    });
  });

  describe('permit variation operator led', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [SharedModule, RouterTestingModule],
        declarations: [
          TestComponent,
          DecisionContainerComponent,
          MockDecisionRegulatorLedComponent,
          MockDecisionOperatorLedComponent,
          MockDecisionComponent,
        ],
        providers: [
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
        ],
      }).compileComponents();
    });

    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display regulator led decision info', () => {
      expect(hostElement.textContent).toContain('Review group decision operator led component.');
      expect(hostElement.textContent).toContain('Key:MONITORING_METHODOLOGY_PLAN');
    });
  });

  describe('permit variation regulator led', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [SharedModule, RouterTestingModule],
        declarations: [
          TestComponent,
          DecisionContainerComponent,
          MockDecisionRegulatorLedComponent,
          MockDecisionComponent,
        ],
        providers: [
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
        ],
      }).compileComponents();
    });

    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockPermitVariationRegulatorLedPayload,
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display regulator led decision info', () => {
      expect(hostElement.textContent).toContain('Review group decision regulator led component.');
      expect(hostElement.textContent).toContain('Key:MONITORING_METHODOLOGY_PLAN');
    });
  });
});
