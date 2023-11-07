import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitVariationStore } from '../../../permit-variation/store/permit-variation.store';
import {
  mockPermitVariationCompleteAcceptedReviewGroupDecisions,
  mockPermitVariationReviewOperatorLedPayload,
  mockVariationDeterminationPostBuild,
} from '../../../permit-variation/testing/mock';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitCompletedReviewSectionsCompleted } from '../../testing/mock-permit-apply-action';
import { mockDeterminationPostBuild, mockReviewState } from '../../testing/mock-state';
import { DeterminationComponent } from './determination.component';

describe('DeterminationComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: DeterminationComponent;
  let fixture: ComponentFixture<DeterminationComponent>;
  const route = new ActivatedRouteStub({}, {}, { statusKey: 'determination' });

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<DeterminationComponent> {
    get buttons() {
      return this.queryAll<HTMLLIElement>('button');
    }

    get grantButton() {
      return this.buttons.filter((el) => el.innerHTML.trim() === 'Grant' || el.innerHTML.trim() === 'Approve')[0];
    }

    get rejectButton() {
      return this.buttons.filter((el) => el.innerHTML.trim() === 'Reject')[0];
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(DeterminationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('permit issuance', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [RouterTestingModule, SharedModule, SharedPermitModule],
        declarations: [DeterminationComponent],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          { provide: TasksService, useValue: tasksService },
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
        ],
      }).compileComponents();
    });

    describe('for GRANTED determination not allowed (missing mandatory group)', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockReviewState,
          reviewGroupDecisions: {
            FUELS_AND_EQUIPMENT: { type: 'ACCEPTED', notes: 'notes' },
            INSTALLATION_DETAILS: { type: 'ACCEPTED', notes: 'notes' },
            MANAGEMENT_PROCEDURES: { type: 'ACCEPTED', notes: 'notes' },
            MONITORING_METHODOLOGY_PLAN: { type: 'ACCEPTED', notes: 'notes' },
            ADDITIONAL_INFORMATION: { type: 'ACCEPTED', notes: 'notes' },
            DEFINE_MONITORING_APPROACHES: { type: 'ACCEPTED', notes: 'notes' },
            UNCERTAINTY_ANALYSIS: { type: 'ACCEPTED', notes: 'notes' },
            CALCULATION_CO2: { type: 'ACCEPTED', notes: 'notes' },
            CALCULATION_PFC: { type: 'ACCEPTED', notes: 'notes' },
            MEASUREMENT_N2O: { type: 'ACCEPTED', notes: 'notes' },
            INHERENT_CO2: { type: 'ACCEPTED', notes: 'notes' },
            TRANSFERRED_CO2_N2O: { type: 'ACCEPTED', notes: 'notes' },
            FALLBACK: { type: 'ACCEPTED', notes: 'notes' },
            MEASUREMENT_CO2: { type: 'ACCEPTED', notes: 'notes' },
          },
          reviewSectionsCompleted: {
            FUELS_AND_EQUIPMENT: true,
            INSTALLATION_DETAILS: true,
            MANAGEMENT_PROCEDURES: true,
            MONITORING_METHODOLOGY_PLAN: true,
            ADDITIONAL_INFORMATION: true,
            DEFINE_MONITORING_APPROACHES: true,
            UNCERTAINTY_ANALYSIS: true,
            CALCULATION_CO2: true,
            CALCULATION_PFC: true,
            MEASUREMENT_N2O: true,
            INHERENT_CO2: true,
            TRANSFERRED_CO2_N2O: true,
            FALLBACK: true,
            MEASUREMENT_CO2: true,
          },
        });
      });
      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should show only deem withdrawn button', () => {
        expect(page.buttons.length).toEqual(1);
        expect(page.buttons[0].innerHTML.trim()).toEqual('Deem withdrawn');
      });
    });

    describe('for GRANTED determination allowed', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockReviewState,
          reviewGroupDecisions: {
            PERMIT_TYPE: { type: 'ACCEPTED', notes: 'notes' },
            CONFIDENTIALITY_STATEMENT: { type: 'ACCEPTED', notes: 'notes' },
            FUELS_AND_EQUIPMENT: { type: 'ACCEPTED', notes: 'notes' },
            INSTALLATION_DETAILS: { type: 'ACCEPTED', notes: 'notes' },
            MANAGEMENT_PROCEDURES: { type: 'ACCEPTED', notes: 'notes' },
            MONITORING_METHODOLOGY_PLAN: { type: 'ACCEPTED', notes: 'notes' },
            ADDITIONAL_INFORMATION: { type: 'ACCEPTED', notes: 'notes' },
            DEFINE_MONITORING_APPROACHES: { type: 'ACCEPTED', notes: 'notes' },
            UNCERTAINTY_ANALYSIS: { type: 'ACCEPTED', notes: 'notes' },
            CALCULATION_CO2: { type: 'ACCEPTED', notes: 'notes' },
            CALCULATION_PFC: { type: 'ACCEPTED', notes: 'notes' },
            MEASUREMENT_N2O: { type: 'ACCEPTED', notes: 'notes' },
            INHERENT_CO2: { type: 'ACCEPTED', notes: 'notes' },
            TRANSFERRED_CO2_N2O: { type: 'ACCEPTED', notes: 'notes' },
            FALLBACK: { type: 'ACCEPTED', notes: 'notes' },
            MEASUREMENT_CO2: { type: 'ACCEPTED', notes: 'notes' },
          },
          reviewSectionsCompleted: {
            PERMIT_TYPE: true,
            CONFIDENTIALITY_STATEMENT: true,
            FUELS_AND_EQUIPMENT: true,
            INSTALLATION_DETAILS: true,
            MANAGEMENT_PROCEDURES: true,
            MONITORING_METHODOLOGY_PLAN: true,
            ADDITIONAL_INFORMATION: true,
            DEFINE_MONITORING_APPROACHES: true,
            UNCERTAINTY_ANALYSIS: true,
            CALCULATION_CO2: true,
            CALCULATION_PFC: true,
            MEASUREMENT_N2O: true,
            INHERENT_CO2: true,
            TRANSFERRED_CO2_N2O: true,
            FALLBACK: true,
            MEASUREMENT_CO2: true,
          },
        });
      });
      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should show grant and deem withdrawn button', () => {
        expect(page.buttons.length).toEqual(2);
        expect(page.buttons[0].innerHTML.trim()).toEqual('Grant');
        expect(page.buttons[1].innerHTML.trim()).toEqual('Deem withdrawn');
      });
    });

    describe('for REJECTED determination allowed', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockReviewState,
          reviewGroupDecisions: {
            PERMIT_TYPE: { type: 'ACCEPTED', notes: 'notes' },
            CONFIDENTIALITY_STATEMENT: { type: 'REJECTED', notes: 'notes' },
            FUELS_AND_EQUIPMENT: { type: 'ACCEPTED', notes: 'notes' },
            INSTALLATION_DETAILS: { type: 'ACCEPTED', notes: 'notes' },
            MANAGEMENT_PROCEDURES: { type: 'ACCEPTED', notes: 'notes' },
            MONITORING_METHODOLOGY_PLAN: { type: 'ACCEPTED', notes: 'notes' },
            ADDITIONAL_INFORMATION: { type: 'ACCEPTED', notes: 'notes' },
            DEFINE_MONITORING_APPROACHES: { type: 'ACCEPTED', notes: 'notes' },
            UNCERTAINTY_ANALYSIS: { type: 'ACCEPTED', notes: 'notes' },
            CALCULATION_CO2: { type: 'ACCEPTED', notes: 'notes' },
            CALCULATION_PFC: { type: 'ACCEPTED', notes: 'notes' },
            MEASUREMENT_N2O: { type: 'ACCEPTED', notes: 'notes' },
            INHERENT_CO2: { type: 'ACCEPTED', notes: 'notes' },
            TRANSFERRED_CO2_N2O: { type: 'ACCEPTED', notes: 'notes' },
            FALLBACK: { type: 'ACCEPTED', notes: 'notes' },
            MEASUREMENT_CO2: { type: 'ACCEPTED', notes: 'notes' },
          },
          reviewSectionsCompleted: {
            PERMIT_TYPE: true,
            CONFIDENTIALITY_STATEMENT: true,
            FUELS_AND_EQUIPMENT: true,
            INSTALLATION_DETAILS: true,
            MANAGEMENT_PROCEDURES: true,
            MONITORING_METHODOLOGY_PLAN: true,
            ADDITIONAL_INFORMATION: true,
            DEFINE_MONITORING_APPROACHES: true,
            UNCERTAINTY_ANALYSIS: true,
            CALCULATION_CO2: true,
            CALCULATION_PFC: true,
            MEASUREMENT_N2O: true,
            INHERENT_CO2: true,
            TRANSFERRED_CO2_N2O: true,
            FALLBACK: true,
            MEASUREMENT_CO2: true,
          },
        });
      });

      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should show only deem withdrawn button', () => {
        expect(page.buttons.length).toEqual(2);
        expect(page.buttons[0].innerHTML.trim()).toEqual('Reject');
        expect(page.buttons[1].innerHTML.trim()).toEqual('Deem withdrawn');
      });
    });

    describe('for posting permit determination GRANTED', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockReviewState,
          reviewGroupDecisions: {
            PERMIT_TYPE: { type: 'ACCEPTED', notes: 'notes' },
            CONFIDENTIALITY_STATEMENT: { type: 'ACCEPTED', notes: 'notes' },
            FUELS_AND_EQUIPMENT: { type: 'ACCEPTED', notes: 'notes' },
            INSTALLATION_DETAILS: { type: 'ACCEPTED', notes: 'notes' },
            MANAGEMENT_PROCEDURES: { type: 'ACCEPTED', notes: 'notes' },
            MONITORING_METHODOLOGY_PLAN: { type: 'ACCEPTED', notes: 'notes' },
            ADDITIONAL_INFORMATION: { type: 'ACCEPTED', notes: 'notes' },
            DEFINE_MONITORING_APPROACHES: { type: 'ACCEPTED', notes: 'notes' },
            UNCERTAINTY_ANALYSIS: { type: 'ACCEPTED', notes: 'notes' },
            CALCULATION_CO2: { type: 'ACCEPTED', notes: 'notes' },
            CALCULATION_PFC: { type: 'ACCEPTED', notes: 'notes' },
            MEASUREMENT_N2O: { type: 'ACCEPTED', notes: 'notes' },
            INHERENT_CO2: { type: 'ACCEPTED', notes: 'notes' },
            TRANSFERRED_CO2_N2O: { type: 'ACCEPTED', notes: 'notes' },
            FALLBACK: { type: 'ACCEPTED', notes: 'notes' },
            MEASUREMENT_CO2: { type: 'ACCEPTED', notes: 'notes' },
          },
          reviewSectionsCompleted: {
            PERMIT_TYPE: true,
            CONFIDENTIALITY_STATEMENT: true,
            FUELS_AND_EQUIPMENT: true,
            INSTALLATION_DETAILS: true,
            MANAGEMENT_PROCEDURES: true,
            MONITORING_METHODOLOGY_PLAN: true,
            ADDITIONAL_INFORMATION: true,
            DEFINE_MONITORING_APPROACHES: true,
            UNCERTAINTY_ANALYSIS: true,
            CALCULATION_CO2: true,
            CALCULATION_PFC: true,
            MEASUREMENT_N2O: true,
            INHERENT_CO2: true,
            TRANSFERRED_CO2_N2O: true,
            FALLBACK: true,
            MEASUREMENT_CO2: true,
          },
        });
      });
      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should post a permit determination GRANTED', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        page.grantButton.click();
        fixture.detectChanges();

        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
          ...mockDeterminationPostBuild(
            { type: 'GRANTED' },
            {
              PERMIT_TYPE: true,
              CONFIDENTIALITY_STATEMENT: true,
              FUELS_AND_EQUIPMENT: true,
              INSTALLATION_DETAILS: true,
              MANAGEMENT_PROCEDURES: true,
              MONITORING_METHODOLOGY_PLAN: true,
              ADDITIONAL_INFORMATION: true,
              DEFINE_MONITORING_APPROACHES: true,
              UNCERTAINTY_ANALYSIS: true,
              CALCULATION_CO2: true,
              CALCULATION_PFC: true,
              MEASUREMENT_N2O: true,
              INHERENT_CO2: true,
              TRANSFERRED_CO2_N2O: true,
              FALLBACK: true,
              MEASUREMENT_CO2: true,
              determination: false,
            },
          ),
        });

        expect(navigateSpy).toHaveBeenCalledWith(['reason'], { relativeTo: route });
      });
    });

    describe('for posting permit determination REJECTED', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockReviewState,
          reviewGroupDecisions: {
            PERMIT_TYPE: { type: 'ACCEPTED', notes: 'notes' },
            CONFIDENTIALITY_STATEMENT: { type: 'REJECTED', notes: 'notes' },
            FUELS_AND_EQUIPMENT: { type: 'ACCEPTED', notes: 'notes' },
            INSTALLATION_DETAILS: { type: 'ACCEPTED', notes: 'notes' },
            MANAGEMENT_PROCEDURES: { type: 'ACCEPTED', notes: 'notes' },
            MONITORING_METHODOLOGY_PLAN: { type: 'ACCEPTED', notes: 'notes' },
            ADDITIONAL_INFORMATION: { type: 'ACCEPTED', notes: 'notes' },
            DEFINE_MONITORING_APPROACHES: { type: 'ACCEPTED', notes: 'notes' },
            UNCERTAINTY_ANALYSIS: { type: 'ACCEPTED', notes: 'notes' },
            CALCULATION_CO2: { type: 'ACCEPTED', notes: 'notes' },
            CALCULATION_PFC: { type: 'ACCEPTED', notes: 'notes' },
            MEASUREMENT_N2O: { type: 'ACCEPTED', notes: 'notes' },
            INHERENT_CO2: { type: 'ACCEPTED', notes: 'notes' },
            TRANSFERRED_CO2_N2O: { type: 'ACCEPTED', notes: 'notes' },
            FALLBACK: { type: 'ACCEPTED', notes: 'notes' },
            MEASUREMENT_CO2: { type: 'ACCEPTED', notes: 'notes' },
          },
          reviewSectionsCompleted: {
            PERMIT_TYPE: true,
            CONFIDENTIALITY_STATEMENT: true,
            FUELS_AND_EQUIPMENT: true,
            INSTALLATION_DETAILS: true,
            MANAGEMENT_PROCEDURES: true,
            MONITORING_METHODOLOGY_PLAN: true,
            ADDITIONAL_INFORMATION: true,
            DEFINE_MONITORING_APPROACHES: true,
            UNCERTAINTY_ANALYSIS: true,
            CALCULATION_CO2: true,
            CALCULATION_PFC: true,
            MEASUREMENT_N2O: true,
            INHERENT_CO2: true,
            TRANSFERRED_CO2_N2O: true,
            FALLBACK: true,
            MEASUREMENT_CO2: true,
          },
        });
      });
      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should post a permit determination REJECTED', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        page.rejectButton.click();
        fixture.detectChanges();

        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
          mockDeterminationPostBuild(
            { type: 'REJECTED' },
            {
              PERMIT_TYPE: true,
              CONFIDENTIALITY_STATEMENT: true,
              FUELS_AND_EQUIPMENT: true,
              INSTALLATION_DETAILS: true,
              MANAGEMENT_PROCEDURES: true,
              MONITORING_METHODOLOGY_PLAN: true,
              ADDITIONAL_INFORMATION: true,
              DEFINE_MONITORING_APPROACHES: true,
              UNCERTAINTY_ANALYSIS: true,
              CALCULATION_CO2: true,
              CALCULATION_PFC: true,
              MEASUREMENT_N2O: true,
              INHERENT_CO2: true,
              TRANSFERRED_CO2_N2O: true,
              FALLBACK: true,
              MEASUREMENT_CO2: true,
              determination: false,
            },
          ),
        );

        expect(navigateSpy).toHaveBeenCalledWith(['reason'], { relativeTo: route });
      });
    });
  });

  describe('permit variation', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [RouterTestingModule, SharedModule, SharedPermitModule],
        declarations: [DeterminationComponent],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          { provide: TasksService, useValue: tasksService },
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
        ],
      }).compileComponents();
    });

    describe('variation task for GRANTED determination not allowed when missing variation details decision', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        (store as PermitVariationStore).setState({
          ...mockPermitVariationReviewOperatorLedPayload,
          permitVariationDetailsReviewCompleted: undefined,
          reviewGroupDecisions: mockPermitVariationCompleteAcceptedReviewGroupDecisions,
          reviewSectionsCompleted: mockPermitCompletedReviewSectionsCompleted,
        });
      });
      beforeEach(createComponent);

      it('should show only deem withdrawn button', () => {
        expect(page.buttons.length).toEqual(1);
        expect(page.buttons[0].innerHTML.trim()).toEqual('Deem withdrawn');
      });
    });

    describe('variation task for GRANTED determination allowed should show Approve', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        (store as PermitVariationStore).setState({
          ...mockPermitVariationReviewOperatorLedPayload,
          permitVariationDetailsReviewDecision: {
            type: 'ACCEPTED',
            details: { notes: 'notes' },
          },
          permitVariationDetailsReviewCompleted: true,
          reviewGroupDecisions: mockPermitVariationCompleteAcceptedReviewGroupDecisions,
          reviewSectionsCompleted: mockPermitCompletedReviewSectionsCompleted,
        });
      });
      beforeEach(createComponent);

      it('should show approve and deem withdrawn button', () => {
        expect(page.buttons.length).toEqual(2);
        expect(page.buttons[0].innerHTML.trim()).toEqual('Approve');
        expect(page.buttons[1].innerHTML.trim()).toEqual('Deem withdrawn');
      });

      it('should post a permit determination GRANTED', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        page.grantButton.click();
        fixture.detectChanges();

        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
          ...mockVariationDeterminationPostBuild(
            { type: 'GRANTED' },
            {
              ...mockPermitCompletedReviewSectionsCompleted,
              determination: false,
            },
          ),
        });

        expect(navigateSpy).toHaveBeenCalledWith(['reason'], { relativeTo: route });
      });
    });

    describe('variation task for rejected determination not completed yet', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        (store as PermitVariationStore).setState({
          ...mockPermitVariationReviewOperatorLedPayload,
          permitVariationDetailsReviewDecision: undefined,
          permitVariationDetailsReviewCompleted: false,
          reviewGroupDecisions: {
            ...mockPermitVariationCompleteAcceptedReviewGroupDecisions,
            CONFIDENTIALITY_STATEMENT: { type: 'REJECTED', notes: 'notes' },
          },
          reviewSectionsCompleted: mockPermitCompletedReviewSectionsCompleted,
        });
      });
      beforeEach(createComponent);

      it('should show only deem withdrawn button', () => {
        expect(page.buttons.length).toEqual(1);
        expect(page.buttons[0].innerHTML.trim()).toEqual('Deem withdrawn');
      });
    });

    describe('variation task for rejected determination', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        (store as PermitVariationStore).setState({
          ...mockPermitVariationReviewOperatorLedPayload,
          permitVariationDetailsReviewDecision: {
            type: 'REJECTED',
            details: { notes: 'notes' },
          },
          permitVariationDetailsReviewCompleted: true,
          reviewGroupDecisions: mockPermitVariationCompleteAcceptedReviewGroupDecisions,
          reviewSectionsCompleted: mockPermitCompletedReviewSectionsCompleted,
        });
      });
      beforeEach(createComponent);

      it('should show reject and deem withdrawn button', () => {
        expect(page.buttons.length).toEqual(2);
        expect(page.buttons[0].innerHTML.trim()).toEqual('Reject');
        expect(page.buttons[1].innerHTML.trim()).toEqual('Deem withdrawn');
      });
    });

    describe('variation task for rejected determination with standard section not completed yet', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        (store as PermitVariationStore).setState({
          ...mockPermitVariationReviewOperatorLedPayload,
          permitVariationDetailsReviewDecision: {
            type: 'REJECTED',
            details: { notes: 'notes' },
          },
          permitVariationDetailsReviewCompleted: true,
          reviewGroupDecisions: mockPermitVariationCompleteAcceptedReviewGroupDecisions,
          reviewSectionsCompleted: {
            ...mockPermitCompletedReviewSectionsCompleted,
            CONFIDENTIALITY_STATEMENT: false,
          },
        });
      });
      beforeEach(createComponent);

      it('should show only deem withdrawn button', () => {
        expect(page.buttons.length).toEqual(1);
        expect(page.buttons[0].innerHTML.trim()).toEqual('Deem withdrawn');
      });
    });

    describe('at least one group is rejected and the variation details is amend needed', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        (store as PermitVariationStore).setState({
          ...mockPermitVariationReviewOperatorLedPayload,
          permitVariationDetailsReviewDecision: {
            type: 'OPERATOR_AMENDS_NEEDED',
            details: { notes: 'notes' },
          },
          permitVariationDetailsReviewCompleted: true,
          reviewGroupDecisions: {
            ...mockPermitVariationCompleteAcceptedReviewGroupDecisions,
            CONFIDENTIALITY_STATEMENT: { type: 'REJECTED', notes: 'notes' },
          },
          reviewSectionsCompleted: mockPermitCompletedReviewSectionsCompleted,
        });
      });
      beforeEach(createComponent);

      it('should show only deem withdrawn button', () => {
        expect(page.buttons.length).toEqual(1);
        expect(page.buttons[0].innerHTML.trim()).toEqual('Deem withdrawn');
      });
    });
  });
});
