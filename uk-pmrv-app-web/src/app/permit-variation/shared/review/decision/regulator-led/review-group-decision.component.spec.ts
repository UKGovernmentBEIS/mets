import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedPermitModule } from '../../../../../permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '../../../../../permit-application/store/permit-application.store';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitVariationStore } from '../../../../store/permit-variation.store';
import { mockPermitVariationRegulatorLedPayload } from '../../../../testing/mock';
import { AboutVariationGroupKey } from '../../../../variation-types';
import { ReviewGroupVariationPipe } from '../review-group-variation.pipe';
import { ReviewGroupDecisionComponent } from './review-group-decision.component';

describe('ReviewGroupDecisionComponent', () => {
  let page: Page;
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let store: PermitVariationStore;

  const tasksService = mockClass(TasksService);

  @Component({
    template: `
      <app-variation-regulator-led-review-group-decision
        [canEdit]="canEdit"
        [groupKey]="groupKey"
        (notification)="reviewDecisionSubmitted($event)"
      ></app-variation-regulator-led-review-group-decision>
    `,
  })
  class TestComponent {
    canEdit = true;
    groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'] | AboutVariationGroupKey =
      'MONITORING_METHODOLOGY_PLAN';
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
      return this.queryAll<HTMLButtonElement>('button[type="submit"]').slice(-1).pop();
    }
    get changeLink() {
      return this.query<HTMLLinkElement>('a');
    }
    get addItemButton() {
      const addItemButtons = this.queryAll<HTMLButtonElement>('button[type="button"]');
      return addItemButtons[0];
    }
    getItemValue(idx: number) {
      return this.getInputValue(`#variationScheduleItems.${idx}.item`);
    }
    setItemValue(idx: number, value: string) {
      this.setInputValue(`#variationScheduleItems.${idx}.item`, value);
    }
    get reviewNotesValue() {
      return this.getInputValue('#notes');
    }
    set reviewNotesValue(value: string) {
      this.setInputValue('#notes', value);
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
      imports: [SharedModule, SharedPermitModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: {} },
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitVariationStore,
        },
      ],
      declarations: [TestComponent, ReviewGroupDecisionComponent, ReviewGroupVariationPipe],
    }).compileComponents();
  });

  describe('for regulator that submits permit variation monitor methology plan group', () => {
    const route = new ActivatedRouteStub(
      {},
      {},
      {
        groupKey: 'MONITORING_METHODOLOGY_PLAN',
      },
    );

    beforeEach(() => {
      TestBed.overrideProvider(ActivatedRoute, { useValue: route });
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationRegulatorLedPayload,
        permitSectionsCompleted: {
          monitoringMethodologyPlans: [true],
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should post an empty decision', () => {
      const reviewDecisionSubmittedSpy = jest.spyOn(component, 'reviewDecisionSubmitted');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.reviewNotesValue = '';

      page.submitFormButton.click();
      fixture.detectChanges();

      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledWith(true);
      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED',
        requestTaskId: mockPermitVariationRegulatorLedPayload.requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED_PAYLOAD',
          group: 'MONITORING_METHODOLOGY_PLAN',
          decision: {
            notes: '',
            variationScheduleItems: [],
          },
        },
      });
    });

    it('should submit a valid form and emit the decision', () => {
      const reviewDecisionSubmittedSpy = jest.spyOn(component, 'reviewDecisionSubmitted');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.reviewNotesValue = 'somenotes';

      page.addItemButton.click();
      fixture.detectChanges();

      page.setItemValue(0, 'item0');

      page.submitFormButton.click();
      fixture.detectChanges();

      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledWith(true);
      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED_PAYLOAD',
          group: 'MONITORING_METHODOLOGY_PLAN',
          decision: {
            notes: 'somenotes',
            variationScheduleItems: ['item0'],
          },
        },
      });
    });
  });

  describe('for regulator that submits permit variation monitor methology plan group with section not completed', () => {
    const route = new ActivatedRouteStub(
      {},
      {},
      {
        groupKey: 'MONITORING_METHODOLOGY_PLAN',
      },
    );

    beforeEach(() => {
      TestBed.overrideProvider(ActivatedRoute, { useValue: route });
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationRegulatorLedPayload,
        permitSectionsCompleted: {
          monitoringMethodologyPlans: [false],
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not submit a valid form and emit the decision', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.reviewNotesValue = 'somenotes';

      page.addItemButton.click();
      fixture.detectChanges();

      page.setItemValue(0, 'item0');

      page.submitFormButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['All sections must be completed']);
    });
  });

  describe('for regulator that submits permit variation about the variation with complete variation details', () => {
    const route = new ActivatedRouteStub(
      {},
      {},
      {
        groupKey: 'ABOUT_VARIATION',
      },
    );

    beforeEach(() => {
      TestBed.overrideProvider(ActivatedRoute, { useValue: route });
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationRegulatorLedPayload,
        permitVariationDetailsCompleted: true,
        permitVariationDetails: {
          reason: 'reason',
          modifications: [{ type: 'CALCULATION_TO_MEASUREMENT_METHODOLOGIES' }],
        },
      });
    });

    beforeEach(createComponent);

    it('should submit a valid form and emit the decision', () => {
      component.groupKey = 'ABOUT_VARIATION';
      fixture.detectChanges();
      const reviewDecisionSubmittedSpy = jest.spyOn(component, 'reviewDecisionSubmitted');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.reviewNotesValue = 'somenotes';

      page.addItemButton.click();
      fixture.detectChanges();

      page.setItemValue(0, 'item0');

      page.submitFormButton.click();
      fixture.detectChanges();

      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledWith(true);
      expect(reviewDecisionSubmittedSpy).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION_REGULATOR_LED',
        requestTaskId: 237,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION_REGULATOR_LED_PAYLOAD',
          decision: {
            notes: 'somenotes',
            variationScheduleItems: ['item0'],
          },
        },
      });
    });
  });

  describe('for regulator that submits permit variation about the variation with incomplete variation details', () => {
    const route = new ActivatedRouteStub(
      {},
      {},
      {
        groupKey: 'ABOUT_VARIATION',
      },
    );

    beforeEach(() => {
      TestBed.overrideProvider(ActivatedRoute, { useValue: route });
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationRegulatorLedPayload,
        permitVariationDetailsCompleted: false,
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not submit a valid form and emit the decision', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.reviewNotesValue = 'somenotes';

      page.addItemButton.click();
      fixture.detectChanges();

      page.setItemValue(0, 'item0');

      page.submitFormButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['All sections must be completed']);
    });
  });
});
