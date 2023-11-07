import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { MonitoringApproachTotalEmissionsFormComponent } from './monitoring-approach-total-emissions-form.component';

describe('MonitoringApproachTotalEmissionsFormComponent', () => {
  let fixture: ComponentFixture<MonitoringApproachTotalEmissionsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(MonitoringApproachTotalEmissionsFormComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });
});
