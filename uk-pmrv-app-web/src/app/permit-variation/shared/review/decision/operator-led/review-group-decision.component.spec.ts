import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { BasePage, mockClass } from '@testing';

import {
  PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload,
  RequestTaskAttachmentsHandlingService,
  TasksService,
} from 'pmrv-api';

import { SharedPermitModule } from '../../../../../permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '../../../../../permit-application/store/permit-application.store';
import { PermitVariationStore } from '../../../../store/permit-variation.store';
import { mockPermitVariationReviewOperatorLedPayload } from '../../../../testing/mock';
import { AboutVariationGroupKey } from '../../../../variation-types';
import { ReviewGroupVariationPipe } from '../review-group-variation.pipe';
import { ReviewGroupDecisionComponent } from './review-group-decision.component';

describe('ReviewGroupDecisionComponent', () => {
  let page: Page;
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let store: PermitVariationStore;

  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const tasksService = mockClass(TasksService);

  @Component({
    template: `
      <app-variation-operator-led-review-group-decision
        [canEdit]="canEdit"
        [groupKey]="groupKey"
        (notification)="reviewDecisionSubmitted($event)"
      ></app-variation-operator-led-review-group-decision>
    `,
  })
  class TestComponent {
    canEdit = true;
    groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'] | AboutVariationGroupKey =
      'ABOUT_VARIATION';
    notification = false;

    reviewDecisionSubmitted(_: any): void {
      this.notification = _;
    }
  }

  class Page extends BasePage<TestComponent> {
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryErrorList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get submitFormButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get changeLink() {
      return this.query<HTMLLinkElement>('a');
    }
    get acceptDecisionOption() {
      return this.query<HTMLInputElement>('#decision-option0');
    }
    get rejectDecisionOption() {
      return this.query<HTMLInputElement>('#decision-option1');
    }
    get operatorAmendsNeededDecisionOption() {
      return this.query<HTMLInputElement>('#decision-option2');
    }
    get reviewNotesValue() {
      return this.getInputValue('#notes');
    }
    set reviewNotesValue(value: string) {
      this.setInputValue('#notes', value);
    }
    get requiredChangesValue() {
      return this.getInputValue('#requiredChanges.0.reason');
    }
    set requiredChangesValue(value: string) {
      this.setInputValue('#requiredChanges.0.reason', value);
    }
    get filesText() {
      return this.queryAll<HTMLDivElement>('.moj-multi-file-upload__message');
    }
    set fileValue(value: any) {
      this.setInputValue('input[type="file"]', value);
    }
    get fileLabel() {
      return this.query<HTMLButtonElement>('#l\\.requiredChanges\\.0\\.files');
    }
    get summaryPairText() {
      return Array.from(this.queryAll<HTMLDivElement>('.govuk-summary-list__row'))
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .filter(([, data]) => !!data)
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get changeDecisionLink() {
      return this.query<HTMLAnchorElement>('h2[app-summary-header] a');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, ReviewGroupDecisionComponent, ReviewGroupVariationPipe],
      imports: [SharedModule, SharedPermitModule, RouterTestingModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitVariationStore,
        },
      ],
    }).compileComponents();
  });

  describe('for unsubmitted review without data', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        permitVariationDetails: {
          reason: 'cbcv',
          modifications: [
            {
              type: 'COMPANY_NAME_NOT_DUE_TO_OWNERSHIP_CHANGE',
            },
            {
              type: 'INSTALLATION_CATEGORY',
            },
            {
              type: 'INSTALLATION_SUB',
            },
          ],
        },
        permitVariationDetailsCompleted: true,
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form and emit the decision', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();
      expect(page.acceptDecisionOption.checked).toBe(false);
      expect(page.rejectDecisionOption.checked).toBe(false);
      expect(page.operatorAmendsNeededDecisionOption.checked).toBe(false);

      page.submitFormButton.click();
      fixture.detectChanges();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select a decision for this review group']);

      page.operatorAmendsNeededDecisionOption.click();
      page.submitFormButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter the change required by the operator']);

      page.acceptDecisionOption.click();
      page.submitFormButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION_PAYLOAD',
          decision: {
            type: 'ACCEPTED',
            details: {
              notes: null,
            },
          },
          reviewSectionsCompleted: mockPermitVariationReviewOperatorLedPayload.reviewSectionsCompleted,
          permitVariationDetailsReviewCompleted: true,
        },
      });
      expect(page.summaryPairText).toEqual([['Decision status', 'Accepted']]);
    });
  });

  describe('for submitted review decision group with decision data', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        permitVariationDetails: {
          reason: 'reason',
          modifications: [{ type: 'CALCULATION_TO_MEASUREMENT_METHODOLOGIES' }],
        },
        permitVariationDetailsCompleted: true,
        permitVariationDetailsReviewCompleted: true,
        permitVariationDetailsReviewDecision: {
          type: 'ACCEPTED',
          details: {
            notes: 'notes',
          } as any,
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display change link', () => {
      expect(page.changeDecisionLink).toBeTruthy();
    });

    it('should change and submit new decision', () => {
      const reviewDecisionSubmittedSpy = jest.spyOn(component, 'reviewDecisionSubmitted');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.changeLink.click();
      fixture.detectChanges();

      page.rejectDecisionOption.click();
      page.reviewNotesValue = 'new notes';
      page.submitFormButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION_PAYLOAD',
          decision: {
            type: 'REJECTED',
            details: {
              notes: 'new notes',
            },
          },
          reviewSectionsCompleted: mockPermitVariationReviewOperatorLedPayload.reviewSectionsCompleted,
          permitVariationDetailsReviewCompleted: true,
        },
      });

      expect(page.summaryPairText).toEqual([
        ['Decision status', 'Rejected'],
        ['Notes', 'new notes'],
      ]);
      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledTimes(1);
      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledWith(true);
    });
  });
});
