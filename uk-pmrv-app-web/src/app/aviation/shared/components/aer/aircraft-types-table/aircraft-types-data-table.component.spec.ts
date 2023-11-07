import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { BasePage } from '@testing';

import { GovukComponentsModule } from 'govuk-components';

import { AircraftTypesDataTableComponent } from './aircraft-types-data-table.component';

describe('AircraftTypesDataTableComponent', () => {
  let component: AircraftTypesDataTableComponent;
  let fixture: ComponentFixture<AircraftTypesDataTableComponent>;
  let page: Page;

  class Page extends BasePage<AircraftTypesDataTableComponent> {
    get tierRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AircraftTypesDataTableComponent);
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
    component.aviationAerAircraftDataDetails = [
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
    ];
    fixture.detectChanges();

    const tableElement = fixture.debugElement.query(By.css('govuk-table'));
    expect(tableElement).toBeTruthy();
    expect(page.tierRows.map((row) => Array.from(row.cells).map((col) => col.textContent.trim()))).toEqual([
      ['C560', '', 'D-CAPB', 'Aviation Operator', '01/01/2021', '31/12/2021'],
      ['C560', '', 'D-CAPB', 'Aviation Operator', '01/01/2021', '31/12/2021'],
    ]);
  });
});
