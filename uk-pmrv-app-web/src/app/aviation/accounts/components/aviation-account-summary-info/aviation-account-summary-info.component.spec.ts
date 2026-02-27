import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, RouterLink } from '@angular/router';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';

import { EtsNamePipe } from '../../pipes';
import { AviationAccountDetails } from '../../store';
import { AviationAccountSummaryInfoComponent } from './aviation-account-summary-info.component';

@Component({
  selector: 'app-test-parent',
  standalone: false,
  template: `
    <app-aviation-account-summary-info [summaryInfo]="summaryInfo"></app-aviation-account-summary-info>
  `,
})
class TestParentComponent {
  summaryInfo: AviationAccountDetails = {
    name: 'TEST',
    emissionTradingScheme: 'CORSIA',
    crcoCode: 'TEST',
    commencementDate: '1978-03-25',
    sopId: 3,
  };
}

describe('AviationAccountSummaryInfoComponent', () => {
  let component: TestParentComponent;
  let fixture: ComponentFixture<TestParentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterLink],
      declarations: [TestParentComponent, AviationAccountSummaryInfoComponent, EtsNamePipe, GovukDatePipe],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(TestParentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
