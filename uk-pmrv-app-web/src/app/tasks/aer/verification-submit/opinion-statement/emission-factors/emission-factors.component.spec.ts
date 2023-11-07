import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { EmissionFactorsComponent } from '@tasks/aer/verification-submit/opinion-statement/emission-factors/emission-factors.component';
import { noEmissionFactorDescription } from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';
import { OpinionStatementModule } from '@tasks/aer/verification-submit/opinion-statement/opinion-statement.module';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import {
  mockState,
  mockVerificationApplyPayload,
} from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('EmissionFactorsComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: EmissionFactorsComponent;
  let fixture: ComponentFixture<EmissionFactorsComponent>;

  const tasksService = mockClass(TasksService);
  const expectedEmissionFactor = 'Test emission factor';

  class Page extends BasePage<EmissionFactorsComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    set emissionFactorsDescriptionValue(value: string) {
      this.setInputValue('#emissionFactorsDescription', value);
    }

    get inputElement(): HTMLTextAreaElement {
      return this.query<HTMLTextAreaElement>('textarea[name$="emissionFactorsDescription"]');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(EmissionFactorsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new emission factor', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          opinionStatement: {
            emissionFactorsDescription: null,
          },
        }),
      );
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Describe the emission factors used');
      expect(page.inputElement).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
      expect(page.errorSummary).toBeFalsy();
    });

    it('should display error on empty form submit', () => {
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noEmissionFactorDescription]);
      expect(page.errorSummaryListContents.length).toEqual(1);
    });

    it(`should submit a valid form and navigate to emission-sources`, () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents.length).toEqual(1);

      page.emissionFactorsDescriptionValue = expectedEmissionFactor;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            opinionStatement: {
              emissionFactorsDescription: expectedEmissionFactor,
            },
          },
          { opinionStatement: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../review-emissions'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing emission factor', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Describe the emission factors used');
      expect(page.inputElement).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
      expect(page.errorSummary).toBeFalsy();
    });

    it(`should submit a valid form and navigate to review-emissions`, () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith(['../review-emissions'], { relativeTo: activatedRoute });
    });

    it(`should edit, submit a valid form and navigate to review-emissions`, () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.emissionFactorsDescriptionValue = expectedEmissionFactor;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            opinionStatement: {
              ...mockVerificationApplyPayload.verificationReport.opinionStatement,
              emissionFactorsDescription: expectedEmissionFactor,
            },
          },
          { opinionStatement: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../review-emissions'], { relativeTo: activatedRoute });
    });
  });
});
