import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { FlightProceduresDataTableComponent } from './flight-procedures-data-table.component';

describe('FlightProceduresDataTableComponent', () => {
  let fixture: ComponentFixture<FlightProceduresDataTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlightProceduresDataTableComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(FlightProceduresDataTableComponent);

    fixture.componentInstance.headingText = 'Test Heading';

    fixture.componentInstance.operatingStatePairsCorsiaDetails = [
      {
        stateA: 'Canada',
        stateB: 'Mexico',
      },
    ];

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display heading text', () => {
    const headingElement = fixture.debugElement.query(By.css('.govuk-heading-m'));
    expect(headingElement.nativeElement.textContent).toBe('Test Heading');
  });

  it('should display the table with emission data details', () => {
    const tableElement = fixture.debugElement.query(By.css('govuk-table'));
    expect(tableElement).toBeTruthy();
  });
});
