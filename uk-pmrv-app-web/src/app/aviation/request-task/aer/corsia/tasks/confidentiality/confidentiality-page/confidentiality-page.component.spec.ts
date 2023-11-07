import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { ConfidentialityFormProvider } from '../confidentiality-form.provider';
import { ConfidentialityPageComponent } from './confidentiality-page.component';

describe('ConfidentialityPageComponent', () => {
  let component: ConfidentialityPageComponent;
  let fixture: ComponentFixture<ConfidentialityPageComponent>;
  let store: RequestTaskStore;
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);

  const activatedRouteStub = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfidentialityPageComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ConfidentialityFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    setupStore(store);

    fixture = TestBed.createComponent(ConfidentialityPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('ConfidentialityPageComponent', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should call the saveAer function with the correct data when the form is valid', async () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      const data = {
        confidentiality: {
          totalEmissionsPublished: false,
          aggregatedStatePairDataPublished: false,
        },
      };

      const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));

      component.confidentialityForm.setValue({
        totalEmissionsPublished: false,
        aggregatedStatePairDataPublished: false,
      });

      component.onSubmit();

      expect(saveAerSpy).toHaveBeenCalledWith(data, 'in progress');

      saveAerSpy.mockRestore();
    });
  });
});

function setupStore(store: RequestTaskStore) {
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
          totalEmissionsPublished: false,
          aggregatedStatePairDataPublished: false,
          aerSectionsCompleted: { confidentiality: [true], serviceContactDetails: [true] },
          serviceContactDetails: {
            name: 'oper1 eng',
            email: 'operator1.england@pmrv.uk',
            roleCode: 'operator_admin',
          },
          aerMonitoringPlanVersions: [],
        },
        version: 512,
      },
    },
  } as any);
}
