import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { MonitoringApproachNumberFlightsFormComponent } from './monitoring-approach-number-flights-form.component';

describe('MonitoringApproachNumberFlightsFormComponent', () => {
  let fixture: ComponentFixture<MonitoringApproachNumberFlightsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(MonitoringApproachNumberFlightsFormComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });
});
