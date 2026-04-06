import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { ComplianceMonitoringGroupComponent } from '@shared/components/review-groups/compliance-monitoring-group/compliance-monitoring-group.component';
import { SharedModule } from '@shared/shared.module';

import { ComplianceMonitoringReporting } from 'pmrv-api';

describe('ComplianceMonitoringGroupComponent', () => {
  let component: ComplianceMonitoringGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    standalone: false,
    template: `
      <app-compliance-monitoring-group
        [isEditable]="isEditable"
        [compliance]="compliance"></app-compliance-monitoring-group>
    `,
  })
  class TestComponent {
    isEditable = false;
    compliance = {
      accuracy: false,
      accuracyReason: 'No integrity reason',
      completeness: true,
      consistency: true,
      comparability: true,
      transparency: true,
      integrity: true,
    } as ComplianceMonitoringReporting;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
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
        [`Not compliant  No integrity reason`, `Compliant`, `Compliant`, `Compliant`, `Compliant`, `Compliant`],
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
          `Not compliant  No integrity reason`,
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
