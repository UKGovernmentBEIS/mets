import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockPostBuild, mockStateBuild } from '@tasks/vir/comments-response/testing/mock-state';
import { mockStateRespond } from '@tasks/vir/comments-response/testing/mock-vir-application-respond-payload';
import { VirTaskCommentsResponseModule } from '@tasks/vir/comments-response/vir-task-comments-response.module';
import { noDate, noSelection } from '@tasks/vir/errors/validation-errors';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { OperatorFollowupComponent } from './operator-followup.component';

describe('OperatorFollowupComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: OperatorFollowupComponent;
  let fixture: ComponentFixture<OperatorFollowupComponent>;

  const currentItem = 'B1';
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub(
    { taskId: mockStateRespond.requestTaskItem.requestTask.id, id: currentItem },
    null,
    {
      reference: currentItem,
    },
  );
  const tasksService = mockClass(TasksService);
  const expectedDate = new Date('2023-12-01');
  const expectedNextRoute = '../../B1/summary';

  class Page extends BasePage<OperatorFollowupComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get verificationItem() {
      return this.query('app-verification-recommendation-item');
    }

    get operatorResponseItem() {
      return this.query('app-operator-response-item');
    }

    get regulatorResponseItem() {
      return this.query('app-regulator-response-item');
    }

    get improvementCompletedButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="improvementCompleted"]');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    set dateCompleted(date: Date) {
      this.setInputValue(`#dateCompleted-day`, date.getDate());
      this.setInputValue(`#dateCompleted-month`, date.getMonth() + 1);
      this.setInputValue(`#dateCompleted-year`, date.getFullYear());
    }

    set reason(value: string) {
      this.setInputValue('#reason', value);
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(OperatorFollowupComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirTaskCommentsResponseModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new operator-followup details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          operatorImprovementFollowUpResponses: {},
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
      expect(page.heading1.textContent.trim()).toEqual(`Respond to ${currentItem}`);
      expect(page.verificationItem).toBeTruthy();
      expect(page.operatorResponseItem).toBeTruthy();
      expect(page.regulatorResponseItem).toBeTruthy();
      expect(page.improvementCompletedButtons).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noSelection]);
      expect(page.errorSummaryListContents.length).toEqual(1);
    });

    it('should submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.improvementCompletedButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noDate]);

      page.improvementCompletedButtons[0].click();
      page.dateCompleted = expectedDate;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            reference: currentItem,
            operatorImprovementFollowUpResponse: {
              improvementCompleted: true,
              dateCompleted: expectedDate,
            },
          },
          { B1: false },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });

  describe('for existing operator-followup details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateRespond);
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Respond to B1');
      expect(page.verificationItem).toBeTruthy();
      expect(page.improvementCompletedButtons).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to `upload-evidence-question` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.improvementCompletedButtons[1].click();
      page.reason = 'Test description B1, when no';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            reference: currentItem,
            operatorImprovementFollowUpResponse: {
              improvementCompleted: false,
              reason: 'Test description B1, when no',
            },
          },
          { B1: false },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });
});
