import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { mockState } from '@aviation/request-task/aer/ukets/tasks/send-report/testing/mock-state';
import { RequestTaskState, RequestTaskStore } from '@aviation/request-task/store';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { AviationReportingService } from 'pmrv-api';

import { TotalEmissionsNonDomesticFlightsTableComponent } from './total-emissions-non-domestic-flights-table.component';

describe('TotalEmissionsNonDomesticFlightsTableComponent', () => {
  let store: RequestTaskStore;
  let component: TotalEmissionsNonDomesticFlightsTableComponent;
  let fixture: ComponentFixture<TotalEmissionsNonDomesticFlightsTableComponent>;
  let page: Page;

  class Page extends BasePage<TotalEmissionsNonDomesticFlightsTableComponent> {
    get tableRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TotalEmissionsNonDomesticFlightsTableComponent],
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

    store = TestBed.inject(RequestTaskStore);
    store.setState({
      ...mockState,
    } as RequestTaskState);

    fixture = TestBed.createComponent(TotalEmissionsNonDomesticFlightsTableComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
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
