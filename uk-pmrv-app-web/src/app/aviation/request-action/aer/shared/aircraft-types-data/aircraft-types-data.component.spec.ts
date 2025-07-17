import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import AircraftTypesDataComponent from '@aviation/request-action/aer/shared/aircraft-types-data/aircraft-types-data.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerUkEtsRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

class Page extends BasePage<AircraftTypesDataComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get tableRows(): HTMLTableRowElement[] {
    return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
  }
}

describe('AircraftTypesDataComponent', () => {
  let component: AircraftTypesDataComponent;
  let fixture: ComponentFixture<AircraftTypesDataComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, AircraftTypesDataComponent],
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
            aviationAerAircraftData: {
              aviationAerAircraftDataDetails: [
                {
                  endDate: '2022-12-31',
                  startDate: '2022-01-02',
                  ownerOrLessor: 'AviationName',
                  registrationNumber: '9HIBJ',
                  aircraftTypeDesignator: 'A320',
                },
              ],
            },
          },
        } as AerUkEtsRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(AircraftTypesDataComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Aircraft types data');
    expect(page.tableRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      ['A320', '', '9HIBJ', 'AviationName', '02/01/2022', '31/12/2022'],
    ]);
  });
});
