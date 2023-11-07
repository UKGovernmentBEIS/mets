import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { GovukComponentsModule } from 'govuk-components';

import { FlightDataTableComponent } from './flight-data-table.component';

describe('FlightDataTableComponent', () => {
  let component: FlightDataTableComponent;
  let fixture: ComponentFixture<FlightDataTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(FlightDataTableComponent);
    component = fixture.componentInstance;
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

  it('should display the table with emission data details', () => {
    component.emissionDataDetails = [
      {
        airportFrom: { icao: 'KSFO', name: 'KSFO', country: 'Spain', countryType: 'EEA_COUNTRY' },
        airportTo: { icao: 'KLAX', name: 'KLAX', country: 'Spain', countryType: 'EEA_COUNTRY' },
        fuelType: 'JET_KEROSENE',
        fuelConsumption: '100',
        flightsNumber: 5,
      },
    ];
    fixture.detectChanges();

    const tableElement = fixture.debugElement.query(By.css('govuk-table'));
    expect(tableElement).toBeTruthy();
  });
});
