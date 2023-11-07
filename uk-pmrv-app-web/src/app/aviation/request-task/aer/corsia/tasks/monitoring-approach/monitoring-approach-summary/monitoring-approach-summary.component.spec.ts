import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import {
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  AviationAerCorsiaMonitoringApproach,
  TasksService,
} from 'pmrv-api';

import { AviationAerCorsiaMonitoringApproachFormProvider } from '../monitoring-approach-form.provider';
import { MonitoringApproachSummaryComponent } from './monitoring-approach-summary.component';

describe('MonitoringApproachSummaryComponent', () => {
  let component: MonitoringApproachSummaryComponent;
  let fixture: ComponentFixture<MonitoringApproachSummaryComponent>;
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let formProvider: AviationAerCorsiaMonitoringApproachFormProvider;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  const initialState = {
    certUsed: false,
    fuelUseMonitoringDetails: {
      fuelDensityType: 'ACTUAL_DENSITY',
      identicalToProcedure: true,
      blockHourUsed: true,
      aircraftTypeDetails: [
        {
          designator: 'C560',
          subtype: 'test',
          fuelBurnRatio: '1.2',
        },
        {
          designator: 'A320',
          subtype: 'test',
          fuelBurnRatio: '1',
        },
      ],
    },
  } as AviationAerCorsiaMonitoringApproach;

  class Page extends BasePage<MonitoringApproachSummaryComponent> {
    get appSummaryTemplate() {
      return this.query<HTMLDivElement>('app-monitoring-approach-corsia-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, FormsModule, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AviationAerCorsiaMonitoringApproachFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
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
            aer: { monitoringApproach: initialState },
            aerSectionsCompleted: {},
          } as AviationAerCorsiaApplicationSubmitRequestTaskPayload,
        },
      },
    });

    formProvider = TestBed.inject<AviationAerCorsiaMonitoringApproachFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue(initialState);

    fixture = TestBed.createComponent(MonitoringApproachSummaryComponent);
    component = fixture.componentInstance;
    router = fixture.debugElement.injector.get(Router);
    fixture.detectChanges();
    page = new Page(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display HTML element', () => {
    expect(page.appSummaryTemplate).toBeTruthy();
  });

  it('should call saveAer on submit and redirect to task list', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith({ monitoringApproach: initialState }, 'complete');
    expect(navigateSpy).toHaveBeenCalledWith(['../../../'], { relativeTo: activatedRouteStub });
  });
});
