import { ComponentFixture, TestBed } from '@angular/core/testing';
import {By} from "@angular/platform-browser";

import {of} from "rxjs";

import { AviationAerTotalEmissions } from 'pmrv-api';

import { TotalEmissionsSchemeYearSummaryComponent } from './total-emissions-scheme-year-summary.component';

describe('TotalEmissionsSchemeYearSummaryComponent', () => {
  let component: TotalEmissionsSchemeYearSummaryComponent;
  let fixture: ComponentFixture<TotalEmissionsSchemeYearSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TotalEmissionsSchemeYearSummaryComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TotalEmissionsSchemeYearSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct values when totalEmissions$ is set', () => {
    const mockEmissions: AviationAerTotalEmissions = {
      numFlightsCoveredByUkEts: 1234,
      standardFuelEmissions: '1000',
      reductionClaimEmissions: '200',
      totalEmissions: '800',
    };
    component.totalEmissions$ = of(mockEmissions);
    fixture.detectChanges();

    const numFlightsElem = fixture.debugElement.query(By.css('.emissions-num-flights dd')).nativeElement;
    expect(Number(numFlightsElem.textContent)).toEqual(mockEmissions.numFlightsCoveredByUkEts);

    const standardFuelsElem = fixture.debugElement.query(By.css('.emissions-standard-fuels dd')).nativeElement;
    expect(standardFuelsElem.textContent).toContain(`${mockEmissions.standardFuelEmissions} tCO2`);

    const reductionClaimsElem = fixture.debugElement.query(By.css('.emissions-reduction-claim dd')).nativeElement;
    expect(reductionClaimsElem.textContent).toContain(`${mockEmissions.reductionClaimEmissions} tCO2`);

    const totalEmissionsElem = fixture.debugElement.query(By.css('.emissions-scheme-year dd')).nativeElement;
    expect(totalEmissionsElem.textContent).toContain(`${mockEmissions.totalEmissions} tCO2`);
  });

  it('should not display the reduction claim row if reductionClaimEmissions is not set', () => {
    const mockEmissions: AviationAerTotalEmissions = {
      numFlightsCoveredByUkEts: 1234,
      standardFuelEmissions: '1000',
      totalEmissions: '800',
    };
    component.totalEmissions$ = of(mockEmissions);
    fixture.detectChanges();

    const reductionClaimsElem = fixture.debugElement.query(By.css('.emissions-reduction-claim'));
    expect(reductionClaimsElem).toBeNull();
  });
});
