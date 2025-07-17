import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TotalEmissionsComponent } from '@aviation/request-task/aer/corsia/shared/tasks/total-emissions/total-emissions.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

import { AviationAerCorsia, AviationAerCorsiaTotalEmissions, AviationReportingService } from 'pmrv-api';

describe('TotalEmissionsComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: TotalEmissionsComponent;
  let fixture: ComponentFixture<TotalEmissionsComponent>;

  const aviationAerCorsia = {
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

  const mockAviationReportingService = {
    getStandardFuelsEmissionsCorsia: () => of(null),
    getTotalEmissionsCorsia: () =>
      of({
        allFlightsEmissions: '35650',
        allFlightsNumber: 2987,
        offsetFlightsEmissions: '1133',
        offsetFlightsNumber: 1240,
        nonOffsetFlightsEmissions: '19602',
        nonOffsetFlightsNumber: 1747,
        emissionsReductionClaim: '0',
      } as AviationAerCorsiaTotalEmissions),
  };

  class Page extends BasePage<TotalEmissionsComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get appStandardFuelsTable() {
      return this.query<HTMLDivElement>('app-total-emissions-corsia-standard-fuels-table-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TotalEmissionsComponent, RouterTestingModule],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: AviationReportingService, useValue: mockAviationReportingService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);

    store.setState({
      requestTaskItem: {
        requestInfo: { type: 'AVIATION_AER_CORSIA' },
        requestTask: {
          id: 19,
          type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
          payload: {
            payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
            aer: aviationAerCorsia,
          },
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(TotalEmissionsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.summaryValues).toEqual([
      ['Emissions from all international flights', '35650 tCO2 from 2987 flights'],
      ['Emissions from flights with offsetting requirements', '1133 tCO2 from 1240 flights'],
      ['Emissions from flights with no offsetting requirements', '19602 tCO2 from 1747 flights'],
      ['Emissions reduction claim from CORSIA eligible fuels', '0 tCO2'],
    ]);

    expect(page.appStandardFuelsTable).toBeTruthy();
  });
});
