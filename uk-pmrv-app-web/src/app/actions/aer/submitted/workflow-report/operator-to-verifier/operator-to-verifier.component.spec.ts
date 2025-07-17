import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestActionReportService } from '@shared/services/request-action-report.service';
import { mockClass } from '@testing';

import { OperatorToVerifierComponent } from './operator-to-verifier.component';

describe('OperatorToVerifierComponent', () => {
  let component: OperatorToVerifierComponent;
  let fixture: ComponentFixture<OperatorToVerifierComponent>;

  const requestActionReportService = mockClass(RequestActionReportService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OperatorToVerifierComponent],
      imports: [HttpClientTestingModule],
      providers: [{ provide: RequestActionReportService, useValue: requestActionReportService }],
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
