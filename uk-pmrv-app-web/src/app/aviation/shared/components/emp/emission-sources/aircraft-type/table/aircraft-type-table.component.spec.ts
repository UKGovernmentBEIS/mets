import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AircraftTypeTableComponent } from '@aviation/shared/components/emp/emission-sources/aircraft-type/table/aircraft-type-table.component';
import { BasePage } from '@testing';

describe('AircraftTypeTableComponent', () => {
  let page: Page;
  let component: AircraftTypeTableComponent;
  let fixture: ComponentFixture<AircraftTypeTableComponent>;

  class Page extends BasePage<AircraftTypeTableComponent> {
    get tierRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AircraftTypeTableComponent, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AircraftTypeTableComponent);
    component = fixture.componentInstance;

    component.aircraftTypes = [
      {
        subtype: 'Sub Type',
        fuelTypes: ['AVIATION_GASOLINE', 'OTHER'],
        isCurrentlyUsed: true,
        aircraftTypeInfo: {
          model: 'Model',
          manufacturer: 'Manufacturer',
          designatorType: 'Designator Type',
        },
        numberOfAircrafts: 100,
        idx: 0
      },
    ];
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.tierRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      ['Manufacturer Model (Designator Type)', 'Sub Type', '100', 'Aviation gasoline,  Other', 'ChangeRemove'],
    ]);
  });
});
