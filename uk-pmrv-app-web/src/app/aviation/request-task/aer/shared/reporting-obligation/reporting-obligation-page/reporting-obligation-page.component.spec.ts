import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { ReportingObligationFormProvider } from '../reporting-obligation-form.provider';
import { reportingObligationQuery } from '../store/reporting-obligation.selector';
import ReportObligationPageComponent from './reporting-obligation-page.component';

describe('ReportObligationPageComponent', () => {
  let component: ReportObligationPageComponent;
  let fixture: ComponentFixture<ReportObligationPageComponent>;
  let store: RequestTaskStore;
  let formProvider: ReportingObligationFormProvider;
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);

  const activatedRouteStub = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportObligationPageComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ReportingObligationFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<ReportingObligationFormProvider>(TASK_FORM_PROVIDER);
    setupStore(store, formProvider);

    fixture = TestBed.createComponent(ReportObligationPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('ReportObligationPageComponent', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should call the saveAer function with the correct data when the form is valid', async () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      const data = {
        reportingObligation: {
          reportingRequired: false,
          reportingObligationDetails: {
            noReportingReason: 'asdsadsadasd',
            supportingDocuments: [],
          },
        },
      };

      const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));

      component.reportingObligationForm.setValue({
        reportingRequired: false,
        reportingObligationDetails: {
          noReportingReason: 'asdsadsadasd',
          supportingDocuments: [],
        },
      });

      component.onSubmit();

      expect(saveAerSpy).toHaveBeenCalledWith(data, 'in progress');

      const storeData = await firstValueFrom(store.pipe(reportingObligationQuery.selectReportingObligation));

      expect(storeData).toEqual({
        reportingRequired: false,
        reportingObligationDetails: {
          noReportingReason: 'asdsadsadasd',
          supportingDocuments: [],
        },
      });

      saveAerSpy.mockRestore();
    });
  });
});

function setupStore(store: RequestTaskStore, formProvider: ReportingObligationFormProvider) {
  store.setState({
    requestTaskItem: {
      requestTask: {
        id: 10,
        request_id: 'AEM00002-2022',
        process_task_id: '5d0c6057-f952-11ed-a988-cc153192bf12',
        type: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT',
        assignee: 'a42a7b13-4da8-496b-a0da-9dda28d4bfb6',
        start_date: '2023-05-23T07:12:46.314Z',
        due_date: '2023-03-31',
        pause_date: null,
        end_date: null,
        payload: {
          payloadType: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD',
          reportingYear: '2022',
          aerAttachments: { 'c99b125d-e1c5-454c-817d-94ff221087b1': 'CATS-Company awareness session.pdf' },
          reportingRequired: false,
          aerSectionsCompleted: { reportingObligation: [true], serviceContactDetails: [true] },
          serviceContactDetails: {
            name: 'oper1 eng',
            email: 'operator1.england@pmrv.uk',
            roleCode: 'operator_admin',
          },
          aerMonitoringPlanVersions: [],
          reportingObligationDetails: {
            noReportingReason: 'asdsadsadasd',
            supportingDocuments: [],
          },
        },
        version: 512,
      },
    },
  } as any);

  formProvider.setFormValue({
    reportingRequired: store.aerDelegate.payload.reportingRequired,
    reportingObligationDetails: store.aerDelegate.payload.reportingObligationDetails,
  });
}
