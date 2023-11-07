import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { BasePage } from '@testing';

import {
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  AviationAerCorsiaInternationalFlightsEmissions,
  AviationReportingService,
} from 'pmrv-api';

import { TotalEmissionsCorsiaStatePairsTableTemplateComponent } from './total-emissions-corsia-state-pairs-table-template.component';

describe('TotalEmissionsCorsiaStatePairsTableTemplateComponent', () => {
  let page: Page;
  let component: TotalEmissionsCorsiaStatePairsTableTemplateComponent;
  let fixture: ComponentFixture<TotalEmissionsCorsiaStatePairsTableTemplateComponent>;

  const mockAviationReportingService = {
    getInternationalFlightsEmissionsCorsia: () =>
      of({
        flightsEmissionsDetails: [
          {
            departureState: 'United Kingdom',
            arrivalState: 'Greece',
            flightsNumber: 4,
            fuelType: 'JET_KEROSENE',
            offset: true,
            fuelConsumption: '5.261',
            emissions: '16.625',
          },
        ],
      } as AviationAerCorsiaInternationalFlightsEmissions),
  };

  class Page extends BasePage<TotalEmissionsCorsiaStatePairsTableTemplateComponent> {
    get tableRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TotalEmissionsCorsiaStatePairsTableTemplateComponent, RouterTestingModule],
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
    fixture = TestBed.createComponent(TotalEmissionsCorsiaStatePairsTableTemplateComponent);
    component = fixture.componentInstance;

    component.corsiaRequestTaskPayload = {
      reportingYear: 2023,
      aer: {
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
      },
    } as AviationAerCorsiaApplicationSubmitRequestTaskPayload;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the table', () => {
    expect(page.tableRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      ['United Kingdom', 'Greece', '4', 'Jet kerosene (Jet A1 or Jet A)', 'Yes', '5.261 t', '16.625 tCO2'],
    ]);
  });
});
