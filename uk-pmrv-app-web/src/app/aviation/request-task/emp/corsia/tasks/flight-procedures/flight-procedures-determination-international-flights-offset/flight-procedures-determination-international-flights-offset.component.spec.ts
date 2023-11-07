import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { EMP_CORSIA } from '@aviation/request-task/emp/shared/mocks/mock-emp-corsia';
import { EmpRequestTaskPayloadCorsia, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import produce from 'immer';

import { FlightProceduresFormProvider } from '../flight-procedures-form.provider';
import FlightProceduresDeterminationInternationalFlightsNoOffsetComponent from './flight-procedures-determination-international-flights-offset.component';

describe('FlightProceduresDeterminationInternationalFlightsNoOffsetComponent', () => {
  let fixture: ComponentFixture<FlightProceduresDeterminationInternationalFlightsNoOffsetComponent>;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlightProceduresDeterminationInternationalFlightsNoOffsetComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: FlightProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT',
            payload: { emissionsMonitoringPlan: EMP_CORSIA } as EmpRequestTaskPayloadCorsia,
          },
        };
      }),
    );

    fixture = TestBed.createComponent(FlightProceduresDeterminationInternationalFlightsNoOffsetComponent);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should submit and navigate to determination-international-flights-no-monitoring', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveEmpSpy = jest.spyOn(store.empCorsiaDelegate, 'saveEmp').mockReturnValue(of({}));

    fixture.componentInstance.formProvider.setFormValue(EMP_CORSIA.flightAndAircraftProcedures);

    fixture.componentInstance.onSubmit();

    expect(saveEmpSpy).toHaveBeenCalledTimes(1);

    expect(navigateSpy).toHaveBeenCalledWith(['..', 'determination-international-flights-no-monitoring'], {
      relativeTo: activatedRoute,
    });
  });
});
