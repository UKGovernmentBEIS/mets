import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerRequestTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { AerUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/dist/types/setup/setup';
import produce from 'immer';

import { FlightIdentification, TasksService } from 'pmrv-api';

import { OperatorDetailsFormProvider } from '../operator-details-form.provider';
import { OperatorDetailsFlightIdentificationComponent } from './operator-details-flight-identification.component';

describe('OperatorDetailsFlightIdentificationComponent', () => {
  let component: OperatorDetailsFlightIdentificationComponent;
  let fixture: ComponentFixture<OperatorDetailsFlightIdentificationComponent>;
  let store: RequestTaskStore;
  let user: UserEvent;
  let router: Router;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorDetailsFlightIdentificationComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: OperatorDetailsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
    }).compileComponents();

    user = userEvent.setup();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT',
            payload: AerUkEtsStoreDelegate.INITIAL_STATE as AerRequestTaskPayload,
          },
        };
      }),
    );

    fixture = TestBed.createComponent(OperatorDetailsFlightIdentificationComponent);
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
    expect(screen.getAllByText(/Select the call sign identifier you use/)).toHaveLength(2);

    await user.click(screen.getByRole('radio', { name: /International Civil Aviation Organisation/ }));
    fixture.detectChanges();

    expect(screen.getAllByText(/State which ICAO designators you are using/)).toHaveLength(2);

    await user.click(screen.getByRole('radio', { name: /Aircraft registration markings/ }));
    fixture.detectChanges();

    expect(screen.getAllByText(/State which aircraft registration markings you are using/)).toHaveLength(2);
  });

  it('should call the saveAer function with the correct data when the form is valid and navigate to summary page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const flightIdentification = {
      flightIdentificationType: 'INTERNATIONAL_CIVIL_AVIATION_ORGANISATION',
      icaoDesignators: 'ICAO designators',
      aircraftRegistrationMarkings: [],
    } as FlightIdentification;

    const data = {
      operatorDetails: {
        flightIdentification,
      },
    };

    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));

    component.form.setValue({
      flightIdentificationType: flightIdentification.flightIdentificationType,
      icaoDesignators: flightIdentification.icaoDesignators,
      aircraftRegistrationMarkings: flightIdentification.aircraftRegistrationMarkings,
    }) as unknown as FlightIdentification;
    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith(data, 'in progress');
    expect(navigateSpy).toHaveBeenCalledWith(['../', 'air-operating-certificate'], { relativeTo: activatedRouteStub });
  });
});
