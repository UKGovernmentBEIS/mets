import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { MonitoringApproachSummaryTemplateComponent } from './monitoring-approach-summary-template.component';

describe('MonitoringApproachSummaryTemplateComponent', () => {
  let fixture: ComponentFixture<MonitoringApproachSummaryTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(MonitoringApproachSummaryTemplateComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });
});
