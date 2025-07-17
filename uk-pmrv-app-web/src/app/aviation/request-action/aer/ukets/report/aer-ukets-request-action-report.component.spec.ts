import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestActionReportService } from '@shared/services/request-action-report.service';
import { mockClass } from '@testing';

import { AerUketsRequestActionReportComponent } from './aer-ukets-request-action-report.component';

describe('AerUketsRequestActionReportComponent', () => {
  let component: AerUketsRequestActionReportComponent;
  let fixture: ComponentFixture<AerUketsRequestActionReportComponent>;

  const requestActionReportService = mockClass(RequestActionReportService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerUketsRequestActionReportComponent],
      providers: [{ provide: RequestActionReportService, useValue: requestActionReportService }],
    }).compileComponents();

    fixture = TestBed.createComponent(AerUketsRequestActionReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should invoke print upon load', () => {
    setTimeout(() => {
      expect(component).toBeTruthy();
      expect(requestActionReportService.print).toHaveBeenCalledTimes(1);
    }, 1000);
  });
});
