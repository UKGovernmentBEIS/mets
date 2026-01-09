import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { PermitVariationStore } from '../../../../permit-variation/store/permit-variation.store';
import {
  mockPermitVariationRegulatorLedPayload,
  mockPermitVariationReviewOperatorLedPayload,
  mockVariationDeterminationPostBuild,
  mockVariationRegulatorLedDeterminationPostBuild,
} from '../../../../permit-variation/testing/mock';
import { SharedPermitModule } from '../../../shared/shared-permit.module';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockState } from '../../../testing/mock-state';
import { ActivationDateComponent } from './activation-date.component';

describe('ActivationDateComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: ActivationDateComponent;
  let fixture: ComponentFixture<ActivationDateComponent>;
  const route = new ActivatedRouteStub({}, {}, { statusKey: 'determination' });

  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);

  class Page extends BasePage<ActivationDateComponent> {
    set activationDateDay(value: string) {
      this.setInputValue('#activationDate-day', value);
    }

    set activationDateMonth(value: string) {
      this.setInputValue('#activationDate-month', value);
    }

    set activationDateYear(value: string) {
      this.setInputValue('#activationDate-year', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ActivationDateComponent);
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
        declarations: [ActivationDateComponent],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          { provide: TasksService, useValue: tasksService },
          { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
        ],
      }).compileComponents();
    });

    describe('for activation date input', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState(mockState);
      });
      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should submit a valid form', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        expect(page.errorSummary).toBeFalsy();

        page.submitButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeTruthy();
        expect(page.errorSummaryErrorList).toEqual(['Enter a date']);

        page.activationDateYear = new Date().getFullYear() + 1 + '';
        page.activationDateMonth = '1';
        page.activationDateDay = '1';
        page.submitButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeFalsy();
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);

        expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
      });
    });
  });

  describe('permit variation', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [RouterTestingModule, SharedModule, SharedPermitModule],
        declarations: [ActivationDateComponent],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          { provide: TasksService, useValue: tasksService },
          { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
        ],
      }).compileComponents();
    });

    describe('for variation task activation date', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockPermitVariationReviewOperatorLedPayload,
          determination: {
            type: 'GRANTED',
            reason: 'reason',
          },
          reviewSectionsCompleted: { determination: false },
        });
      });
      beforeEach(createComponent);

      it('should submit a valid variation form', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        page.activationDateYear = new Date().getFullYear() + 1 + '';
        page.activationDateMonth = '1';
        page.activationDateDay = '1';
        page.submitButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeFalsy();
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
          mockVariationDeterminationPostBuild(
            {
              type: 'GRANTED',
              reason: 'reason',
              activationDate: new Date(`${new Date().getFullYear() + 1}-01-01T00:00:00.000Z`),
            },
            {
              determination: false,
            },
          ),
        );

        expect(navigateSpy).toHaveBeenCalledWith(['../log-changes'], { relativeTo: route });
      });
    });

    describe('for HSE variation task activation date', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockPermitVariationReviewOperatorLedPayload,
          permitType: 'HSE',
          determination: {
            type: 'GRANTED',
            reason: 'reason',
          },
          reviewSectionsCompleted: { determination: false },
        });
      });
      beforeEach(createComponent);

      it('should submit a valid variation form and redirect to emissions wizard step', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        page.activationDateYear = new Date().getFullYear() + 1 + '';
        page.activationDateMonth = '1';
        page.activationDateDay = '1';
        page.submitButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeFalsy();
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
          mockVariationDeterminationPostBuild(
            {
              type: 'GRANTED',
              reason: 'reason',
              activationDate: new Date(`${new Date().getFullYear() + 1}-01-01T00:00:00.000Z`),
            },
            {
              determination: false,
            },
          ),
        );

        expect(navigateSpy).toHaveBeenCalledWith(['../emissions'], { relativeTo: route });
      });
    });

    describe('for variation regulator led task activation date', () => {
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
          },
          reviewSectionsCompleted: { determination: false },
        });
      });
      beforeEach(createComponent);

      it('should submit a valid variation form and redirect to reason template', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        page.activationDateYear = new Date().getFullYear() + 1 + '';
        page.activationDateMonth = '1';
        page.activationDateDay = '1';
        page.submitButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeFalsy();
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
          mockVariationRegulatorLedDeterminationPostBuild(
            {
              reason: 'reason',
              activationDate: new Date(`${new Date().getFullYear() + 1}-01-01T00:00:00.000Z`),
            },
            {
              determination: false,
            },
          ),
        );

        expect(navigateSpy).toHaveBeenCalledWith(['../reason-template'], { relativeTo: route });
      });
    });
  });
});
