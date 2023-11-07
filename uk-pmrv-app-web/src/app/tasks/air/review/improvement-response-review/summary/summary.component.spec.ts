import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AirTaskReviewModule } from '@tasks/air/review/air-task-review.module';
import {
  mockAirApplicationReviewPayload,
  mockStateReview,
} from '@tasks/air/review/testing/mock-air-application-review-payload';
import { mockPostBuild, mockStateBuild } from '@tasks/air/review/testing/mock-state';
import { noPastDate } from '@tasks/air/shared/errors/validation-errors';
import { mockState } from '@tasks/air/submit/testing/mock-air-application-submit-payload';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AirImprovement, TasksService } from 'pmrv-api';

import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  const reference = '1';
  const currentItem: AirImprovement = mockAirApplicationReviewPayload.airImprovements[reference];
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub(
    { taskId: mockState.requestTaskItem.requestTask.id, id: reference },
    null,
    {
      airImprovement: currentItem,
    },
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get airImprovementItem() {
      return this.query('app-air-improvement-item');
    }

    get airOperatorResponseItem() {
      return this.query('app-air-operator-response-item');
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
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AirTaskReviewModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for valid improvement response review summary', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateReview);
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements', () => {
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Check your answers');
      expect(page.airImprovementItem).toBeTruthy();
      expect(page.airOperatorResponseItem).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should submit and navigate to task list', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            regulatorReviewResponse: mockAirApplicationReviewPayload.regulatorReviewResponse,
          },
          { [reference]: true },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
    });
  });

  describe('for invalid improvement response review summary', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          regulatorReviewResponse: {
            regulatorImprovementResponses: {
              '1': {
                improvementRequired: true,
                improvementDeadline: '2020-01-01',
                officialResponse: 'Test official response 1',
                comments: 'Test comment 1',
                files: ['44444444-4444-4444-a444-444444444444', '55555555-5555-4555-a555-555555555555'],
              },
            },
            reportSummary: 'Test summary',
          },
        }),
      );
      createComponent();
    });

    it('should display error summary when date is invalid', () => {
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noPastDate]);
    });
  });
});
