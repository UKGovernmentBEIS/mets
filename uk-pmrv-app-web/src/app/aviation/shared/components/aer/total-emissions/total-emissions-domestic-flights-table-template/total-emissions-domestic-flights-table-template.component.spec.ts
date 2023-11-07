import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TotalEmissionsDomesticFlightsTableTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-domestic-flights-table-template/total-emissions-domestic-flights-table-template.component';
import { BasePage } from '@testing';

import { AviationReportingService } from 'pmrv-api';

describe('TotalEmissionsDomesticFlightsTableTemplateComponent', () => {
  let page: Page;
  let component: TotalEmissionsDomesticFlightsTableTemplateComponent;
  let fixture: ComponentFixture<TotalEmissionsDomesticFlightsTableTemplateComponent>;

  const mockAviationReportingService = {
    getDomesticFlightsEmissionsUkEts: () =>
      of({
        domesticFlightsEmissionsDetails: [
          {
            flightsNumber: 151,
            fuelType: 'JET_KEROSENE',
            fuelConsumption: '130.364',
            emissions: '410.647',
            country: 'United Kingdom',
          },
        ],
        totalEmissions: '410.647',
      }),
  };

  class Page extends BasePage<TotalEmissionsDomesticFlightsTableTemplateComponent> {
    get tableRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TotalEmissionsDomesticFlightsTableTemplateComponent, RouterTestingModule],
      providers: [{ provide: AviationReportingService, useValue: mockAviationReportingService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TotalEmissionsDomesticFlightsTableTemplateComponent);
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
      ['United Kingdom', '151', 'Jet kerosene (Jet A1 or Jet A)', '130.364', '410.647'],
    ]);
  });
});
