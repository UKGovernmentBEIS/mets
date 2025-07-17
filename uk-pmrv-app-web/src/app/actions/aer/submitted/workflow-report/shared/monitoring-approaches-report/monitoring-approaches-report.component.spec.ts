import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MonitoringApproachesReportComponent } from './monitoring-approaches-report.component';

describe('MonitoringApproachesReportComponent', () => {
  let component: MonitoringApproachesReportComponent;
  let fixture: ComponentFixture<MonitoringApproachesReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MonitoringApproachesReportComponent],
      imports: [HttpClientTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MonitoringApproachesReportComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
