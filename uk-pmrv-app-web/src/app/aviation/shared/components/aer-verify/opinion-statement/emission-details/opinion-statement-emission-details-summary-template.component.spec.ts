import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { VERIFICATION_REPORT } from '@aviation/request-task/aer/ukets/aer-verify/tests/mock-verification-report';
import { FUEL_TYPES } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';

import OpinionStatementEmissionDetailsSummaryTemplateComponent from './opinion-statement-emission-details-summary-template.compmonent';

describe('OpinionStatementEmissionDetailsSummaryTemplateComponent', () => {
  let component: OpinionStatementEmissionDetailsSummaryTemplateComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let element: HTMLElement;

  @Component({
    template: `
      <app-opinion-statement-emission-details-summary-template
        [fuelTypes]="fuelTypes"
        [monitoringApproachType]="monitoringApproachType"
        [isEditable]="isEditable"
        [queryParams]="queryParams"></app-opinion-statement-emission-details-summary-template>
    `,
  })
  class TestComponent {
    fuelTypes = this.getFuelTypes(VERIFICATION_REPORT.opinionStatement.fuelTypes);
    monitoringApproachType = VERIFICATION_REPORT.opinionStatement.monitoringApproachType;
    isEditable = false;
    queryParams = {};

    private getFuelTypes(
      fuelTypes: Array<'JET_KEROSENE' | 'JET_GASOLINE' | 'AVIATION_GASOLINE'>,
    ): { id: string; key: string; value: string }[] {
      return fuelTypes.map((ft) => {
        return {
          id: ft,
          key: FUEL_TYPES.find((f) => f.value === ft).summaryDescription,
          value: FUEL_TYPES.find((f) => f.value === ft).consumption,
        };
      });
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementEmissionDetailsSummaryTemplateComponent, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;

    component = fixture.debugElement.query(
      By.directive(OpinionStatementEmissionDetailsSummaryTemplateComponent),
    ).componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the emission details without being editable', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['Standard fuels and emission factors', 'Monitoring approach'],
        [
          'Jet kerosene (Jet A1 or Jet A) at 3.15 tCO2 per tonne of fuel',
          'Unmodified Eurocontrol Support Facility data',
        ],
      ],
    ]);
  });

  it('should render the emission details and be editable', () => {
    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['Standard fuels and emission factors', 'Monitoring approach'],
        [
          'Jet kerosene (Jet A1 or Jet A) at 3.15 tCO2 per tonne of fuel',
          'Change',
          'Unmodified Eurocontrol Support Facility data',
          'Change',
        ],
      ],
    ]);
  });
});
