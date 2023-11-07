import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../testing';
import { PermitIssuanceStore } from '../../permit-issuance/store/permit-issuance.store';
import { PermitVariationStore } from '../../permit-variation/store/permit-variation.store';
import { mockPermitVariationSubmitOperatorLedPayload } from '../../permit-variation/testing/mock';
import { SharedModule } from '../../shared/shared.module';
import { SharedPermitModule } from '../shared/shared-permit.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPostBuild, mockState } from '../testing/mock-state';
import { AmendComponent } from './amend.component';
import { AmendSummaryTemplateComponent } from './summary/amend-summary-template.component';

describe('AmendComponent', () => {
  let component: AmendComponent;
  let fixture: ComponentFixture<AmendComponent>;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AmendComponent> {
    get amendsNeedList() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row');
    }

    get confirmCheckbox() {
      return this.query<HTMLInputElement>('input#changes-0');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AmendComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('issuance', () => {
    let store: PermitIssuanceStore;
    const route = new ActivatedRouteStub({ taskId: '1', section: 'fuels' });

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [AmendComponent, AmendSummaryTemplateComponent],
        imports: [RouterTestingModule, SharedModule, SharedPermitModule],
        providers: [
          { provide: TasksService, useValue: tasksService },
          { provide: ActivatedRoute, useValue: route },
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
        ],
      }).compileComponents();
    });

    describe('amend permit issuance', () => {
      beforeEach(async () => {
        store = TestBed.inject(PermitIssuanceStore);
        store.setState({
          ...mockState,
          reviewGroupDecisions: {
            FUELS_AND_EQUIPMENT: {
              type: 'OPERATOR_AMENDS_NEEDED',
              details: {
                requiredChanges: [{ reason: 'Changes required' }],
                notes: 'notes',
              },
            },
          },
        });
      });

      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should successfully submit task and navigate', () => {
        const navigateSpy = jest.spyOn(router, 'navigate');
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        expect(page.confirmCheckbox.checked).toBeFalsy();

        page.submitButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeTruthy();
        expect(page.errorSummaryList).toEqual([
          'Check the box to confirm you have made changes and want to mark as complete',
        ]);

        page.confirmCheckbox.click();
        page.submitButton.click();
        fixture.detectChanges();

        expect(page.confirmCheckbox.checked).toBeTruthy();
        expect(page.errorSummary).toBeFalsy();
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(mockPostBuild({}, { AMEND_fuels: [true] }));
        expect(navigateSpy).toHaveBeenCalledTimes(1);
        expect(navigateSpy).toHaveBeenCalledWith(['../../amend/fuels/summary'], { relativeTo: route });
      });
    });
  });

  describe('variation', () => {
    let store: PermitVariationStore;
    const route = new ActivatedRouteStub({ taskId: '1', section: 'about-variation' });

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [AmendComponent, AmendSummaryTemplateComponent],
        imports: [RouterTestingModule, SharedModule, SharedPermitModule],
        providers: [
          { provide: TasksService, useValue: tasksService },
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
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should successfully submit task and navigate', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      expect(page.confirmCheckbox.checked).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual([
        'Check the box to confirm you have made changes and want to mark as complete',
      ]);

      page.confirmCheckbox.click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.confirmCheckbox.checked).toBeTruthy();
      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_VARIATION_SAVE_APPLICATION_AMEND',
        requestTaskId: mockPermitVariationSubmitOperatorLedPayload.requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_VARIATION_SAVE_APPLICATION_AMEND_PAYLOAD',
          permit: mockPermitVariationSubmitOperatorLedPayload.permit,
          permitSectionsCompleted: mockPermitVariationSubmitOperatorLedPayload.permitSectionsCompleted,
          reviewSectionsCompleted: mockPermitVariationSubmitOperatorLedPayload.reviewSectionsCompleted,
          permitVariationDetails: {},
          permitVariationDetailsCompleted: false,
          permitVariationDetailsAmendCompleted: true,
        },
      } as RequestTaskActionPayload);
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../../amend/about-variation/summary'], { relativeTo: route });
    });
  });
});
