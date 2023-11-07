import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { mockState } from '@aviation/request-task/aer/ukets/tasks/send-report/testing/mock-state';
import { RequestTaskState, RequestTaskStore } from '@aviation/request-task/store';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { AviationReportingService } from 'pmrv-api';

import { TotalEmissionsAerodromePairsTableComponent } from './total-emissions-aerodrome-pairs-table.component';

describe('TotalEmissionsAerodromePairsTableComponent', () => {
  let store: RequestTaskStore;
  let component: TotalEmissionsAerodromePairsTableComponent;
  let fixture: ComponentFixture<TotalEmissionsAerodromePairsTableComponent>;
  let page: Page;

  class Page extends BasePage<TotalEmissionsAerodromePairsTableComponent> {
    get tableRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  const mockAviationReportingService = {
    getAerodromePairsEmissionsUkEts: () =>
      of([
        {
          departureAirport: {
            icao: 'EGNR',
            name: 'HAWARDEN',
            country: 'United Kingdom',
            countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
          },
          arrivalAirport: {
            icao: 'EGPN',
            name: 'DUNDEE',
            country: 'United Kingdom',
            countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
          },
          flightsNumber: 1,
          emissions: '2.592',
        },
      ]),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TotalEmissionsAerodromePairsTableComponent],
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

    fixture = TestBed.createComponent(TotalEmissionsAerodromePairsTableComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the table', () => {
    expect(page.tableRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      ['EGNR United Kingdom', 'EGPN United Kingdom', '1', '2.592'],
    ]);
  });
});
