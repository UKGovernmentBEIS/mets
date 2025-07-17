import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerRequestTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { AerUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { ActivatedRouteStub, mockClass } from '@testing';
import produce from 'immer';

import { AircraftTypesService, AviationAerAircraftData, TasksService } from 'pmrv-api';

import { AircraftTypesDataFormProvider } from '../aircraft-types-data-form.provider';
import { AircraftTypesDataSummaryComponent } from './aircraft-types-data-summary.component';

describe('AircraftTypesDataSummaryComponent', () => {
  let fixture: ComponentFixture<AircraftTypesDataSummaryComponent>;
  let component: AircraftTypesDataSummaryComponent;
  let formProvider: AircraftTypesDataFormProvider;
  let store: RequestTaskStore;
  let router: Router;

  const activatedRouteStub = new ActivatedRouteStub();
  const data = {
    aviationAerAircraftDataDetails: [
      {
        aircraftTypeDesignator: 'C560',
        subType: '',
        registrationNumber: 'D-CAPB',
        ownerOrLessor: 'Aviation Operator',
        startDate: '2021-01-01',
        endDate: '2021-12-31',
      },
      {
        aircraftTypeDesignator: 'C560',
        subType: '',
        registrationNumber: 'D-CAPB',
        ownerOrLessor: 'Aviation Operator',
        startDate: '2021-01-01',
        endDate: '2021-12-31',
      },
    ],
  };
  const tasksService = mockClass(TasksService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule, AircraftTypesDataSummaryComponent],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AircraftTypesDataFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
        { provide: AircraftTypesService, useValue: mockClass(AircraftTypesService) },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT',
            payload: {
              ...AerUkEtsStoreDelegate.INITIAL_STATE,
              aviationAerAircraftData: { ...data },
              aerSectionsCompleted: {
                aviationAerAircraftData: [false],
              },
            } as AerRequestTaskPayload,
          },
        };
      }),
    );

    fixture = TestBed.createComponent(AircraftTypesDataSummaryComponent);
    component = fixture.componentInstance;
    formProvider = TestBed.inject<AircraftTypesDataFormProvider>(TASK_FORM_PROVIDER);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call saveAer on submit and redirect to task list', async () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));

    formProvider.getAircraftDataDetailsControl().clearAsyncValidators();
    formProvider.setFormValue(data as AviationAerAircraftData);
    formProvider.getAircraftDataDetailsControl().setValue(data.aviationAerAircraftDataDetails);
    formProvider.getAircraftDataDetailsControl().updateValueAndValidity();
    fixture.detectChanges();
    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith({ aviationAerAircraftData: data }, 'complete');
    expect(navigateSpy).toHaveBeenCalledWith(['../../..'], { relativeTo: activatedRouteStub });
  });
});
