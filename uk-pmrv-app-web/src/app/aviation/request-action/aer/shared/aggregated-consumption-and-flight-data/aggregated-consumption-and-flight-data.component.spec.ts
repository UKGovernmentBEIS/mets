import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import AggregatedConsumptionAndFlightDataComponent from '@aviation/request-action/aer/shared/aggregated-consumption-and-flight-data/aggregated-consumption-and-flight-data.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerUkEtsRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

class Page extends BasePage<AggregatedConsumptionAndFlightDataComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get tableRows(): HTMLTableRowElement[] {
    return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
  }
}

describe('AggregatedConsumptionAndFlightDataComponent', () => {
  let component: AggregatedConsumptionAndFlightDataComponent;
  let fixture: ComponentFixture<AggregatedConsumptionAndFlightDataComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, AggregatedConsumptionAndFlightDataComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_AER_UKETS_APPLICATION_SUBMITTED',
        creationDate: '2022-11-29T12:12:48.469862Z',
        payload: {
          aer: {
            aggregatedEmissionsData: {
              aggregatedEmissionDataDetails: [
                {
                  fuelType: 'JET_KEROSENE',
                  airportTo: {
                    icao: 'EBBR',
                    name: 'BRUSSELS/BRUSSELS-NATIONAL',
                    country: 'Belgium',
                    countryType: 'EEA_COUNTRY',
                  },
                  airportFrom: {
                    icao: 'EGGW',
                    name: 'LONDON LUTON',
                    country: 'United Kingdom',
                    countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
                  },
                  flightsNumber: 4,
                  fuelConsumption: '5.261',
                },
              ],
            },
          },
        } as AerUkEtsRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(AggregatedConsumptionAndFlightDataComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Aggregated consumption and flight data');
    expect(page.tableRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      ['EGGW', 'EBBR', 'Jet kerosene (Jet A1 or Jet A)', '5.261', '4'],
    ]);
  });
});
