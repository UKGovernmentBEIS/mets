import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { noJustification, noTechnicalInfeasibilityExplanation } from '@tasks/air/shared/errors/validation-errors';
import { AirTaskSubmitModule } from '@tasks/air/submit/air-task-submit.module';
import {
  mockAirApplicationSubmitPayload,
  mockState,
} from '@tasks/air/submit/testing/mock-air-application-submit-payload';
import { mockPostBuild, mockStateBuild } from '@tasks/air/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AirImprovement, RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { ImprovementNegativeComponent } from './improvement-negative.component';

describe('ImprovementNegativeComponent', () => {
  let page: Page;
  let router: Router;
  let control: FormControl;
  let store: CommonTasksStore;
  let component: ImprovementNegativeComponent;
  let fixture: ComponentFixture<ImprovementNegativeComponent>;

  const reference = '2';
  const currentItem: AirImprovement = mockAirApplicationSubmitPayload.airImprovements[reference];
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

  class Page extends BasePage<ImprovementNegativeComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get unreasonableCost() {
      return this.query<HTMLInputElement>('#justification-0');
    }
    get technicalInfeasibility() {
      return this.query<HTMLInputElement>('#justification-1');
    }

    set technicalInfeasibilityExplanation(value: string) {
      this.setInputValue('#technicalInfeasibilityExplanation', value);
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

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ImprovementNegativeComponent);
    component = fixture.componentInstance;
    control = component.form.get('files') as FormControl;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AirTaskSubmitModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();
  });

  describe('for new improvement negative details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          operatorImprovementResponses: {},
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
      expect(page.heading1.textContent.trim()).toEqual('Provide information about this improvement');
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noJustification]);

      page.technicalInfeasibility.click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummaryListContents).toEqual([noTechnicalInfeasibilityExplanation]);
    });

    it('should submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      attachmentService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: uuid4 } })),
      );

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noJustification]);

      control.setValue([{ file: new File(['test content 3'], 'testfile3.jpg'), uuid: uuid3 }]);
      page.filesValue = [new File(['test content 4'], 'testfile4.jpg')];
      fixture.detectChanges();

      expect(page.fileDeleteButtons).toHaveLength(2);
      expect(page.filesText.map((row) => row.textContent.trim())).toEqual([
        'testfile3.jpg',
        'testfile4.jpg has been uploaded',
      ]);

      page.unreasonableCost.click();
      page.technicalInfeasibility.click();
      page.technicalInfeasibilityExplanation = 'Test description 2';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            operatorImprovementResponses: {
              [reference]: {
                isCostUnreasonable: true,
                isTechnicallyInfeasible: true,
                technicalInfeasibilityExplanation: 'Test description 2',
                files: [uuid3, uuid4],
              },
            },
          },
          { [reference]: false },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });

  describe('for existing improvement negative details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Provide information about this improvement');
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
      attachmentService.uploadRequestTaskAttachment.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: uuid4 } })),
      );

      expect(page.fileDeleteButtons).toHaveLength(2);
      expect(page.filesText.map((row) => row.textContent.trim())).toEqual(['100.png', '200.png']);
      expect(page.errorSummary).toBeFalsy();

      control.setValue([{ file: new File(['test content 3'], 'testfile3.jpg'), uuid: uuid3 }]);
      page.filesValue = [new File(['test content 4'], 'testfile4.jpg')];
      fixture.detectChanges();

      expect(page.fileDeleteButtons).toHaveLength(2);
      expect(page.filesText.map((row) => row.textContent.trim())).toEqual([
        'testfile3.jpg',
        'testfile4.jpg has been uploaded',
      ]);

      expect(page.errorSummary).toBeFalsy();

      page.unreasonableCost.click();
      page.technicalInfeasibilityExplanation = 'Test description 2 edited';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            operatorImprovementResponses: {
              ...mockAirApplicationSubmitPayload.operatorImprovementResponses,
              [reference]: {
                ...mockAirApplicationSubmitPayload.operatorImprovementResponses?.[reference],
                isCostUnreasonable: false,
                technicalInfeasibilityExplanation: 'Test description 2 edited',
                files: [uuid3, uuid4],
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
