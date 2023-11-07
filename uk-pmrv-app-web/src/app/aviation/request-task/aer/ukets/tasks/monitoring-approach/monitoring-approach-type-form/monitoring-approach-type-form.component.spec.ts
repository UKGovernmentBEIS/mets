import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { MonitoringApproachTypeFormComponent } from './monitoring-approach-type-form.component';

describe('MonitoringApproachTypeFormComponent', () => {
  let fixture: ComponentFixture<MonitoringApproachTypeFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(MonitoringApproachTypeFormComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });
});
