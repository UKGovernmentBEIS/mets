import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AerModule } from '@actions/aer/aer.module';

import { VerifierAssessmentReportComponent } from './verifier-assessment-report.component';

describe('VerifierAssessmentReportComponent', () => {
  let component: VerifierAssessmentReportComponent;
  let fixture: ComponentFixture<VerifierAssessmentReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
      imports: [AerModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VerifierAssessmentReportComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
