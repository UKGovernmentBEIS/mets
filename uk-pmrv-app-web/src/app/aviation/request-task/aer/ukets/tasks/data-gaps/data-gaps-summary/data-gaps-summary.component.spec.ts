import { ComponentFixture, fakeAsync, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, mockClass } from '@testing';

import { AviationAerDataGaps, TasksService } from 'pmrv-api';

import { DataGapsFormProvider } from '../data-gaps-form.provider';
import DataGapsSummaryComponent from './data-gaps-summary.component';

describe('DataGapsSummaryComponent', () => {
  let component: DataGapsSummaryComponent;
  let fixture: ComponentFixture<DataGapsSummaryComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  const dataGaps: AviationAerDataGaps = {
    exist: true,
    affectedFlightsPercentage: '20',
    dataGaps: [
      {
        reason: 'reason 1',
        type: 'type 1',
        replacementMethod: 'replacement method 1',
        flightsAffected: 5,
        totalEmissions: '7',
      },
      {
        reason: 'reason 2',
        type: 'type 2',
        replacementMethod: 'replacement method 2',
        flightsAffected: 6,
        totalEmissions: '8',
      },
    ],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataGapsSummaryComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: DataGapsFormProvider },
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
        requestTask: {
          type: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT',
          payload: {
            aer: {
              ...AerUkEtsStoreDelegate.INITIAL_STATE,
              dataGaps,
            },
          } as any,
        },
      },
    });

    fixture = TestBed.createComponent(DataGapsSummaryComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call "saveAer" on submit and redirect to task list', fakeAsync(() => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));

    component.formProvider.setFormValue(dataGaps);

    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith({ dataGaps: component.formProvider.getFormValue() }, 'complete');

    expect(navigateSpy).toHaveBeenCalledWith(['../../..'], { relativeTo: activatedRouteStub, replaceUrl: true });
  }));
});
