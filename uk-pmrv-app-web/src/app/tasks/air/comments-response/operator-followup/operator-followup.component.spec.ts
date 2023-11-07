import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AirTaskCommentsResponseModule } from '@tasks/air/comments-response/air-task-comments-response.module';
import {
  mockAirApplicationRespondPayload,
  mockStateRespond,
} from '@tasks/air/comments-response/testing/mock-air-application-respond-payload';
import { mockPostBuild, mockStateBuild } from '@tasks/air/comments-response/testing/mock-state';
import { noDate, noSelection } from '@tasks/air/shared/errors/validation-errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AirImprovement, RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { OperatorFollowupComponent } from './operator-followup.component';

describe('OperatorFollowupComponent', () => {
  let page: Page;
  let router: Router;
  let control: FormControl;
  let store: CommonTasksStore;
  let component: OperatorFollowupComponent;
  let fixture: ComponentFixture<OperatorFollowupComponent>;

  const reference = '1';
  const currentItem: AirImprovement = mockAirApplicationRespondPayload.airImprovements[reference];
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub(
    { taskId: mockStateRespond.requestTaskItem.requestTask.id, id: reference },
    null,
    {
      airImprovement: currentItem,
    },
  );
  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const expectedDate = new Date('2020-12-01');
  const expectedNextRoute = '../summary';
  const uuid3 = '33333333-3333-4333-a333-333333333333';
  const uuid4 = '44444444-4444-4444-a444-444444444444';

  class Page extends BasePage<OperatorFollowupComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get airImprovementItem() {
      return this.query('app-air-improvement-item');
    }

    get airOperatorResponseItem() {
      return this.query('app-air-operator-response-item');
    }

    get airRegulatorResponseItem() {
      return this.query('app-air-regulator-response-item');
    }

    get improvementCompletedButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="improvementCompleted"]');
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
    control = component.form.get('files') as FormControl;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AirTaskCommentsResponseModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
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
      expect(page.heading1.textContent.trim()).toEqual(`Respond to regulator`);
      expect(page.airImprovementItem).toBeTruthy();
      expect(page.airOperatorResponseItem).toBeTruthy();
      expect(page.airRegulatorResponseItem).toBeTruthy();
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
      attachmentService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: uuid4 } })),
      );

      page.improvementCompletedButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noDate]);

      control.setValue([{ file: new File(['test content 3'], 'testfile3.jpg'), uuid: uuid3 }]);
      page.filesValue = [new File(['test content 4'], 'testfile4.jpg')];
      fixture.detectChanges();

      expect(page.fileDeleteButtons).toHaveLength(2);
      expect(page.filesText.map((row) => row.textContent.trim())).toEqual([
        'testfile3.jpg',
        'testfile4.jpg has been uploaded',
      ]);

      page.improvementCompletedButtons[0].click();
      page.dateCompleted = expectedDate;
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            reference: Number(reference),
            operatorImprovementFollowUpResponse: {
              improvementCompleted: true,
              dateCompleted: expectedDate,
              reason: null,
              files: [uuid3, uuid4],
            },
          },
          { [reference]: false },
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
      expect(page.heading1.textContent.trim()).toEqual('Respond to regulator');
      expect(page.airImprovementItem).toBeTruthy();
      expect(page.airOperatorResponseItem).toBeTruthy();
      expect(page.airRegulatorResponseItem).toBeTruthy();
      expect(page.improvementCompletedButtons).toBeTruthy();
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

    it('should edit, submit a valid form and navigate to `upload-evidence-question` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.improvementCompletedButtons[1].click();
      page.reason = 'Test reason 1, when no';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            reference: Number(reference),
            operatorImprovementFollowUpResponse: {
              improvementCompleted: false,
              dateCompleted: null,
              reason: 'Test reason 1, when no',
              files: mockAirApplicationRespondPayload.operatorImprovementFollowUpResponses[reference].files,
            },
          },
          { [reference]: false },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });
});
