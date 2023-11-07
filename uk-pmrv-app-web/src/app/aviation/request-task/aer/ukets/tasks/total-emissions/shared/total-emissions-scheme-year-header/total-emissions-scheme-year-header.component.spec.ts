import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { mockState } from '@aviation/request-task/aer/ukets/tasks/send-report/testing/mock-state';
import { RequestTaskState, RequestTaskStore } from '@aviation/request-task/store';

import { AviationReportingService } from 'pmrv-api';

import { TotalEmissionsSchemeYearHeaderComponent } from './total-emissions-scheme-year-header.component';

describe('TotalEmissionsSchemeYearHeaderComponent', () => {
  let store: RequestTaskStore;
  let fixture: ComponentFixture<TotalEmissionsSchemeYearHeaderComponent>;

  const mockAviationReportingService = {
    getTotalEmissionsUkEts: () => of(null),
    getStandardFuelsEmissionsUkEts: () => of(null),
    getAerodromePairsEmissionsUkEts: () => of(null),
    getDomesticFlightsEmissionsUkEts: () => of(null),
    getNonDomesticFlightsEmissionsUkEts: () => of(null),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, TotalEmissionsSchemeYearHeaderComponent],
      providers: [{ provide: AviationReportingService, useValue: mockAviationReportingService }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState({
      ...mockState,
    } as RequestTaskState);

    fixture = TestBed.createComponent(TotalEmissionsSchemeYearHeaderComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display emissions for the scheme year', () => {
    const heading = fixture.nativeElement.querySelector('h2');
    expect(heading.textContent).toContain('Emissions for the scheme year');
  });
});
