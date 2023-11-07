import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, mockClass } from '@testing';
import { screen } from '@testing-library/angular';

import {
  AviationAerCorsia,
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  AviationAerCorsiaDataGaps,
  AviationAerCorsiaTotalEmissions,
  AviationAerUkEtsAggregatedEmissionsData,
  AviationReportingService,
  TasksService,
} from 'pmrv-api';

import { DataGapsFormProvider } from '../data-gaps-form.provider';
import DataGapsListComponent from './data-gaps-list.component';

describe('DataGapsListComponent', () => {
  let component: DataGapsListComponent;
  let fixture: ComponentFixture<DataGapsListComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  const dataGaps: AviationAerCorsiaDataGaps = {
    exist: true,
    dataGapsDetails: {
      dataGapsPercentage: '3',
      dataGapsPercentageType: 'MORE_THAN_FIVE_PER_CENT',
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
    },
  };

  const aggregatedEmissionsData: AviationAerUkEtsAggregatedEmissionsData = {
    aggregatedEmissionDataDetails: [
      {
        airportFrom: { country: 'countryFrom', countryType: 'EEA_COUNTRY', icao: 'icaoFrom', name: 'nameFrom' },
        airportTo: { country: 'countryTo', countryType: 'EEA_COUNTRY', icao: 'icaoTo', name: 'nameTo' },
        fuelType: 'JET_KEROSENE',
        fuelConsumption: 'consumption',
        flightsNumber: 5,
      },
    ],
  };

  const aviationAerCorsia = {
    aggregatedEmissionsData: {
      ...aggregatedEmissionsData,
    },
    emissionsReductionClaim: {
      exist: true,
      emissionsReductionClaimDetails: {
        totalEmissions: '123',
      },
    },
  } as AviationAerCorsia;

  const mockAviationReportingService = {
    getStandardFuelsEmissionsCorsia: () => of(null),
    getTotalEmissionsCorsia: () =>
      of({
        aviationAerCorsia: aviationAerCorsia,
        corsiaRequestTaskPayload: {
          aer: aviationAerCorsia,
        } as AviationAerCorsiaApplicationSubmitRequestTaskPayload,
        totalEmissions: {
          allFlightsEmissions: '35650',
          allFlightsNumber: 2987,
          offsetFlightsEmissions: '1133',
          offsetFlightsNumber: 1240,
          nonOffsetFlightsEmissions: '19602',
          nonOffsetFlightsNumber: 1747,
          emissionsReductionClaim: '0',
        } as AviationAerCorsiaTotalEmissions,
      }),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataGapsListComponent],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: DataGapsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: AviationReportingService, useValue: mockAviationReportingService },
      ],
    }).compileComponents();
    store = TestBed.inject(RequestTaskStore);

    const state = store.getState();

    store.setState({
      ...state,
      requestTaskItem: {
        ...state.requestTaskItem,
        requestTask: {
          type: 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT',
          payload: {
            aer: {
              ...AerCorsiaStoreDelegate.INITIAL_STATE,
              dataGaps,
              aggregatedEmissionsData,
            },
          } as any,
        },
      },
    });

    fixture = TestBed.createComponent(DataGapsListComponent);
    component = fixture.componentInstance;
    component.formProvider.setFormValue(dataGaps);

    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display correct page heading', () => {
    expect(screen.getByText('List the data gaps')).toBeInTheDocument();
  });

  it('should call "saveAer" on submit and redirect to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));

    component.formProvider.setFormValue(dataGaps);

    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith({ dataGaps: component.formProvider.getFormValue() }, 'in progress');

    expect(navigateSpy).toHaveBeenCalledWith(['../', 'summary'], { relativeTo: activatedRouteStub });
  });
});
