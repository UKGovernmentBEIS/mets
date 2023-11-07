import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { MonitoringApproachCertUsageComponent } from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/monitoring-approach-cert-usage/monitoring-approach-cert-usage.component';
import { AviationAerCorsiaMonitoringApproachFormProvider } from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/monitoring-approach-form.provider';
import { AerRequestTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/dist/types/setup/setup';

import {
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  AviationAerCorsiaMonitoringApproach,
  TasksService,
} from 'pmrv-api';

describe('MonitoringApproachCertUsageComponent', () => {
  let component: MonitoringApproachCertUsageComponent;
  let fixture: ComponentFixture<MonitoringApproachCertUsageComponent>;
  let store: RequestTaskStore;
  let user: UserEvent;
  let router: Router;
  let formProvider: AviationAerCorsiaMonitoringApproachFormProvider;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);
  const initialState = { certUsed: true, certDetails: { flightType: null, publicationYear: null } };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonitoringApproachCertUsageComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AviationAerCorsiaMonitoringApproachFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
    }).compileComponents();

    user = userEvent.setup();

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
            aer: { monitoringApproach: initialState },
            aerSectionsCompleted: {},
          } as AviationAerCorsiaApplicationSubmitRequestTaskPayload,
        },
      },
    });

    formProvider = TestBed.inject<AviationAerCorsiaMonitoringApproachFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue(initialState);

    fixture = TestBed.createComponent(MonitoringApproachCertUsageComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show form errors', async () => {
    await user.click(screen.getByRole('button', { name: /Continue/ }));
    fixture.detectChanges();

    expect(screen.getByRole('alert')).toBeVisible();
    expect(screen.getAllByText(/Select which type of flights you have used the CERT for/)).toHaveLength(2);
    expect(screen.getAllByText(/Enter the year of the CERT version you used/)).toHaveLength(2);
  });

  it('should call the saveAer function with the correct data when the form is valid and navigate to next page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const monitoringApproach = {
      certUsed: true,
      certDetails: {
        flightType: 'ALL_INTERNATIONAL_FLIGHTS',
        publicationYear: 2023,
      },
    } as AviationAerCorsiaMonitoringApproach;

    const data = {
      monitoringApproach: {
        ...(store.getState().requestTaskItem.requestTask.payload as AerRequestTaskPayload).aer.monitoringApproach,
        ...monitoringApproach,
      },
    };

    component.form.setValue(monitoringApproach.certDetails);
    component.form.markAsDirty();
    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith(data, 'in progress');
    expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRouteStub });
  });
});
