import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DataSamplingComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/process-analysis/data-sampling/data-sampling.component';
import { ProcessAnalysisFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/process-analysis/process-analysis-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import {
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerCorsiaProcessAnalysis,
  TasksService,
} from 'pmrv-api';

describe('DataSamplingComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: DataSamplingComponent;
  let fixture: ComponentFixture<DataSamplingComponent>;
  let formProvider: ProcessAnalysisFormProvider;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<DataSamplingComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    set dataSampling(value: string) {
      this.setInputValue('#dataSampling', value);
    }
    set dataSamplingResults(value: string) {
      this.setInputValue('#dataSamplingResults', value);
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
    fixture = TestBed.createComponent(DataSamplingComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataSamplingComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ProcessAnalysisFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new data sampling', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<ProcessAnalysisFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestInfo: { type: 'AVIATION_AER_CORSIA' },
          requestTask: {
            id: 19,
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
              verificationReport: {
                processAnalysis: {
                  verificationActivities: 'My verification activities',
                  strategicAnalysis: 'My strategic analysis',
                },
              },
              verificationSectionsCompleted: {},
            },
          },
        },
      } as any);

      formProvider.setFormValue(
        (store.aerVerifyDelegate.payload as AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload)
          .verificationReport.processAnalysis as AviationAerCorsiaProcessAnalysis,
      );
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Data sampling');
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents.length).toEqual(2);
      expect(page.errorSummaryListContents).toEqual([
        'Describe the data sampling and testing procedures',
        'Give the results of all the data sampling and testing exercises',
      ]);
    });

    it('should submit a valid form and navigate to `compliance` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.dataSampling = 'My data sampling';
      page.dataSamplingResults = 'My data sampling results';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
        requestTaskActionPayload: {
          processAnalysis: {
            verificationActivities: 'My verification activities',
            strategicAnalysis: 'My strategic analysis',
            dataSampling: 'My data sampling',
            dataSamplingResults: 'My data sampling results',
            emissionsMonitoringPlanCompliance: null,
          },
          verificationSectionsCompleted: { processAnalysis: [false] },
          payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['../compliance'], { relativeTo: activatedRoute });
    });
  });

  describe('for existing data sampling', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      formProvider = TestBed.inject<ProcessAnalysisFormProvider>(TASK_FORM_PROVIDER);

      store.setState({
        requestTaskItem: {
          requestInfo: { type: 'AVIATION_AER_CORSIA' },
          requestTask: {
            id: 19,
            type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
              verificationReport: {
                processAnalysis: {
                  verificationActivities: 'My verification activities',
                  strategicAnalysis: 'My strategic analysis',
                  dataSampling: 'My data sampling',
                  dataSamplingResults: 'My data sampling results',
                  emissionsMonitoringPlanCompliance: 'My emissions monitoring plan compliance',
                },
              },
              verificationSectionsCompleted: {},
            },
          },
        },
      } as any);
      formProvider.setFormValue(
        (store.aerVerifyDelegate.payload as AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload)
          .verificationReport.processAnalysis as AviationAerCorsiaProcessAnalysis,
      );

      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Data sampling');
      expect(page.submitButton).toBeTruthy();
    });

    it('should edit, submit a valid form and navigate to `compliance` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.dataSampling = 'My data sampling changed';
      page.dataSamplingResults = 'My data sampling results changed';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskId: 19,
        requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
        requestTaskActionPayload: {
          processAnalysis: {
            verificationActivities: 'My verification activities',
            strategicAnalysis: 'My strategic analysis',
            dataSampling: 'My data sampling changed',
            dataSamplingResults: 'My data sampling results changed',
            emissionsMonitoringPlanCompliance: 'My emissions monitoring plan compliance',
          },
          verificationSectionsCompleted: { processAnalysis: [false] },
          payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
        },
      });
      expect(navigateSpy).toHaveBeenCalledWith(['../compliance'], { relativeTo: activatedRoute });
    });
  });
});
