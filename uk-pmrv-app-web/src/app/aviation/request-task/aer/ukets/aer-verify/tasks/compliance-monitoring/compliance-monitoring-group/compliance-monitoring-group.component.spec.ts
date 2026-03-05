import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { AviationAerComplianceMonitoringReportingRules } from 'pmrv-api';

import { ComplianceMonitoringGroupComponent } from './compliance-monitoring-group.component';

describe('ComplianceMonitoringGroupComponent', () => {
  let component: ComplianceMonitoringGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-compliance-monitoring-group-template
        [isEditable]="isEditable"
        [compliance]="compliance"></app-compliance-monitoring-group-template>
    `,
  })
  class TestComponent {
    isEditable = false;
    compliance = {
      accuracyCompliant: false,
      accuracyNonCompliantReason: 'No accuracy reason',
      completenessCompliant: true,
      consistencyCompliant: true,
      comparabilityCompliant: true,
      transparencyCompliant: true,
      integrityCompliant: true,
    } as AviationAerComplianceMonitoringReportingRules;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, ComplianceMonitoringGroupComponent],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(ComplianceMonitoringGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        [
          'Accuracy',
          'Completeness',
          'Consistency',
          'Comparability over time',
          'Transparency',
          'Integrity of methodology',
        ],
        [`Not compliant  No accuracy reason`, `Compliant`, `Compliant`, `Compliant`, `Compliant`, `Compliant`],
      ],
    ]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        [
          'Accuracy',
          'Completeness',
          'Consistency',
          'Comparability over time',
          'Transparency',
          'Integrity of methodology',
        ],
        [
          `Not compliant  No accuracy reason`,
          'Change',
          `Compliant`,
          'Change',
          `Compliant`,
          'Change',
          `Compliant`,
          'Change',
          `Compliant`,
          'Change',
          `Compliant`,
          'Change',
        ],
      ],
    ]);
  });
});
