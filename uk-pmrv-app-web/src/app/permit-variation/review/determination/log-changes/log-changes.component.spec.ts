import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { DestroySubject } from '../../../../core/services/destroy-subject.service';
import { SharedPermitModule } from '../../../../permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '../../../../permit-application/store/permit-application.store';
import {} from '../../../../permit-application/testing/mock-state';
import { SharedModule } from '../../../../shared/shared.module';
import { PermitVariationStore } from '../../../store/permit-variation.store';
import {
  mockPermitVariationRegulatorLedPayload,
  mockPermitVariationReviewOperatorLedPayload,
  mockVariationDeterminationPostBuild,
  mockVariationRegulatorLedDeterminationPostBuild,
} from '../../../testing/mock';
import { LogChangesComponent } from './log-changes.component';

describe('LogChangesComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitVariationStore;
  let component: LogChangesComponent;
  let fixture: ComponentFixture<LogChangesComponent>;
  const route = new ActivatedRouteStub({}, {}, { statusKey: 'determination' });

  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);

  class Page extends BasePage<LogChangesComponent> {
    get logChanges() {
      return this.getInputValue('#logChanges');
    }

    set logChanges(value: string) {
      this.setInputValue('#logChanges', value);
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
    fixture = TestBed.createComponent(LogChangesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      declarations: [LogChangesComponent],
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

  describe('log changes for variation task', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationReviewOperatorLedPayload,
        determination: {
          type: 'GRANTED',
          reason: 'reason',
          activationDate: '1-1-2030',
        },
        reviewSectionsCompleted: { determination: false },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('variation task should submit a valid form that submits the variation payload', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.logChanges = 'log changes';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockVariationDeterminationPostBuild(
          {
            type: 'GRANTED',
            reason: 'reason',
            activationDate: '1-1-2030',
            logChanges: 'log changes',
          },
          {
            determination: false,
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });
  });

  describe('log changes for variation regulator led task', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitVariationStore);
      store.setState({
        ...mockPermitVariationRegulatorLedPayload,
        permitVariationDetails: {
          reason: 'reason',
          modifications: [{ type: 'CALCULATION_TO_MEASUREMENT_METHODOLOGIES' }],
        },
        permitVariationDetailsCompleted: true,
        determination: {
          reason: 'reason',
          activationDate: '2023-01-01',
          reasonTemplate: 'FOLLOWING_IMPROVEMENT_REPORT_BY_OPERATOR',
        },
        reviewSectionsCompleted: { determination: false },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form that submits the variation payload', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.logChanges = 'log changes';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockVariationRegulatorLedDeterminationPostBuild(
          {
            reason: 'reason',
            activationDate: '2023-01-01',
            reasonTemplate: 'FOLLOWING_IMPROVEMENT_REPORT_BY_OPERATOR',
            logChanges: 'log changes',
          },
          {
            determination: false,
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });
  });
});
