import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { mockState } from '@aviation/request-task/aer/ukets/tasks/send-report/testing/mock-state';
import { RequestTaskState, RequestTaskStore } from '@aviation/request-task/store';
import { BasePage } from '@testing';

import {
  AviationAerCorsia,
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  AviationAerCorsiaTotalEmissions,
  AviationReportingService,
} from 'pmrv-api';

import { TotalEmissionsSchemeCorsiaComponent } from './total-emissions-scheme-corsia.component';

describe('TotalEmissionsSchemeCorsiaComponent', () => {
  let component: TotalEmissionsSchemeCorsiaComponent;
  let store: RequestTaskStore;
  let fixture: ComponentFixture<TotalEmissionsSchemeCorsiaComponent>;
  let page: Page;

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

  class Page extends BasePage<TotalEmissionsSchemeCorsiaComponent> {
    get headings(): string[] {
      return this.queryAll<HTMLHeadingElement>('h2').map((item) => item.textContent.trim());
    }

    get appSchemeYearSummary() {
      return this.query<HTMLDivElement>('app-total-emissions-corsia-scheme-year-summary');
    }

    get appStandardFuelsTable() {
      return this.query<HTMLDivElement>('app-total-emissions-corsia-standard-fuels-table-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, TotalEmissionsSchemeCorsiaComponent],
      providers: [{ provide: AviationReportingService, useValue: mockAviationReportingService }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState({
      ...mockState,
    } as RequestTaskState);

    fixture = TestBed.createComponent(TotalEmissionsSchemeCorsiaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    page = new Page(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display emissions for the scheme year', () => {
    expect(page.headings).toEqual(['Emissions for the scheme year', 'Emissions summary']);
    expect(page.appSchemeYearSummary).toBeTruthy();
    expect(page.appStandardFuelsTable).toBeTruthy();
  });
});
