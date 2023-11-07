import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import TotalEmissionsComponent from '@aviation/request-action/aer/corsia/tasks/total-emissions/total-emissions.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerCorsiaRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { AviationAerCorsia, AviationAerCorsiaTotalEmissions, AviationReportingService } from 'pmrv-api';

class Page extends BasePage<TotalEmissionsComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
  get appStandardFuelsTable() {
    return this.query<HTMLDivElement>('app-total-emissions-corsia-standard-fuels-table-template');
  }
}

describe('TotalEmissionsComponent', () => {
  let component: TotalEmissionsComponent;
  let fixture: ComponentFixture<TotalEmissionsComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
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
        allFlightsEmissions: '35650',
        allFlightsNumber: 2987,
        offsetFlightsEmissions: '1133',
        offsetFlightsNumber: 1240,
        nonOffsetFlightsEmissions: '19602',
        nonOffsetFlightsNumber: 1747,
        emissionsReductionClaim: '0',
      } as AviationAerCorsiaTotalEmissions),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, TotalEmissionsComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
        { provide: AviationReportingService, useValue: mockAviationReportingService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER',
        creationDate: '2022-11-29T12:12:48.469862Z',
        payload: {
          aer: aviationAerCorsia,
        } as AerCorsiaRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(TotalEmissionsComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Total emissions');
    expect(page.summaryValues).toEqual([
      ['Emissions from all international flights', '35650 tCO2 from 2987 flights'],
      ['Emissions from flights with offsetting requirements', '1133 tCO2 from 1240 flights'],
      ['Emissions from flights with no offsetting requirements', '19602 tCO2 from 1747 flights'],
      ['Emissions reduction claim from CORSIA eligible fuels', '0 tCO2'],
    ]);
    expect(page.appStandardFuelsTable).toBeTruthy();
  });
});
