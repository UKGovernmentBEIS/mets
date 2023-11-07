import { Component, Input, PipeTransform } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { ReviewGroupStatusWrapperPipe } from '@permit-application/shared/pipes/review-group-status-wrapper.pipe';
import {
  mockPermitCompletedAcceptedReviewGroupDecisions,
  mockPermitCompletedReviewSectionsCompleted,
} from '@permit-application/testing/mock-permit-apply-action';
import { ReviewGroupStatusPermitIssuancePipe } from '@permit-issuance/review/review-group-status-permit-issuance.pipe';
import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';

import { RequestActionsService, RequestItemsService } from 'pmrv-api';

import { PermitTransferState } from '../../store/permit-transfer.state';
import { PermitTransferStore } from '../../store/permit-transfer.store';
import {
  mockPermitTransferDetailsConfirmation,
  mockPermitTransferReviewPayload,
  mockPermitTransferSubmitPayload,
} from '../../testing/mock';
import { ReviewSectionsContainerComponent } from './review-sections-container.component';

describe('SectionsContainerComponent', () => {
  let component: ReviewSectionsContainerComponent;
  let fixture: ComponentFixture<ReviewSectionsContainerComponent>;
  let hostElement: HTMLElement;
  let store: PermitTransferStore;
  let router: Router;
  let route: ActivatedRoute;

  const requestActionsService = mockClass(RequestActionsService);
  const requestItemsService = mockClass(RequestItemsService);

  const commonStore: PermitTransferState = {
    ...mockPermitTransferReviewPayload,
    reviewSectionsCompleted: {
      ...mockPermitCompletedReviewSectionsCompleted,
      determination: false,
    },
    reviewGroupDecisions: mockPermitCompletedAcceptedReviewGroupDecisions,
    allowedRequestTaskActions: [
      'PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW',
      'PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION',
      'PERMIT_TRANSFER_B_REQUEST_PEER_REVIEW',
      'PERMIT_TRANSFER_B_REVIEW_RETURN_FOR_AMENDS',
    ],
  };

  const createComponent = () => {
    fixture = TestBed.createComponent(ReviewSectionsContainerComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    fixture.detectChanges();
  };

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
    @Input() statusResolverPipe: PipeTransform;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        ReviewSectionsContainerComponent,
        MockReviewSectionsComponent,
        ReviewGroupStatusPermitIssuancePipe,
        ReviewGroupStatusWrapperPipe,
      ],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: RequestItemsService, useValue: requestItemsService },
        { provide: RequestActionsService, useValue: requestActionsService },
        DestroySubject,
      ],
    }).compileComponents();
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
  });

  describe('completed permit transfer with no determination', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitTransferStore);
      store.setState(commonStore);

      createComponent();
    });

    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display header', () => {
      expect(hostElement.querySelector('app-page-heading h1').textContent.trim()).toContain(
        'GHGE permit transfer review Assigned to: John Doe',
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

  describe('permit transfer with determination', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitTransferStore);
      store.setState({
        ...commonStore,
        reviewSectionsCompleted: {
          ...mockPermitCompletedReviewSectionsCompleted,
          CONFIRM_TRANSFER_DETAILS: true,
          determination: true,
        },
        permitTransferDetails: mockPermitTransferSubmitPayload.permitTransferDetails,
        permitTransferDetailsConfirmation: mockPermitTransferDetailsConfirmation,
        permitTransferDetailsConfirmationDecision: {
          type: 'ACCEPTED',
          details: { notes: 'Confirmation decision notes' },
        },
        determination: {
          activationDate: '2022-02-22 14:26:44',
          reason: 'Grant reason',
          type: 'GRANTED',
        },
      });

      createComponent();
    });

    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should display overall decision', () => {
      expect(hostElement.querySelector('ul li[linktext="Overall decision"] span').textContent.trim()).toEqual(
        'Overall decision',
      );
      expect(hostElement.querySelector('ul li[linktext="Overall decision"] govuk-tag').textContent.trim()).toEqual(
        'granted',
      );
    });

    it('should display related action buttons', () => {
      expect(hostElement.querySelector('button[title="Notify Operator for decision"]')).not.toBeNull();
      expect(hostElement.querySelector('button[title="Send for peer review"]')).not.toBeNull();
      expect(hostElement.querySelector('button[title="Return for amends"]')).toBeNull();
    });
  });

  describe('permit with amend decision', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitTransferStore);
      store.setState({
        ...commonStore,
        reviewSectionsCompleted: {
          ...mockPermitCompletedReviewSectionsCompleted,
        },
        reviewGroupDecisions: {
          INSTALLATION_DETAILS: {
            type: 'OPERATOR_AMENDS_NEEDED',
            details: {
              requiredChanges: [{ reason: 'Changes required' }],
            },
          },
        },
      });

      createComponent();
    });

    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should display overall decision', () => {
      expect(hostElement.querySelector('ul li[linktext="Overall decision"] span').textContent.trim()).toEqual(
        'Overall decision',
      );
      expect(hostElement.querySelector('ul li[linktext="Overall decision"] govuk-tag').textContent.trim()).toEqual(
        'undecided',
      );
    });

    it('should display related action buttons', () => {
      expect(hostElement.querySelector('button[title="Notify Operator for decision"]')).toBeNull();
      expect(hostElement.querySelector('button[title="Send for peer review"]')).toBeNull();
      expect(hostElement.querySelector('button[title="Return for amends"]')).not.toBeNull();
    });

    it('should navigate to amend page when button is clicked', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      const amendButton = hostElement.querySelector<HTMLButtonElement>('button[title="Return for amends"]');
      amendButton.click();
      fixture.detectChanges();
      expect(navigateSpy).toHaveBeenCalledWith(['return-for-amends'], { relativeTo: route });
    });
  });
});
