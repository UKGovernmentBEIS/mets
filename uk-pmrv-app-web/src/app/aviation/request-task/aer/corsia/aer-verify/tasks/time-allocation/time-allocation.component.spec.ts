import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TimeAllocationComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/time-allocation/time-allocation.component';
import { TimeAllocationFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/time-allocation/time-allocation-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import {
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerCorsiaTimeAllocationScope,
  TasksService,
} from 'pmrv-api';

describe('TimeAllocationComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: TimeAllocationComponent;
  let fixture: ComponentFixture<TimeAllocationComponent>;
  let formProvider: TimeAllocationFormProvider;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<TimeAllocationComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }
    get heading2(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h2');
    }

    set verificationTotalTime(value: string) {
      this.setInputValue('#verificationTotalTime', value);
    }
    set verificationScope(value: string) {
      this.setInputValue('#verificationScope', value);
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
    fixture = TestBed.createComponent(TimeAllocationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TimeAllocationComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: TimeAllocationFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new time allocation', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<TimeAllocationFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestInfo: { type: 'AVIATION_AER_CORSIA' },
          requestTask: {
            id: 19,
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
              verificationReport: {},
              verificationSectionsCompleted: {},
            },
          },
        },
      } as any);

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Time allocation and scope of the verification');
      expect(page.heading2.map((h2) => h2.textContent.trim())).toEqual([
        'Total time of the verification',
        'Scope of the verification',
      ]);
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents.length).toEqual(2);
      expect(page.errorSummaryListContents).toEqual([
        'Enter the total time required to carry out the verification, including any revisions',
        'Enter the scope of the verification',
      ]);
    });

    it('should submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.verificationTotalTime = 'My total time';
      page.verificationScope = 'My verification scope';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
        requestTaskActionPayload: {
          timeAllocationScope: {
            verificationTotalTime: 'My total time',
            verificationScope: 'My verification scope',
          },
          verificationSectionsCompleted: { timeAllocationScope: [false] },
          payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing time allocation', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<TimeAllocationFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestInfo: { type: 'AVIATION_AER_CORSIA' },
          requestTask: {
            id: 19,
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
              verificationReport: {
                timeAllocationScope: {
                  verificationTotalTime: 'My total time',
                  verificationScope: 'My verification scope',
                },
              },
              verificationSectionsCompleted: {},
            },
          },
        },
      } as any);
      formProvider.setFormValue(
        (store.aerVerifyDelegate.payload as AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload)
          .verificationReport.timeAllocationScope as AviationAerCorsiaTimeAllocationScope,
      );

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Time allocation and scope of the verification');
      expect(page.heading2.map((h2) => h2.textContent.trim())).toEqual([
        'Total time of the verification',
        'Scope of the verification',
      ]);
      expect(page.submitButton).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.verificationTotalTime = 'My total time changed';
      page.verificationScope = 'My verification scope changed';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
        requestTaskActionPayload: {
          timeAllocationScope: {
            verificationTotalTime: 'My total time changed',
            verificationScope: 'My verification scope changed',
          },
          verificationSectionsCompleted: { timeAllocationScope: [false] },
          payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
    });
  });
});
