import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
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
import { mockState } from '../../../testing/mock-state';
import { FirstYearComponent } from './first-year.component';

describe('FirstYearComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: FirstYearComponent;
  let fixture: ComponentFixture<FirstYearComponent>;
  const route = new ActivatedRouteStub({}, {}, { statusKey: 'determination' });

  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);

  class Page extends BasePage<FirstYearComponent> {
    set firstYearOfReportingObligation(value: string) {
      this.setInputValue('#firstYearOfReportingObligation', value);
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
    fixture = TestBed.createComponent(FirstYearComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('permit issuance', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [SharedModule, SharedPermitModule],
        declarations: [FirstYearComponent],
        providers: [
          provideRouter([]),
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

    describe('for first year input', () => {
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
        expect(page.errorSummaryErrorList).toEqual(['Enter the first year of Registry reporting obligation']);

        page.firstYearOfReportingObligation = '2022';

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
        imports: [SharedModule, SharedPermitModule],
        declarations: [FirstYearComponent],
        providers: [
          provideRouter([]),
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

    describe('for variation task first year', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockPermitVariationReviewOperatorLedPayload,
          determination: {
            type: 'GRANTED',
            reason: 'reason',
            activationDate: '2023-06-01',
          },
          reviewSectionsCompleted: { determination: false },
        });
      });
      beforeEach(createComponent);

      it('should submit a valid variation form', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        page.firstYearOfReportingObligation = '2022';

        page.submitButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeFalsy();
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
          mockVariationDeterminationPostBuild(
            {
              type: 'GRANTED',
              reason: 'reason',
              activationDate: '2023-06-01',
              firstYearOfReportingObligation: 2022,
            },
            {
              determination: false,
            },
          ),
        );

        expect(navigateSpy).toHaveBeenCalledWith(['../log-changes'], { relativeTo: route });
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
            activationDate: '2023-06-01',
          },
          reviewSectionsCompleted: { determination: false },
        });
      });
      beforeEach(createComponent);

      it('should submit a valid variation form and redirect to reason template', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        page.firstYearOfReportingObligation = '2022';

        page.submitButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeFalsy();
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
          mockVariationRegulatorLedDeterminationPostBuild(
            {
              reason: 'reason',
              activationDate: '2023-06-01',
              firstYearOfReportingObligation: 2022,
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
