import { ComponentFixture, TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { mockState } from '@aviation/request-task/aer/ukets/tasks/send-report/testing/mock-state';
import { TotalEmissionsDomesticFlightsTableComponent } from '@aviation/request-task/aer/ukets/tasks/total-emissions/table/total-emissions-domestic-flights-table/total-emissions-domestic-flights-table.component';
import { RequestTaskState, RequestTaskStore } from '@aviation/request-task/store';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { AviationReportingService } from 'pmrv-api';

describe('TotalEmissionsDomesticFlightsTableComponent', () => {
  let store: RequestTaskStore;
  let component: TotalEmissionsDomesticFlightsTableComponent;
  let fixture: ComponentFixture<TotalEmissionsDomesticFlightsTableComponent>;
  let page: Page;

  class Page extends BasePage<TotalEmissionsDomesticFlightsTableComponent> {
    get tableRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TotalEmissionsDomesticFlightsTableComponent],
      providers: [{ provide: AviationReportingService, useValue: mockAviationReportingService }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState({
      ...mockState,
    } as RequestTaskState);

    fixture = TestBed.createComponent(TotalEmissionsDomesticFlightsTableComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
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
