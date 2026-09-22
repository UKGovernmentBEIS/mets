import { LowerCasePipe } from '@angular/common';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, RouterLink } from '@angular/router';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';

import { EtsNamePipe, FyroLabelPipe } from '../../pipes';
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

  function fyroRowText(): string {
    const dtElements = Array.from(fixture.nativeElement.querySelectorAll('dt')) as HTMLElement[];
    return dtElements.find((dt) => dt.textContent.trim().startsWith('First year'))?.textContent.trim();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterLink, LowerCasePipe],
      declarations: [
        TestParentComponent,
        AviationAccountSummaryInfoComponent,
        EtsNamePipe,
        FyroLabelPipe,
        GovukDatePipe,
      ],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(TestParentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the CORSIA FYRO label for a CORSIA account', () => {
    expect(fyroRowText()).toEqual('First year within the scope of applicability');
  });

  it('should show the UK ETS FYRO label for a UK ETS account', () => {
    component.summaryInfo = { ...component.summaryInfo, emissionTradingScheme: 'UK_ETS_AVIATION' };
    fixture.detectChanges();

    expect(fyroRowText()).toEqual('First year of reporting obligation');
  });
});
