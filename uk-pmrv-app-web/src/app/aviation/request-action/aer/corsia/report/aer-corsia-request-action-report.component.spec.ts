import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestActionReportService } from '@shared/services/request-action-report.service';
import { mockClass } from '@testing';

import { AerCorsiaRequestActionReportComponent } from './aer-corsia-request-action-report.component';

describe('AerCorsiaRequestActionReportComponent', () => {
  let component: AerCorsiaRequestActionReportComponent;
  let fixture: ComponentFixture<AerCorsiaRequestActionReportComponent>;

  const requestActionReportService = mockClass(RequestActionReportService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerCorsiaRequestActionReportComponent],
      providers: [{ provide: RequestActionReportService, useValue: requestActionReportService }],
    }).compileComponents();

    fixture = TestBed.createComponent(AerCorsiaRequestActionReportComponent);
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
