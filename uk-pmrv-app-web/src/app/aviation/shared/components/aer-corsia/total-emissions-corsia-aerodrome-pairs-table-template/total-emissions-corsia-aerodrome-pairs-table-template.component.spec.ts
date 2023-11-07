import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { Observable, of } from 'rxjs';

import { BasePage } from '@testing';

import { AviationAerCorsia, AviationAerCorsiaAerodromePairsTotalEmissions, AviationReportingService } from 'pmrv-api';

import { TotalEmissionsCorsiaAerodromePairsTableTemplateComponent } from './total-emissions-corsia-aerodrome-pairs-table-template.component';

describe('TotalEmissionsCorsiaAerodromePairsTableTemplateComponent', () => {
  let page: Page;
  let component: TotalEmissionsCorsiaAerodromePairsTableTemplateComponent;
  let fixture: ComponentFixture<TotalEmissionsCorsiaAerodromePairsTableTemplateComponent>;

  const mockAviationReportingService = {
    getAerodromePairsEmissionsCorsia: () =>
      of([
        {
          departureAirport: {
            icao: 'EGNR',
            name: 'HAWARDEN',
            country: 'United Kingdom',
            countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
            state: 'state 1',
          },
          arrivalAirport: {
            icao: 'EGPN',
            name: 'DUNDEE',
            country: 'United Kingdom',
            countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
            state: 'state 2',
          },
          flightsNumber: 1,
          emissions: '2.592',
          offset: true,
        },
      ]) as Observable<AviationAerCorsiaAerodromePairsTotalEmissions[]>,
  };

  class Page extends BasePage<TotalEmissionsCorsiaAerodromePairsTableTemplateComponent> {
    get tableRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TotalEmissionsCorsiaAerodromePairsTableTemplateComponent, RouterTestingModule],
      providers: [
        { provide: AviationReportingService, useValue: mockAviationReportingService },
        {
          provide: ActivatedRoute,
          useValue: {
            queryParamMap: of(new Map([['pagination', 'someValue']])),
          },
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TotalEmissionsCorsiaAerodromePairsTableTemplateComponent);
    component = fixture.componentInstance;

    component.aviationAerCorsia = {
      aggregatedEmissionsData: {
        aggregatedEmissionDataDetails: [
          {
            airportFrom: {
              icao: 'EGWU',
              name: 'NORTHOLT',
              country: 'United Kingdom',
              countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
            },
            airportTo: {
              icao: 'EIDW',
              name: 'DUBLIN INTERNATIONAL',
              country: 'Ireland',
              countryType: 'EEA_COUNTRY',
            },
            fuelType: 'JET_KEROSENE',
            fuelConsumption: '1.938',
            flightsNumber: 2,
          },
        ],
      },
      emissionsReductionClaim: {
        exist: true,
        emissionsReductionClaimDetails: {
          cefFiles: [],
          totalEmissions: '333',
          noDoubleCountingDeclarationFiles: [],
        },
      },
    } as AviationAerCorsia;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the table', () => {
    expect(page.tableRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      ['EGNR state 1', 'EGPN state 2', '1', 'Yes', '2.592 tCO2'],
    ]);
  });
});
