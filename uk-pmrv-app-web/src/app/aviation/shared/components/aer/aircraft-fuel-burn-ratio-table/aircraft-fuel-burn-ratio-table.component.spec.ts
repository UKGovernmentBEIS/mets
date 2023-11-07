import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { BasePage } from '@testing';

import { GovukComponentsModule } from 'govuk-components';

import { AircraftFuelBurnRatioTableComponent } from './aircraft-fuel-burn-ratio-table.component';

describe('AircraftTypesDataTableComponent', () => {
  let component: AircraftFuelBurnRatioTableComponent;
  let fixture: ComponentFixture<AircraftFuelBurnRatioTableComponent>;
  let page: Page;

  class Page extends BasePage<AircraftFuelBurnRatioTableComponent> {
    get tierRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AircraftFuelBurnRatioTableComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display heading text', () => {
    component.headingText = 'Test Heading';
    fixture.detectChanges();

    const headingElement = fixture.debugElement.query(By.css('.govuk-heading-m'));
    expect(headingElement.nativeElement.textContent).toBe('Test Heading');
  });

  it('should display the table with aircraft data details', () => {
    component.aviationAerCorsiaAircraftTypeDetails = [
      {
        designator: 'C560',
        subtype: 'Cessna',
        fuelBurnRatio: '1.234',
      },
      {
        designator: 'A320',
        subtype: 'Airbus',
        fuelBurnRatio: '5.432',
      },
      {
        designator: 'A320',
        subtype: '',
        fuelBurnRatio: '10.432',
      },
    ];
    fixture.detectChanges();

    const tableElement = fixture.debugElement.query(By.css('govuk-table'));
    expect(tableElement).toBeTruthy();
    expect(page.tierRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      ['C560', 'Cessna', '1.234'],
      ['A320', 'Airbus', '5.432'],
      ['A320', '', '10.432'],
    ]);
  });
});
