import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { Observable, of } from 'rxjs';

import { BasePage } from '@testing';

import { AviationAerCorsia, AviationAerCorsiaStandardFuelsTotalEmissions, AviationReportingService } from 'pmrv-api';

import { TotalEmissionsCorsiaStandardFuelsTableTemplateComponent } from './total-emissions-corsia-standard-fuels-table-template.component';

describe('TotalEmissionsCorsiaStandardFuelsTableTemplateComponent', () => {
  let page: Page;
  let component: TotalEmissionsCorsiaStandardFuelsTableTemplateComponent;
  let fixture: ComponentFixture<TotalEmissionsCorsiaStandardFuelsTableTemplateComponent>;

  const mockAviationReportingService = {
    getStandardFuelsEmissionsCorsia: () =>
      of([
        {
          fuelType: 'JET_KEROSENE',
          emissionsFactor: '1.0',
          fuelConsumption: '3.0',
          emissions: '4.0',
        },
      ]) as Observable<AviationAerCorsiaStandardFuelsTotalEmissions[]>,
  };

  class Page extends BasePage<TotalEmissionsCorsiaStandardFuelsTableTemplateComponent> {
    get tableRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TotalEmissionsCorsiaStandardFuelsTableTemplateComponent, RouterTestingModule],
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
    fixture = TestBed.createComponent(TotalEmissionsCorsiaStandardFuelsTableTemplateComponent);
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
      ['Jet kerosene (Jet A1 or Jet A)', '1.0 tCO2/t fuel', '3.0 t', '4.0 tCO2'],
    ]);
  });
});
