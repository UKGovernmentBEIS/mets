import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TotalEmissionsNonDomesticFlightsTableTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-non-domestic-flights-table-template/total-emissions-non-domestic-flights-table-template.component';
import { BasePage } from '@testing';

import { AviationReportingService } from 'pmrv-api';

describe('TotalEmissionsNonDomesticFlightsTableTemplateComponent', () => {
  let page: Page;
  let component: TotalEmissionsNonDomesticFlightsTableTemplateComponent;
  let fixture: ComponentFixture<TotalEmissionsNonDomesticFlightsTableTemplateComponent>;

  const mockAviationReportingService = {
    getNonDomesticFlightsEmissionsUkEts: () =>
      of({
        nonDomesticFlightsEmissionsDetails: [
          {
            flightsNumber: 4,
            fuelType: 'JET_KEROSENE',
            fuelConsumption: '5.261',
            emissions: '16.572',
            departureCountry: 'United Kingdom',
            arrivalCountry: 'Belgium',
          },
        ],
      }),
  };

  class Page extends BasePage<TotalEmissionsNonDomesticFlightsTableTemplateComponent> {
    get tableRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TotalEmissionsNonDomesticFlightsTableTemplateComponent, RouterTestingModule],
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
    fixture = TestBed.createComponent(TotalEmissionsNonDomesticFlightsTableTemplateComponent);
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
      ['United Kingdom', 'Belgium', '4', 'Jet kerosene (Jet A1 or Jet A)', '5.261', '16.572'],
    ]);
  });
});
