import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestActionReportService } from '@shared/services/request-action-report.service';
import { mockClass } from '@testing';

import { OperatorSubmittedComponent } from './operator-submitted.component';

describe('OperatorSubmittedComponent', () => {
  let component: OperatorSubmittedComponent;
  let fixture: ComponentFixture<OperatorSubmittedComponent>;

  const requestActionReportService = mockClass(RequestActionReportService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OperatorSubmittedComponent],
      imports: [HttpClientTestingModule],
      providers: [{ provide: RequestActionReportService, useValue: requestActionReportService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OperatorSubmittedComponent);
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
