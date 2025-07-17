import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AircraftTypesDataComponent } from '@aviation/request-task/aer/corsia/shared/tasks/aircraft-types-data/aircraft-types-data.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';

describe('AircraftTypesDataComponent', () => {
  let page: Page;
  let store: RequestTaskStore;
  let component: AircraftTypesDataComponent;
  let fixture: ComponentFixture<AircraftTypesDataComponent>;

  class Page extends BasePage<AircraftTypesDataComponent> {
    get tableRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AircraftTypesDataComponent, RouterTestingModule],
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
              aviationAerAircraftData: {
                aviationAerAircraftDataDetails: [
                  {
                    aircraftTypeDesignator: 'C560',
                    subType: '',
                    registrationNumber: 'D-CAPB',
                    ownerOrLessor: 'Aviation Operator',
                    startDate: '2021-01-01',
                    endDate: '2021-12-31',
                  },
                  {
                    aircraftTypeDesignator: 'C560',
                    subType: '',
                    registrationNumber: 'D-CAPB',
                    ownerOrLessor: 'Aviation Operator',
                    startDate: '2021-01-01',
                    endDate: '2021-12-31',
                  },
                ],
              },
            },
          },
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(AircraftTypesDataComponent);
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
      ['C560', '', 'D-CAPB', 'Aviation Operator', '01/01/2021', '31/12/2021'],
      ['C560', '', 'D-CAPB', 'Aviation Operator', '01/01/2021', '31/12/2021'],
    ]);
  });
});
