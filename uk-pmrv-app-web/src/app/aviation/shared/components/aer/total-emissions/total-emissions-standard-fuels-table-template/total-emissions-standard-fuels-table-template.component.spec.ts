import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TotalEmissionsStandardFuelsTableTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-standard-fuels-table-template/total-emissions-standard-fuels-table-template.component';
import { BasePage } from '@testing';

import { AviationReportingService } from 'pmrv-api';

describe('TotalEmissionsStandardFuelsTableTemplateComponent', () => {
  let page: Page;
  let component: TotalEmissionsStandardFuelsTableTemplateComponent;
  let fixture: ComponentFixture<TotalEmissionsStandardFuelsTableTemplateComponent>;

  const mockAviationReportingService = {
    getStandardFuelsEmissionsUkEts: () =>
      of([
        {
          fuelType: 'JET_KEROSENE',
          emissionsFactor: '1.0',
          netCalorificValue: '2.0',
          fuelConsumption: '3.0',
          emissions: '4.0',
        },
      ]),
  };

  class Page extends BasePage<TotalEmissionsStandardFuelsTableTemplateComponent> {
    get tableRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TotalEmissionsStandardFuelsTableTemplateComponent, RouterTestingModule],
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
    fixture = TestBed.createComponent(TotalEmissionsStandardFuelsTableTemplateComponent);
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
      ['Jet kerosene (Jet A1 or Jet A)', '1.0 tCO2/t fuel', '2.0 GJ/t fuel', '3.0 t', '4.0 tCO2'],
    ]);
  });
});
