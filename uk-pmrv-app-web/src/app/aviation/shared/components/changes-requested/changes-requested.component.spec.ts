import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, mockClass } from '@testing';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/dist/types/setup/setup';

import { TasksService } from 'pmrv-api';

import { ChangesRequestedComponent } from './changes-requested.component';

describe('ChangesRequestedComponent', () => {
  let component: ChangesRequestedComponent;
  let fixture: ComponentFixture<ChangesRequestedComponent>;
  let store: RequestTaskStore;
  let user: UserEvent;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangesRequestedComponent, RouterTestingModule],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    user = userEvent.setup();

    store = TestBed.inject(RequestTaskStore);

    const state = store.getState();

    store.setState({
      ...state,
      isEditable: true,
      requestTaskItem: {
        ...state.requestTaskItem,
        requestTask: {
          type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT',
          payload: {
            emissionsMonitoringPlan: EmpUkEtsStoreDelegate.INITIAL_STATE,
            reviewGroupDecisions: {
              EMISSION_SOURCES: {
                details: { requiredChanges: [{ reason: 'First change' }, { reason: 'Second change' }] },
                type: 'OPERATOR_AMENDS_NEEDED',
              },
              FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES: {
                details: { requiredChanges: [{ reason: 'Third change' }] },
                type: 'OPERATOR_AMENDS_NEEDED',
              },
            },
          } as any,
        },
      },
    });

    fixture = TestBed.createComponent(ChangesRequestedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show page content', () => {
    expect(screen.getByText(/Flights and aircraft monitoring procedures/)).toBeInTheDocument();
    expect(screen.getByText(/Emission sources/)).toBeInTheDocument();
    expect(screen.getAllByText(/Changes required/)).toHaveLength(2);

    expect(screen.getByText(/First change/)).toBeInTheDocument();
    expect(screen.getByText(/Second change/)).toBeInTheDocument();
    expect(screen.getByText(/Third change/)).toBeInTheDocument();
  });

  it('should show form errors', async () => {
    await user.click(screen.getByRole('button', { name: /Confirm and Complete/ }));
    fixture.detectChanges();

    expect(screen.getByRole('alert')).toBeVisible();
    expect(
      screen.getAllByText(/Check the box to confirm you have made changes and want to mark as complete/),
    ).toHaveLength(2);
  });
});
