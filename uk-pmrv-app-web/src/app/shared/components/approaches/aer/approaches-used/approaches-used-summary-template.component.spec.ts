import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { ApproachesUsedSummaryTemplateComponent } from '@shared/components/approaches/aer/approaches-used/approaches-used-summary-template.component';
import { SharedModule } from '@shared/shared.module';

import { AerMonitoringApproachEmissions } from 'pmrv-api';

describe('ApproachesUsedSummaryTemplateComponent', () => {
  let component: ApproachesUsedSummaryTemplateComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-approaches-used-summary-template
        [monitoringApproaches]="monitoringApproaches"></app-approaches-used-summary-template>
    `,
  })
  class TestComponent {
    monitoringApproaches: { [key: string]: AerMonitoringApproachEmissions } = {
      CALCULATION_CO2: null,
      MEASUREMENT_CO2: null,
      MEASUREMENT_N2O: null,
      CALCULATION_PFC: null,
      INHERENT_CO2: null,
      TRANSFERRED_CO2_N2O: null,
    };
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    component = fixture.debugElement.query(By.directive(ApproachesUsedSummaryTemplateComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the summary', () => {
    expect(
      Array.from(element.querySelectorAll('dl')).map((el) => [
        el.querySelector('dt').textContent.trim(),
        Array.from(el.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      ]),
    ).toEqual([
      [
        'Approaches used',
        [
          `Calculation of CO2  Measurement of CO2  Measurement of nitrous oxide (N2O)  Calculation of perfluorocarbons (PFC)  Inherent CO2 emissions  Procedures for transferred CO2 or N2O`,
        ],
      ],
    ]);
  });
});
