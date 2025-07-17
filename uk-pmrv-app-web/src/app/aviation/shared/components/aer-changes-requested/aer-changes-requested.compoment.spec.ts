import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, mockClass } from '@testing';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/dist/types/setup/setup';

import { TasksService } from 'pmrv-api';

import { AerChangesRequestedComponent } from './aer-changes-requested.component';

describe('AerChangesRequestedComponent', () => {
  let component: AerChangesRequestedComponent;
  let fixture: ComponentFixture<AerChangesRequestedComponent>;
  let store: RequestTaskStore;
  let user: UserEvent;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerChangesRequestedComponent, RouterTestingModule],
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
        requestInfo: {
          type: 'AVIATION_AER_UKETS',
        },
        requestTask: {
          type: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT',
          payload: {
            emissionsMonitoringPlan: AerUkEtsStoreDelegate.INITIAL_STATE,
            reviewGroupDecisions: {
              MONITORING_PLAN_CHANGES: {
                details: { requiredChanges: [{ reason: 'First change' }, { reason: 'Second change' }] },
                type: 'OPERATOR_AMENDS_NEEDED',
              },
              MONITORING_APPROACH: {
                details: { requiredChanges: [{ reason: 'Third change' }] },
                type: 'OPERATOR_AMENDS_NEEDED',
              },
            },
          } as any,
        },
      },
    });

    fixture = TestBed.createComponent(AerChangesRequestedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show page content', () => {
    expect(screen.getByText(/Monitoring plan changes/)).toBeInTheDocument();
    expect(screen.getByText(/Monitoring approach/)).toBeInTheDocument();
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
      screen.getAllByText(/You must confirm that you have made the changes the regulator has requested/),
    ).toHaveLength(2);
  });
});
