import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { SummaryDetailsComponent as PermitVariationSummaryDetails } from '../../../../permit-variation/shared/review/determination/summary-details.component';
import { PermitVariationStore } from '../../../../permit-variation/store/permit-variation.store';
import {
  mockPermitVariationRegulatorLedPayload,
  mockPermitVariationReviewOperatorLedPayload,
  mockVariationDeterminationPostBuild,
  mockVariationRegulatorLedDeterminationPostBuild,
} from '../../../../permit-variation/testing/mock';
import { SharedModule } from '../../../../shared/shared.module';
import { ReviewDeterminationSummaryDetailsComponent } from '../../../shared/review-determination-summary-details/summary-details.component';
import { SharedPermitModule } from '../../../shared/shared-permit.module';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockDeterminationPostBuild, mockReviewStateBuild } from '../../../testing/mock-state';
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: AnswersComponent;
  let fixture: ComponentFixture<AnswersComponent>;
  const route = new ActivatedRouteStub({}, {}, { statusKey: 'determination' });

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AnswersComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
    get confirmAndCompleteButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AnswersComponent);
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
        declarations: [AnswersComponent, ReviewDeterminationSummaryDetailsComponent, PermitVariationSummaryDetails],
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

    describe('for determination GRANTED', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState(
          mockReviewStateBuild(
            {
              type: 'GRANTED',
              reason: 'reason',
              activationDate: new Date('2030-01-01T00:00:00Z').toISOString(),
            },
            {
              determination: false,
            },
          ),
        );
      });
      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should display the GRANTED determination', () => {
        expect(page.summaryDefinitions).toEqual(['Grant', 'Change', 'reason', 'Change', '1 Jan 2030', 'Change']);
      });

      it('should submit the form', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        page.confirmAndCompleteButton.click();
        fixture.detectChanges();

        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
          mockDeterminationPostBuild(
            {
              type: 'GRANTED',
              reason: 'reason',
              activationDate: new Date('2030-01-01T00:00:00Z').toISOString(),
            },
            {
              determination: true,
            },
          ),
        );

        expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: route, state: { notification: true } });
      });
    });
  });

  describe('permit variation', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [RouterTestingModule, SharedModule, SharedPermitModule],
        declarations: [AnswersComponent, ReviewDeterminationSummaryDetailsComponent, PermitVariationSummaryDetails],
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

    describe('for determination GRANTED for variation task', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockPermitVariationReviewOperatorLedPayload,
          determination: {
            type: 'GRANTED',
            reason: 'reason',
            activationDate: new Date('2030-01-01T00:00:00Z').toISOString(),
            logChanges: 'log changes',
          },
          reviewSectionsCompleted: { determination: false },
        });
      });
      beforeEach(createComponent);

      it('should display the GRANTED determination', () => {
        expect(page.summaryDefinitions).toEqual([
          'Approve',
          'Change',
          'reason',
          'Change',
          '1 Jan 2030',
          'Change',
          'log changes',
          'Change',
        ]);
      });

      it('should submit the form', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        page.confirmAndCompleteButton.click();
        fixture.detectChanges();

        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
          mockVariationDeterminationPostBuild(
            {
              type: 'GRANTED',
              reason: 'reason',
              activationDate: new Date('2030-01-01T00:00:00Z').toISOString(),
              logChanges: 'log changes',
            },
            {
              determination: true,
            },
          ),
        );

        expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: route, state: { notification: true } });
      });
    });

    describe('for determination for variation regulator led task', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        (store as PermitVariationStore).setState({
          ...mockPermitVariationRegulatorLedPayload,
          permitVariationDetails: {
            reason: 'reason',
            modifications: [{ type: 'CALCULATION_TO_MEASUREMENT_METHODOLOGIES' }],
          },
          permitVariationDetailsCompleted: true,
          determination: {
            reason: 'reason',
            activationDate: new Date('2030-01-01T00:00:00Z').toISOString(),
            reasonTemplate: 'WHERE_OPERATOR_FAILED_TO_APPLY_IN_ACCORDANCE_WITH_CONDITIONS',
            logChanges: 'logChanges',
          },
          reviewSectionsCompleted: { determination: false },
        });
      });
      beforeEach(createComponent);

      it('should display the determination', () => {
        expect(page.summaryDefinitions).toEqual([
          'reason',
          'Change',
          '1 Jan 2030',
          'Change',
          'Where the operator failed to apply in accordance with conditions',
          'Change',
          'logChanges',
          'Change',
        ]);
      });

      it('should submit the form', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        page.confirmAndCompleteButton.click();
        fixture.detectChanges();

        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
          mockVariationRegulatorLedDeterminationPostBuild(
            {
              reason: 'reason',
              activationDate: new Date('2030-01-01T00:00:00Z').toISOString(),
              reasonTemplate: 'WHERE_OPERATOR_FAILED_TO_APPLY_IN_ACCORDANCE_WITH_CONDITIONS',
              logChanges: 'logChanges',
            },
            {
              determination: true,
            },
          ),
        );

        expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: route, state: { notification: true } });
      });
    });
  });
});
