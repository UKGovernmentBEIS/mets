import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { VERIFICATION_REPORT } from '@aviation/request-task/aer/ukets/aer-verify/tests/mock-verification-report';

import OpinionStatementTotalEmissionsSummaryTemplateComponent from './opinion-statement-total-emissions-summary-template.component';

describe('OpinionStatementTotalEmissionsSummaryTemplateComponent', () => {
  let component: OpinionStatementTotalEmissionsSummaryTemplateComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let element: HTMLElement;

  @Component({
    template: `
      <app-opinion-statement-total-emissions-summary-template
        [totalEmissionsProvided]="totalEmissionsProvided"
        [emissionsCorrect]="emissionsCorrect"
        [manuallyProvidedEmissions]="manuallyProvidedEmissions"
        [isEditable]="isEditable"
        [queryParams]="queryParams"></app-opinion-statement-total-emissions-summary-template>
    `,
  })
  class TestComponent {
    totalEmissionsProvided = '2500';
    emissionsCorrect = VERIFICATION_REPORT.opinionStatement.emissionsCorrect;
    manuallyProvidedEmissions = VERIFICATION_REPORT.opinionStatement.manuallyProvidedEmissions;
    isEditable = false;
    queryParams = {};
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementTotalEmissionsSummaryTemplateComponent, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;

    component = fixture.debugElement.query(
      By.directive(OpinionStatementTotalEmissionsSummaryTemplateComponent),
    ).componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the total emissions without being editable', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['Total emissions reported by the operator', 'Are the reported emissions correct?'],
        ['2500 tCO2', '', 'Yes'],
      ],
    ]);
  });

  it('should render the total emissions with wrong reported emissions without being editable', () => {
    hostComponent.emissionsCorrect = false;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        [
          'Total emissions reported by the operator',
          'Are the reported emissions correct?',
          'Total verified aviation emissions for the scheme year',
        ],
        ['2500 tCO2', '', 'No', 'emissions tCO2'],
      ],
    ]);
  });

  it('should render the total emissions and be editable', () => {
    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['Total emissions reported by the operator', 'Are the reported emissions correct?'],
        ['2500 tCO2', '', 'Yes', 'Change'],
      ],
    ]);
  });

  it('should render the total emissions with wrong reported emissions and be editable', () => {
    hostComponent.isEditable = true;
    hostComponent.emissionsCorrect = false;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        [
          'Total emissions reported by the operator',
          'Are the reported emissions correct?',
          'Total verified aviation emissions for the scheme year',
        ],
        ['2500 tCO2', '', 'No', 'Change', 'emissions tCO2', 'Change'],
      ],
    ]);
  });
});
