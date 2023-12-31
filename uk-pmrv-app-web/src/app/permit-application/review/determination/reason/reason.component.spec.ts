import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { DestroySubject } from '../../../../core/services/destroy-subject.service';
import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { PermitVariationStore } from '../../../../permit-variation/store/permit-variation.store';
import {
  mockPermitVariationReviewOperatorLedPayload,
  mockVariationDeterminationPostBuild,
} from '../../../../permit-variation/testing/mock';
import { SharedModule } from '../../../../shared/shared.module';
import { SharedPermitModule } from '../../../shared/shared-permit.module';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockDeterminationPostBuild, mockReviewState } from '../../../testing/mock-state';
import { ReasonComponent } from './reason.component';

describe('ReasonComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: ReasonComponent;
  let fixture: ComponentFixture<ReasonComponent>;
  const route = new ActivatedRouteStub({}, {}, { statusKey: 'determination' });

  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);

  class Page extends BasePage<ReasonComponent> {
    get reason() {
      return this.getInputValue('#reason');
    }

    set reason(value: string) {
      this.setInputValue('#reason', value);
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
    fixture = TestBed.createComponent(ReasonComponent);
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
        declarations: [ReasonComponent],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          { provide: TasksService, useValue: tasksService },
          { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
          {
            provide: PermitApplicationStore,
            useExisting: PermitIssuanceStore,
          },
          DestroySubject,
        ],
      }).compileComponents();
    });

    describe('for reason input GRANTED', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockReviewState,
          determination: {
            type: 'GRANTED',
          },
        });
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
        expect(page.errorSummaryErrorList).toEqual(['Enter a reason to support your decision.']);

        page.reason = 'reason';
        page.submitButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeFalsy();
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
          mockDeterminationPostBuild(
            {
              type: 'GRANTED',
              reason: 'reason',
            },
            {
              determination: false,
            },
          ),
        );

        expect(navigateSpy).toHaveBeenCalledWith(['../activation-date'], { relativeTo: route });
      });
    });

    describe('for reason input REJECTED', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockReviewState,
          determination: {
            type: 'REJECTED',
          },
        });
      });
      beforeEach(createComponent);

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should submit a valid form', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        page.reason = 'reason';
        page.submitButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeFalsy();
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
          mockDeterminationPostBuild(
            {
              type: 'REJECTED',
              reason: 'reason',
            },
            {
              determination: false,
            },
          ),
        );

        expect(navigateSpy).toHaveBeenCalledWith(['../official-notice'], { relativeTo: route });
      });
    });
  });

  describe('permit variation', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [RouterTestingModule, SharedModule, SharedPermitModule],
        declarations: [ReasonComponent],
        providers: [
          { provide: ActivatedRoute, useValue: route },
          { provide: TasksService, useValue: tasksService },
          { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
          {
            provide: PermitApplicationStore,
            useExisting: PermitVariationStore,
          },
          DestroySubject,
        ],
      }).compileComponents();
    });

    describe('for variation task and type GRANTED', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState({
          ...mockPermitVariationReviewOperatorLedPayload,
          determination: {
            type: 'GRANTED',
          },
          reviewSectionsCompleted: { determination: false },
        });
      });
      beforeEach(createComponent);

      it('variation task should submit a valid form that submits the variation payload', () => {
        tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        page.reason = 'reason';
        page.submitButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeFalsy();
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
          mockVariationDeterminationPostBuild(
            {
              type: 'GRANTED',
              reason: 'reason',
            },
            {
              determination: false,
            },
          ),
        );

        expect(navigateSpy).toHaveBeenCalledWith(['../activation-date'], { relativeTo: route });
      });
    });
  });
});
