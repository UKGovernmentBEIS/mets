import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { VERIFICATION_REPORT } from '@aviation/request-task/aer/ukets/aer-verify/tests/mock-verification-report';
import { SharedModule } from '@shared/shared.module';

import { EtsComplianceRulesGroupComponent } from './ets-compliance-rules-group.component';

describe('EtsComplianceRulesGroupComponent', () => {
  let component: EtsComplianceRulesGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-ets-compliance-rules-group
        [isEditable]="isEditable"
        [etsComplianceRules]="etsComplianceRules"
        [queryParams]="queryParams"
      ></app-ets-compliance-rules-group>
    `,
  })
  class TestComponent {
    isEditable = false;
    etsComplianceRules = VERIFICATION_REPORT.etsComplianceRules;
    queryParams = {};
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
    component = fixture.debugElement.query(By.directive(EtsComplianceRulesGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups without being editable', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        [
          'Emissions monitoring plan requirements and conditions met?',
          'ETS Order requirements met?',
          'Data detail and source verified?',
          'Control activities documented, implemented, maintained and effective to reduce risks?',
          'Procedures in EMP documented, implemented, maintained and effective to reduce risks?',
          'Data verification completed as required?',
          'Correct application of monitoring approach?',
          'Comparison of completeness of flights or data with air traffic data?',
          'Consistency checks made between reported data and ‘mass and balance’ documentation?',
          'Consistency checks made between aggregate fuel consumption and fuel purchase or supply data?',
          'Appropriate methods used for applying missing data?',
          'Regulator guidance on monitoring and reporting met?',
          'Corrected non-conformities from last year?',
        ],
        [
          'No  reason',
          'No  reason',
          'No  reason',
          'No  reason',
          'No  reason',
          'No  reason',
          'No  reason',
          'No  reason',
          'No  reason',
          'No  reason',
          'No  reason',
          'No  reason',
          'Not applicable',
        ],
      ],
    ]);
  });

  it('should render the review groups and be editable', () => {
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
          'Emissions monitoring plan requirements and conditions met?',
          'ETS Order requirements met?',
          'Data detail and source verified?',
          'Control activities documented, implemented, maintained and effective to reduce risks?',
          'Procedures in EMP documented, implemented, maintained and effective to reduce risks?',
          'Data verification completed as required?',
          'Correct application of monitoring approach?',
          'Comparison of completeness of flights or data with air traffic data?',
          'Consistency checks made between reported data and ‘mass and balance’ documentation?',
          'Consistency checks made between aggregate fuel consumption and fuel purchase or supply data?',
          'Appropriate methods used for applying missing data?',
          'Regulator guidance on monitoring and reporting met?',
          'Corrected non-conformities from last year?',
        ],
        [
          'No  reason',
          'Change',
          'No  reason',
          'Change',
          'No  reason',
          'Change',
          'No  reason',
          'Change',
          'No  reason',
          'Change',
          'No  reason',
          'Change',
          'No  reason',
          'Change',
          'No  reason',
          'Change',
          'No  reason',
          'Change',
          'No  reason',
          'Change',
          'No  reason',
          'Change',
          'No  reason',
          'Change',
          'Not applicable',
          'Change',
        ],
      ],
    ]);
  });
});
