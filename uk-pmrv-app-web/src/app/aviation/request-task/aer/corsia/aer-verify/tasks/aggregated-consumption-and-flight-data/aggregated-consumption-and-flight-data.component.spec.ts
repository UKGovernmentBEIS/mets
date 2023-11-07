import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AggregatedConsumptionAndFlightDataComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/aggregated-consumption-and-flight-data/aggregated-consumption-and-flight-data.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

describe('AggregatedConsumptionAndFlightDataComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: AggregatedConsumptionAndFlightDataComponent;
  let fixture: ComponentFixture<AggregatedConsumptionAndFlightDataComponent>;

  class Page extends BasePage<AggregatedConsumptionAndFlightDataComponent> {
    get tableRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AggregatedConsumptionAndFlightDataComponent, RouterTestingModule],
      providers: [{ provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);

    store.setState({
      requestTaskItem: {
        requestInfo: { type: 'AVIATION_AER_CORSIA' },
        requestTask: {
          id: 19,
          type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
          payload: {
            payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
            aer: {
              aggregatedEmissionsData: {
                aggregatedEmissionDataDetails: [
                  {
                    airportFrom: { icao: 'KSFO', name: 'KSFO', country: 'Spain', countryType: 'EEA_COUNTRY' },
                    airportTo: { icao: 'KLAX', name: 'KLAX', country: 'Spain', countryType: 'EEA_COUNTRY' },
                    fuelType: 'JET_KEROSENE',
                    fuelConsumption: '100',
                    flightsNumber: 5,
                  },
                ],
              },
            },
          },
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(AggregatedConsumptionAndFlightDataComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.tableRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      ['KSFO', 'KLAX', 'Jet kerosene (Jet A1 or Jet A)', '100', '5'],
    ]);
  });
});
