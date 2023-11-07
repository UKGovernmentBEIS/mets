import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AviationAerCorsiaMonitoringApproachFormProvider } from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/monitoring-approach-form.provider';
import { MonitoringApproachFuelUsageComponent } from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/monitoring-approach-fuel-usage';
import { RequestTaskStore } from '@aviation/request-task/store';
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

describe('MonitoringApproachFuelUsageComponent', () => {
  let component: MonitoringApproachFuelUsageComponent;
  let fixture: ComponentFixture<MonitoringApproachFuelUsageComponent>;
  let store: RequestTaskStore;
  let user: UserEvent;
  let router: Router;
  let formProvider: AviationAerCorsiaMonitoringApproachFormProvider;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);
  const initialState = {
    certUsed: false,
    fuelUseMonitoringDetails: { fuelDensityType: null, identicalToProcedure: null, blockHourUsed: null },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonitoringApproachFuelUsageComponent, RouterTestingModule],
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

    fixture = TestBed.createComponent(MonitoringApproachFuelUsageComponent);
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
    expect(
      screen.getAllByText(/Select which fuel density type was used to determine fuel uplift in the reporting year/),
    ).toHaveLength(2);

    await user.click(screen.getByRole('radio', { name: /Actual density/ }));
    fixture.detectChanges();

    expect(
      screen.getAllByText(
        /Select if the application of density data is identical to the EMP procedure used for operational and safety reasons/,
      ),
    ).toHaveLength(1);
    expect(
      screen.getAllByText(/Select if fuel allocation by block hour was used during the reporting year/),
    ).toHaveLength(1);
  });

  it('should call the saveAer function with the correct data when the form is valid and navigate to next page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const monitoringApproach = {
      certUsed: false,
      fuelUseMonitoringDetails: {
        fuelDensityType: 'ACTUAL_DENSITY',
        identicalToProcedure: true,
        blockHourUsed: true,
      },
    } as AviationAerCorsiaMonitoringApproach;

    const data = {
      monitoringApproach: {
        ...monitoringApproach,
        fuelUseMonitoringDetails: {
          ...monitoringApproach.fuelUseMonitoringDetails,
          aircraftTypeDetails: null,
        },
      },
    };

    component.form.setValue(monitoringApproach.fuelUseMonitoringDetails);
    component.form.markAsDirty();
    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith(data, 'in progress');
    expect(navigateSpy).toHaveBeenCalledWith(['../fuel-allocation-block-hour'], { relativeTo: activatedRouteStub });
  });
});
