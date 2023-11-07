import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { EmpRequestTaskPayloadCorsia } from '@aviation/request-task/store/request-task.types';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/dist/types/setup/setup';

import { FlightIdentification, PartnershipOrganisation, TasksService } from 'pmrv-api';

import { OperatorDetailsCorsiaFormProvider } from '../operator-details-form.provider';
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
        { provide: TASK_FORM_PROVIDER, useClass: OperatorDetailsCorsiaFormProvider },
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
        requestTask: {
          type: 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT',
          payload: {
            emissionsMonitoringPlan: {
              ...EmpUkEtsStoreDelegate.INITIAL_STATE,
              operatorDetails: {
                organisationStructure: { partners: [] } as PartnershipOrganisation,
              },
            },
            reviewSectionsCompleted: {},
          } as any,
        },
      },
    });

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

    await user.click(
      screen.getByRole('radio', {
        name: /Nationality or common mark, registration mark of an aeroplane or any other code used in item 7 of the flight plan/,
      }),
    );
    fixture.detectChanges();

    expect(screen.getAllByText(/State which aircraft registration markings you are using/)).toHaveLength(2);
  });

  it('should call the saveEmp function with the correct data when the form is valid and navigate to summary page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const flightIdentification = {
      flightIdentificationType: 'INTERNATIONAL_CIVIL_AVIATION_ORGANISATION',
      icaoDesignators: 'ICAO designators',
      aircraftRegistrationMarkings: [],
    } as FlightIdentification;

    const state = store.getState();

    const data = {
      operatorDetails: {
        ...(state.requestTaskItem.requestTask.payload as EmpRequestTaskPayloadCorsia).emissionsMonitoringPlan
          .operatorDetails,
        flightIdentification,
      },
    };

    const saveEmpSpy = jest.spyOn(store.empCorsiaDelegate, 'saveEmp').mockReturnValue(of({}));

    component.form.setValue({
      flightIdentificationType: flightIdentification.flightIdentificationType,
      icaoDesignators: flightIdentification.icaoDesignators,
      aircraftRegistrationMarkings: flightIdentification.aircraftRegistrationMarkings,
    }) as unknown as FlightIdentification;
    component.onSubmit();

    expect(saveEmpSpy).toHaveBeenCalledTimes(1);
    expect(saveEmpSpy).toHaveBeenCalledWith(data, 'in progress');
    expect(navigateSpy).toHaveBeenCalledWith(['../air-operating-certificate'], {
      relativeTo: activatedRouteStub,
      queryParams: { change: null },
    });
  });
});
