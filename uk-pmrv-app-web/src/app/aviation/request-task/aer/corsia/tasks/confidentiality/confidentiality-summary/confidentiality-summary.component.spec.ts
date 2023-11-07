import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';

import {
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  AviationAerCorsiaConfidentiality,
  TasksService,
} from 'pmrv-api';

import { ConfidentialityFormProvider } from '../confidentiality-form.provider';
import { ConfidentialitySummaryComponent } from './confidentiality-summary.component';

describe('ConfidentialitySummaryComponent', () => {
  let component: ConfidentialitySummaryComponent;
  let fixture: ComponentFixture<ConfidentialitySummaryComponent>;
  let store: RequestTaskStore;
  let formProvider: ConfidentialityFormProvider;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);

  const data = {
    totalEmissionsPublished: false,
    aggregatedStatePairDataPublished: false,
  } as any;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfidentialitySummaryComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ConfidentialityFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    const state = store.getState();

    store.setState({
      ...state,
      requestTaskItem: {
        ...state.requestTaskItem,
        requestInfo: { type: 'AVIATION_AER_CORSIA' },
        requestTask: {
          type: 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD',
            aer: { confidentiality: data },
            aerSectionsCompleted: {},
          } as AviationAerCorsiaApplicationSubmitRequestTaskPayload,
        },
      },
    });

    formProvider = TestBed.inject<ConfidentialityFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue(data as AviationAerCorsiaConfidentiality);

    fixture = TestBed.createComponent(ConfidentialitySummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should call the saveAer function with the correct data when the form is valid', async () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));

    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith({ confidentiality: data }, 'complete');
  });
});
