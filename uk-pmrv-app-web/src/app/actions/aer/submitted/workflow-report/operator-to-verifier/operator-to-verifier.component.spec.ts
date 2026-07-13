import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionReportService } from '@shared/services/request-action-report.service';
import { ActivatedRouteStub, mockClass } from '@testing';

import { OperatorToVerifierComponent } from './operator-to-verifier.component';

describe('OperatorToVerifierComponent', () => {
  let component: OperatorToVerifierComponent;
  let fixture: ComponentFixture<OperatorToVerifierComponent>;
  let route: ActivatedRouteStub;

  const requestActionReportService = mockClass(RequestActionReportService);

  beforeEach(async () => {
    route = new ActivatedRouteStub({ 'request-id': 1 });
    await TestBed.configureTestingModule({
      declarations: [OperatorToVerifierComponent],
      providers: [
        provideHttpClient(),
        { provide: RequestActionReportService, useValue: requestActionReportService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OperatorToVerifierComponent);
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
