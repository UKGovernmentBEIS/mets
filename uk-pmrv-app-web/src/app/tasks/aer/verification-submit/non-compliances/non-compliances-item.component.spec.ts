import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import {
  noNonComplianceExplanation,
  noSelection,
} from '@tasks/aer/verification-submit/non-compliances/errors/non-compliances-validation.errors';
import { NonCompliancesModule } from '@tasks/aer/verification-submit/non-compliances/non-compliances.module';
import { NonCompliancesItemComponent } from '@tasks/aer/verification-submit/non-compliances/non-compliances-item.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('NonCompliancesItemComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: NonCompliancesItemComponent;
  let fixture: ComponentFixture<NonCompliancesItemComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' });

  class Page extends BasePage<NonCompliancesItemComponent> {
    get explanationValue() {
      return this.getInputValue('#explanation');
    }

    set explanationValue(value: string) {
      this.setInputValue('#explanation', value);
    }

    get radioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="materialEffect"]');
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
    fixture = TestBed.createComponent(NonCompliancesItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonCompliancesModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new item', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          uncorrectedNonCompliances: {
            areThereUncorrectedNonCompliances: true,
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display an empty form', () => {
      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit a valid form and navigate to list', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual([noNonComplianceExplanation, noSelection]);

      page.explanationValue = 'Explanation 1';
      page.radioButtons[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            uncorrectedNonCompliances: {
              areThereUncorrectedNonCompliances: true,
              uncorrectedNonCompliances: [
                {
                  reference: 'C1',
                  explanation: 'Explanation 1',
                  materialEffect: false,
                },
              ],
            },
          },
          { uncorrectedNonCompliances: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../list'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing item', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          uncorrectedNonCompliances: {
            areThereUncorrectedNonCompliances: true,
            uncorrectedNonCompliances: [
              {
                reference: 'C1',
                explanation: 'Explanation 1',
                materialEffect: false,
              },
            ],
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form and navigate to list', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith(['../list'], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to list', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.explanationValue).toEqual('Explanation 1');
      expect(page.errorSummary).toBeFalsy();

      page.explanationValue = 'New explanation 1';
      page.radioButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            uncorrectedNonCompliances: {
              areThereUncorrectedNonCompliances: true,
              uncorrectedNonCompliances: [
                {
                  reference: 'C1',
                  explanation: 'New explanation 1',
                  materialEffect: true,
                },
              ],
            },
          },
          { uncorrectedNonCompliances: [false] },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../list'], { relativeTo: activatedRoute });
    });
  });
});
