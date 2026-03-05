import { Component, Input, PipeTransform } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestActionsService, RequestItemsService } from 'pmrv-api';

import { mockClass } from '../../../../testing';
import { ReviewGroupStatusWrapperPipe } from '../../../permit-application/shared/pipes/review-group-status-wrapper.pipe';
import {
  mockPermitCompletedAcceptedReviewGroupDecisions,
  mockPermitCompletedReviewSectionsCompleted,
} from '../../../permit-application/testing/mock-permit-apply-action';
import { SharedModule } from '../../../shared/shared.module';
import { PermitVariationStore } from '../../store/permit-variation.store';
import {
  mockPermitVariationCompleteAcceptedReviewGroupDecisions,
  mockPermitVariationRegulatorLedPayload,
  mockPermitVariationReviewOperatorLedPayload,
} from '../../testing/mock';
import { ReviewGroupStatusPermitVariationPipe } from '../review-group-status-permit-variation.pipe';
import { ReviewGroupStatusPermitVariationRegulatorLedPipe } from '../review-group-status-permit-variation-regulator-led.pipe';
import { ReviewSectionsContainerComponent } from './review-sections-container.component';

describe('ReviewSectionsContainerComponent', () => {
  let component: ReviewSectionsContainerComponent;
  let fixture: ComponentFixture<ReviewSectionsContainerComponent>;
  let hostElement: HTMLElement;

  let store: PermitVariationStore;
  let router: Router;
  let route: ActivatedRoute;

  const requestActionsService = mockClass(RequestActionsService);
  const requestItemsService = mockClass(RequestItemsService);

  @Component({
    selector: 'app-review-sections',
    template: `
      <div>
        <div class="status-resolver">
          {{ 'INSTALLATION_DETAILS' | reviewGroupStatusWrapper: statusResolverPipe | async }}
        </div>
      </div>
    `,
  })
  class MockReviewSectionsComponent {
    @Input()
    statusResolverPipe: PipeTransform;
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ReviewSectionsContainerComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        ReviewSectionsContainerComponent,
        MockReviewSectionsComponent,
        ReviewGroupStatusPermitVariationPipe,
        ReviewGroupStatusPermitVariationRegulatorLedPipe,
        ReviewGroupStatusWrapperPipe,
      ],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: RequestItemsService, useValue: requestItemsService },
        { provide: RequestActionsService, useValue: requestActionsService },
      ],
    }).compileComponents();
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
  });

  describe('completed permit variation operator led with no determination', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        reviewSectionsCompleted: {
          ...mockPermitCompletedReviewSectionsCompleted,
          determination: false,
        },
        reviewGroupDecisions: mockPermitCompletedAcceptedReviewGroupDecisions,
        allowedRequestTaskActions: [
          'PERMIT_VARIATION_SAVE_APPLICATION_REVIEW',
          'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION',
          'PERMIT_VARIATION_REQUEST_PEER_REVIEW',
          'PERMIT_VARIATION_REVIEW_RETURN_FOR_AMENDS',
        ],
      });

      createComponent();
    });

    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display header', () => {
      expect(hostElement.querySelector('app-page-heading h1').textContent).toContain('GHGE permit variation review');
    });

    it('should display about variation section', () => {
      expect(hostElement.querySelector('ul li[linktext="About the variation"]')).toBeTruthy();
      expect(hostElement.querySelector('ul li[linktext="About the variation"] govuk-tag').textContent.trim()).toEqual(
        'undecided',
      );
    });

    it('should display section status', () => {
      expect(hostElement.querySelector('div.status-resolver').textContent.trim()).toEqual('accepted');
    });

    it('should display overall decision', () => {
      expect(hostElement.querySelector('ul li[linktext="Overall decision"] span').textContent.trim()).toEqual(
        'Overall decision',
      );
      expect(hostElement.querySelector('ul li[linktext="Overall decision"] govuk-tag').textContent.trim()).toEqual(
        'undecided',
      );
    });

    it('should not display action buttons', () => {
      expect(hostElement.querySelector('button[title="Notify Operator for decision"]')).toBeNull();
      expect(hostElement.querySelector('button[title="Send for peer review"]')).toBeNull();
      expect(hostElement.querySelector('button[title="Return for amends"]')).toBeNull();
    });
  });

  describe('completed permit variation with grant determination', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        reviewGroupDecisions: mockPermitVariationCompleteAcceptedReviewGroupDecisions,
        reviewSectionsCompleted: {
          ...mockPermitCompletedReviewSectionsCompleted,
          determination: true,
        },
        determination: {
          activationDate: '2022-02-22 14:26:44',
          reason: 'Grant reason',
          type: 'GRANTED',
          logChanges: 'ddd',
        },
        permitVariationDetailsCompleted: true,
        permitVariationDetailsReviewCompleted: true,
        permitVariationDetailsReviewDecision: {
          type: 'ACCEPTED',
          variationScheduleItems: [],
        },
        allowedRequestTaskActions: [
          'PERMIT_VARIATION_SAVE_APPLICATION_REVIEW',
          'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION',
          'PERMIT_VARIATION_REQUEST_PEER_REVIEW',
          'PERMIT_VARIATION_REVIEW_RETURN_FOR_AMENDS',
        ],
      });

      createComponent();
    });

    it('should display about variation section', () => {
      expect(hostElement.querySelector('ul li[linktext="About the variation"] govuk-tag').textContent.trim()).toEqual(
        'accepted',
      );
    });

    it('should display overall decision', () => {
      expect(hostElement.querySelector('ul li[linktext="Overall decision"] span').textContent.trim()).toEqual(
        'Overall decision',
      );
      expect(hostElement.querySelector('ul li[linktext="Overall decision"] govuk-tag').textContent.trim()).toEqual(
        'approved',
      );
    });

    it('should display related action buttons', () => {
      expect(hostElement.querySelector('button[title="Notify Operator for decision"]')).not.toBeNull();
      expect(hostElement.querySelector('button[title="Send for peer review"]')).not.toBeNull();
      expect(hostElement.querySelector('button[title="Return for amends"]')).toBeNull();
    });

    it('should navigate to peer review page when button is clicked', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      const peerReviewButton = hostElement.querySelector<HTMLButtonElement>('button[title="Send for peer review"]');
      peerReviewButton.click();
      fixture.detectChanges();
      expect(navigateSpy).toHaveBeenCalledWith(['peer-review'], { relativeTo: route });
    });

    it('should navigate to notify operator page when button is clicked', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      const button = hostElement.querySelector<HTMLButtonElement>('button[title="Notify Operator for decision"]');
      button.click();
      fixture.detectChanges();
      expect(navigateSpy).toHaveBeenCalledWith(['notify-operator'], { relativeTo: route });
    });
  });

  describe('permit variation regulator led with no determination', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationRegulatorLedPayload,
        reviewGroupDecisions: {
          INSTALLATION_DETAILS: { notes: 'notes', variationScheduleItems: [] },
        },
        allowedRequestTaskActions: [
          'PERMIT_VARIATION_SAVE_APPLICATION_REGULATOR_LED',
          'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED',
          'PERMIT_VARIATION_REQUEST_PEER_REVIEW_REGULATOR_LED',
        ],
      });

      createComponent();
    });

    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should display header', () => {
      expect(hostElement.querySelector('app-page-heading h1').textContent).toContain(
        'Make a change to the GHGE permit',
      );
    });

    it('should display about variation section', () => {
      expect(hostElement.querySelector('ul li[linktext="About the variation"]')).toBeTruthy();
      expect(hostElement.querySelector('ul li[linktext="About the variation"] govuk-tag').textContent.trim()).toEqual(
        'not started',
      );
    });

    it('should display section status', () => {
      expect(hostElement.querySelector('div.status-resolver').textContent.trim()).toEqual('complete');
    });

    it('should display overall decision', () => {
      expect(hostElement.querySelector('ul li[linktext="Overall decision"] span').textContent.trim()).toEqual(
        'Overall decision',
      );
      expect(hostElement.querySelector('ul li[linktext="Overall decision"] govuk-tag').textContent.trim()).toEqual(
        'cannot start yet',
      );
    });

    it('should not display action buttons', () => {
      expect(hostElement.querySelector('button[title="Notify Operator for decision"]')).toBeNull();
      expect(hostElement.querySelector('button[title="Send for peer review"]')).toBeNull();
    });
  });

  describe('permit variation regulator led with determination', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationRegulatorLedPayload,
        permitVariationDetailsCompleted: true,
        permitVariationDetails: {
          modifications: [{ type: 'COMPANY_NAME_NOT_DUE_TO_OWNERSHIP_CHANGE' }],
          reason: 'reason',
        },
        permitVariationDetailsReviewDecision: {
          variationScheduleItems: ['item1'],
        },
        reviewGroupDecisions: {
          INSTALLATION_DETAILS: { notes: 'notes', variationScheduleItems: [] },
        },
        reviewSectionsCompleted: {
          determination: true,
        },
        determination: {
          activationDate: '2022-02-22 14:26:44',
          reason: 'Grant reason',
          reasonTemplate: 'WHERE_OPERATOR_FAILED_TO_APPLY_IN_ACCORDANCE_WITH_CONDITIONS',
          logChanges: 'ddd',
        },
        allowedRequestTaskActions: [
          'PERMIT_VARIATION_SAVE_APPLICATION_REGULATOR_LED',
          'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED',
          'PERMIT_VARIATION_REQUEST_PEER_REVIEW_REGULATOR_LED',
        ],
      });

      createComponent();
    });

    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should display header', () => {
      expect(hostElement.querySelector('app-page-heading h1').textContent).toContain(
        'Make a change to the GHGE permit',
      );
    });

    it('should display about variation section', () => {
      expect(hostElement.querySelector('ul li[linktext="About the variation"]')).toBeTruthy();
      expect(hostElement.querySelector('ul li[linktext="About the variation"] govuk-tag').textContent.trim()).toEqual(
        'completed',
      );
    });

    it('should display section status', () => {
      expect(hostElement.querySelector('div.status-resolver').textContent.trim()).toEqual('complete');
    });

    it('should display overall decision', () => {
      expect(hostElement.querySelector('ul li[linktext="Overall decision"] span').textContent.trim()).toEqual(
        'Overall decision',
      );
      expect(hostElement.querySelector('ul li[linktext="Overall decision"] govuk-tag').textContent.trim()).toEqual(
        'approved',
      );
    });

    it('should display related action buttons', () => {
      expect(hostElement.querySelector('button[title="Notify Operator for decision"]')).not.toBeNull();
      expect(hostElement.querySelector('button[title="Send for peer review"]')).not.toBeNull();
      expect(hostElement.querySelector('button[title="Return for amends"]')).toBeNull();
    });

    it('should navigate to peer review page when button is clicked', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      const peerReviewButton = hostElement.querySelector<HTMLButtonElement>('button[title="Send for peer review"]');
      peerReviewButton.click();
      fixture.detectChanges();
      expect(navigateSpy).toHaveBeenCalledWith(['peer-review'], { relativeTo: route });
    });

    it('should navigate to notify operator page when button is clicked', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      const button = hostElement.querySelector<HTMLButtonElement>('button[title="Notify Operator for decision"]');
      button.click();
      fixture.detectChanges();
      expect(navigateSpy).toHaveBeenCalledWith(['notify-operator'], { relativeTo: route });
    });

    it('should not display links for pdf in peer review', () => {
      const textContent = hostElement.querySelector('app-preview-documents').textContent;
      expect(textContent).toBe('');
    });
  });
});
