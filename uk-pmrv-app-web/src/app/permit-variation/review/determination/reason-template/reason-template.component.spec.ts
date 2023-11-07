import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { DestroySubject } from '../../../../core/services/destroy-subject.service';
import { SharedPermitModule } from '../../../../permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '../../../../permit-application/store/permit-application.store';
import { SharedModule } from '../../../../shared/shared.module';
import { PermitVariationStore } from '../../../store/permit-variation.store';
import {
  mockPermitVariationRegulatorLedPayload,
  mockVariationRegulatorLedDeterminationPostBuild,
} from '../../../testing/mock';
import { ReasonTemplateComponent } from './reason-template.component';

describe('ReasonTemplateComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitVariationStore;
  let component: ReasonTemplateComponent;
  let fixture: ComponentFixture<ReasonTemplateComponent>;
  const route = new ActivatedRouteStub({}, {}, { statusKey: 'determination' });

  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);

  class Page extends BasePage<ReasonTemplateComponent> {
    get reasonTemplate1Option() {
      return this.query<HTMLInputElement>('#reasonTemplate-option1');
    }
    get reasonTemplateOtherOption() {
      return this.query<HTMLInputElement>('#reasonTemplate-option5');
    }
    get reasonTemplateOtherSummary() {
      return this.getInputValue('#reasonTemplateOtherSummary');
    }
    set reasonTemplateOtherSummary(value: string) {
      this.setInputValue('#reasonTemplateOtherSummary', value);
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
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
    fixture = TestBed.createComponent(ReasonTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      declarations: [ReasonTemplateComponent],
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

  describe('reason template', () => {
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
        },
        reviewSectionsCompleted: { determination: false },
      });
    });
    beforeEach(createComponent);

    it('should be created', () => {
      expect(component).toBeTruthy();
    });

    it('should submit the reason template', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select a reason template']);

      page.reasonTemplate1Option.click();
      fixture.detectChanges();

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
            reasonTemplateOtherSummary: null,
          },
          {
            determination: false,
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../log-changes'], { relativeTo: route });
    });

    it('should submit the reason template and other reason when other is selected', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select a reason template']);

      page.reasonTemplateOtherOption.click();
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter the reason for the decision']);

      page.reasonTemplateOtherSummary = 'other reason';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockVariationRegulatorLedDeterminationPostBuild(
          {
            reason: 'reason',
            activationDate: '2023-01-01',
            reasonTemplate: 'OTHER',
            reasonTemplateOtherSummary: 'other reason',
          },
          {
            determination: false,
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../log-changes'], { relativeTo: route });
    });
  });
});
