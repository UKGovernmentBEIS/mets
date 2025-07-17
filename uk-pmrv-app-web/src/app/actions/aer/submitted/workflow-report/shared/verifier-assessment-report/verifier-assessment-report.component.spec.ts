import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerifierAssessmentReportComponent } from './verifier-assessment-report.component';

describe('VerifierAssessmentReportComponent', () => {
  let component: VerifierAssessmentReportComponent;
  let fixture: ComponentFixture<VerifierAssessmentReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VerifierAssessmentReportComponent],
      imports: [HttpClientTestingModule],
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
