import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TotalEmissionsAerodromePairsTableTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-aerodrome-pairs-table-template/total-emissions-aerodrome-pairs-table-template.component';
import { BasePage } from '@testing';

import { AviationReportingService } from 'pmrv-api';

describe('TotalEmissionsAerodromePairsTableTemplateComponent', () => {
  let page: Page;
  let component: TotalEmissionsAerodromePairsTableTemplateComponent;
  let fixture: ComponentFixture<TotalEmissionsAerodromePairsTableTemplateComponent>;

  const mockAviationReportingService = {
    getAerodromePairsEmissionsUkEts: () =>
      of([
        {
          departureAirport: {
            icao: 'EGNR',
            name: 'HAWARDEN',
            country: 'United Kingdom',
            countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
          },
          arrivalAirport: {
            icao: 'EGPN',
            name: 'DUNDEE',
            country: 'United Kingdom',
            countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
          },
          flightsNumber: 1,
          emissions: '2.592',
        },
      ]),
  };

  class Page extends BasePage<TotalEmissionsAerodromePairsTableTemplateComponent> {
    get tableRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TotalEmissionsAerodromePairsTableTemplateComponent, RouterTestingModule],
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
    fixture = TestBed.createComponent(TotalEmissionsAerodromePairsTableTemplateComponent);
    component = fixture.componentInstance;

    component.data = {
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
      saf: {
        exist: false,
      },
    } as any;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the table', () => {
    expect(page.tableRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      ['EGNR United Kingdom', 'EGPN United Kingdom', '1', '2.592'],
    ]);
  });
});
