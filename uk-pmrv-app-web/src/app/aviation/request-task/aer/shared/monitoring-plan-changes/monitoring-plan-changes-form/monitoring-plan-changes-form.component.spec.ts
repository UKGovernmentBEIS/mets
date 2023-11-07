import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { MonitoringPlanChangesFormComponent } from './monitoring-plan-changes-form.component';

describe('MonitoringPlanChangesFormComponent', () => {
  let fixture: ComponentFixture<MonitoringPlanChangesFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(MonitoringPlanChangesFormComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });
});
