import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrintComponent } from '@shared/print/print.component';
import { RequestActionReportService } from '@shared/services/request-action-report.service';

import { RequestActionReportComponent } from './request-action-report.component';

describe('RequestActionReportComponent', () => {
  let component: RequestActionReportComponent;
  let fixture: ComponentFixture<RequestActionReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RequestActionReportComponent, PrintComponent],
      providers: [RequestActionReportService],
    }).compileComponents();

    fixture = TestBed.createComponent(RequestActionReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
