import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { noSelection } from '@tasks/aer/verification-submit/non-conformities/errors/non-conformities-validation.errors';
import { NonConformitiesModule } from '@tasks/aer/verification-submit/non-conformities/non-conformities.module';
import { PerPlanComponent } from '@tasks/aer/verification-submit/non-conformities/per-plan/per-plan.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('PerPlanComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: PerPlanComponent;
  let fixture: ComponentFixture<PerPlanComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<PerPlanComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get nonConformitiesButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="areThereUncorrectedNonConformities"]');
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
    fixture = TestBed.createComponent(PerPlanComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonConformitiesModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for new non-conformities-per-plan details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          uncorrectedNonConformities: {},
        }),
      );
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual(
        'Have there been any uncorrected non-conformities with the approved monitoring plan?',
      );
      expect(page.nonConformitiesButtons).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noSelection]);
      expect(page.errorSummaryListContents.length).toEqual(1);
    });

    it('should submit a valid form and navigate to `per-plan`', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents.length).toEqual(1);

      page.nonConformitiesButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            uncorrectedNonConformities: {
              areThereUncorrectedNonConformities: true,
            },
          },
          { uncorrectedNonConformities: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['per-plan', 0], { relativeTo: activatedRoute });
    });
  });

  describe('for existing non-conformities-per-plan details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          uncorrectedNonConformities: {
            areThereUncorrectedNonConformities: true,
          },
        }),
      );
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual(
        'Have there been any uncorrected non-conformities with the approved monitoring plan?',
      );
      expect(page.nonConformitiesButtons).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should submit a valid form and navigate to `per-plan`', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith(['per-plan', 0], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to `previous-year`', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.nonConformitiesButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            uncorrectedNonConformities: {
              areThereUncorrectedNonConformities: false,
              uncorrectedNonConformities: null,
            },
          },
          { uncorrectedNonConformities: [false] },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['previous-year'], { relativeTo: activatedRoute });
    });
  });
});
