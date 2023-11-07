import { ChangeDetectionStrategy } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { mockState } from '@aviation/request-task/aer/ukets/tasks/send-report/testing/mock-state';
import { RequestTaskState, RequestTaskStore } from '@aviation/request-task/store';
import { BasePage } from '@testing';

import { AviationReportingService } from 'pmrv-api';

import { TotalEmissionsStandardFuelsTableComponent } from './total-emissions-standard-fuels-table.component';

describe('TotalEmissionsStandardFuelsTableComponent', () => {
  let store: RequestTaskStore;
  let component: TotalEmissionsStandardFuelsTableComponent;
  let fixture: ComponentFixture<TotalEmissionsStandardFuelsTableComponent>;
  let page: Page;

  class Page extends BasePage<TotalEmissionsStandardFuelsTableComponent> {
    get tableRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  const mockAviationReportingService = {
    getStandardFuelsEmissionsUkEts: () =>
      of([
        {
          fuelType: 'JET_KEROSENE',
          emissionsFactor: '1.0',
          netCalorificValue: '2.0',
          fuelConsumption: '3.0',
          emissions: '4.0',
        },
      ]),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TotalEmissionsStandardFuelsTableComponent],
      providers: [{ provide: AviationReportingService, useValue: mockAviationReportingService }],
    })
      .overrideComponent(TotalEmissionsStandardFuelsTableComponent, {
        set: { changeDetection: ChangeDetectionStrategy.Default },
      })
      .compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState({
      ...mockState,
    } as RequestTaskState);

    fixture = TestBed.createComponent(TotalEmissionsStandardFuelsTableComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the table', () => {
    expect(page.tableRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      ['Jet kerosene (Jet A1 or Jet A)', '1.0 tCO2/t fuel', '2.0 GJ/t fuel', '3.0 t', '4.0 tCO2'],
    ]);
  });
});
