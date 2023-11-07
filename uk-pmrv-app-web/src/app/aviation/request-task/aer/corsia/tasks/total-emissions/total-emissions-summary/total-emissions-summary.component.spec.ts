import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { AviationAerCorsia, AviationAerCorsiaApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { TotalEmissionsSummaryComponent } from './total-emissions-summary.component';

describe('TotalEmissionsSummaryComponent', () => {
  let component: TotalEmissionsSummaryComponent;
  let fixture: ComponentFixture<TotalEmissionsSummaryComponent>;
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  const initialState = {
    aggregatedEmissionsData: {
      aggregatedEmissionDataDetails: [
        {
          airportFrom: { country: 'countryFrom', countryType: 'EEA_COUNTRY', icao: 'icaoFrom', name: 'nameFrom' },
          airportTo: { country: 'countryTo', countryType: 'EEA_COUNTRY', icao: 'icaoTo', name: 'nameTo' },
          fuelType: 'JET_KEROSENE',
          fuelConsumption: 'consumption',
          flightsNumber: 5,
        },
      ],
    },
    emissionsReductionClaim: {
      exist: true,
      emissionsReductionClaimDetails: {
        totalEmissions: '123',
      },
    },
  } as AviationAerCorsia;

  class Page extends BasePage<TotalEmissionsSummaryComponent> {
    get appSchemeCorsia() {
      return this.query<HTMLDivElement>('app-total-emissions-scheme-corsia');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, FormsModule, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
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
            aer: initialState,
            aerSectionsCompleted: {
              aggregatedEmissionsData: [true],
              emissionsReductionClaim: [true],
            },
          } as AviationAerCorsiaApplicationSubmitRequestTaskPayload,
        },
      },
    });

    fixture = TestBed.createComponent(TotalEmissionsSummaryComponent);
    component = fixture.componentInstance;
    router = fixture.debugElement.injector.get(Router);
    fixture.detectChanges();
    page = new Page(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display HTML element', () => {
    expect(page.appSchemeCorsia).toBeTruthy();
  });

  it('should call saveAer on submit and redirect to task list', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    component.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith({ totalEmissionsCorsia: undefined }, 'complete');
    expect(navigateSpy).toHaveBeenCalledWith(['../../../'], { relativeTo: activatedRouteStub });
  });
});
