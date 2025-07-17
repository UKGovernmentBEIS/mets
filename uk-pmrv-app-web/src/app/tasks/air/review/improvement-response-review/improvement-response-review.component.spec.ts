import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AirTaskReviewModule } from '@tasks/air/review/air-task-review.module';
import {
  mockAirApplicationReviewPayload,
  mockStateReview,
} from '@tasks/air/review/testing/mock-air-application-review-payload';
import { mockPostBuild, mockStateBuild } from '@tasks/air/review/testing/mock-state';
import { noComment, noDate, noSelection } from '@tasks/air/shared/errors/validation-errors';
import { mockState } from '@tasks/air/submit/testing/mock-air-application-submit-payload';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AirImprovement, RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { ImprovementResponseReviewComponent } from './improvement-response-review.component';

describe('ImprovementResponseReviewComponent', () => {
  let page: Page;
  let router: Router;
  let control: FormControl;
  let store: CommonTasksStore;
  let component: ImprovementResponseReviewComponent;
  let fixture: ComponentFixture<ImprovementResponseReviewComponent>;

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
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const expectedNextRoute = '../summary';
  const uuid3 = '33333333-3333-4333-a333-333333333333';
  const uuid4 = '44444444-4444-4444-a444-444444444444';

  class Page extends BasePage<ImprovementResponseReviewComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get airImprovementItem() {
      return this.query('app-air-improvement-item');
    }

    get airOperatorResponseItem() {
      return this.query('app-air-operator-response-item');
    }

    get improvementRequiredButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="improvementRequired"]');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get officialResponse() {
      return this.getInputValue('#officialResponse');
    }

    set officialResponse(value: string) {
      this.setInputValue('#officialResponse', value);
    }

    get comments() {
      return this.getInputValue('#comments');
    }

    set comments(value: string) {
      this.setInputValue('#comments', value);
    }

    set improvementDeadline(date: Date) {
      this.setInputValue(`#improvementDeadline-day`, date.getDate());
      this.setInputValue(`#improvementDeadline-month`, date.getMonth() + 1);
      this.setInputValue(`#improvementDeadline-year`, date.getFullYear());
    }

    set filesValue(value: File[]) {
      this.setInputValue('input[type="file"]', value);
    }

    get multipleFileInput(): HTMLElement {
      return this.query('app-multiple-file-input');
    }

    get filesText() {
      return this.queryAll<HTMLDivElement>('.moj-multi-file-upload__message');
    }

    get fileDeleteButtons() {
      return this.queryAll<HTMLButtonElement>('.moj-multi-file-upload__delete');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ImprovementResponseReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    control = component.form.get('files') as FormControl;
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
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();
  });

  describe('for new improvement response review details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          regulatorReviewResponse: {
            regulatorImprovementResponses: {},
            reportSummary: null,
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
      expect(page.heading1.textContent.trim()).toEqual('Review information about this improvement');
      expect(page.airImprovementItem).toBeTruthy();
      expect(page.airOperatorResponseItem).toBeTruthy();
      expect(page.improvementRequiredButtons).toHaveLength(2);
      expect(page.officialResponse).toEqual('');
      expect(page.comments).toEqual('');
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noSelection, noComment, noComment]);
      expect(page.errorSummaryListContents.length).toEqual(3);
    });

    it('should submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      attachmentService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: uuid4 } })),
      );

      page.improvementRequiredButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noDate, noComment, noComment]);

      control.setValue([{ file: new File(['test content 3'], 'testfile3.jpg'), uuid: uuid3 }]);
      page.filesValue = [new File(['test content 4'], 'testfile4.jpg')];
      fixture.detectChanges();

      expect(page.fileDeleteButtons).toHaveLength(2);
      expect(page.filesText.map((row) => row.textContent.trim())).toEqual([
        'testfile3.jpg',
        'testfile4.jpg has been uploaded',
      ]);

      page.improvementRequiredButtons[0].click();

      const nextYearDate = new Date(new Date().setFullYear(new Date().getFullYear() + 1));
      page.improvementDeadline = nextYearDate;
      page.officialResponse = 'Test official response 1';
      page.comments = 'Test comments 1';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            regulatorReviewResponse: {
              regulatorImprovementResponses: {
                [reference]: {
                  improvementRequired: true,
                  improvementDeadline: new Date(new Date(nextYearDate).setUTCHours(0, 0, 0, 0)),
                  officialResponse: 'Test official response 1',
                  comments: 'Test comments 1',
                  files: [uuid3, uuid4],
                },
              },
              reportSummary: null,
            },
          },
          { [reference]: false },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });

  describe('for existing improvement response review details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateReview);
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Review information about this improvement');
      expect(page.airImprovementItem).toBeTruthy();
      expect(page.airOperatorResponseItem).toBeTruthy();
      expect(page.improvementRequiredButtons).toHaveLength(2);
      expect(page.officialResponse).toEqual('Test official response 1');
      expect(page.comments).toEqual('Test comment 1');
      expect(page.multipleFileInput).toBeTruthy();
      expect(page.fileDeleteButtons).toHaveLength(2);
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

    it('should edit, submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.improvementRequiredButtons[1].click();
      page.officialResponse = 'Test official response 1, changed';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            regulatorReviewResponse: {
              ...mockAirApplicationReviewPayload.regulatorReviewResponse,
              regulatorImprovementResponses: {
                ...mockAirApplicationReviewPayload.regulatorReviewResponse.regulatorImprovementResponses,
                [reference]: {
                  improvementRequired: false,
                  improvementDeadline: null,
                  officialResponse: 'Test official response 1, changed',
                  comments: 'Test comment 1',
                  files: ['44444444-4444-4444-a444-444444444444', '55555555-5555-4555-a555-555555555555'],
                },
              },
            },
          },
          { [reference]: false },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });
});
