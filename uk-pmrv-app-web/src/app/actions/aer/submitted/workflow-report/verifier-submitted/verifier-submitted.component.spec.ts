import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionReportService } from '@shared/services/request-action-report.service';
import { ActivatedRouteStub, mockClass } from '@testing';

import { VerifierSubmittedComponent } from './verifier-submitted.component';

describe('VerifierSubmittedComponent', () => {
  let component: VerifierSubmittedComponent;
  let fixture: ComponentFixture<VerifierSubmittedComponent>;
  let route: ActivatedRouteStub;

  const requestActionReportService = mockClass(RequestActionReportService);

  beforeEach(async () => {
    route = new ActivatedRouteStub({ 'request-id': 1 });
    await TestBed.configureTestingModule({
      declarations: [VerifierSubmittedComponent],
      providers: [
        provideHttpClient(),
        { provide: RequestActionReportService, useValue: requestActionReportService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VerifierSubmittedComponent);
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
