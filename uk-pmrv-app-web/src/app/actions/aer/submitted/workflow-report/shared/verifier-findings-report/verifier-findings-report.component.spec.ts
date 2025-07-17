import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerifierFindingsReportComponent } from './verifier-findings-report.component';

describe('VerifierFindingsReportComponent', () => {
  let component: VerifierFindingsReportComponent;
  let fixture: ComponentFixture<VerifierFindingsReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VerifierFindingsReportComponent],
      imports: [HttpClientTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VerifierFindingsReportComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
