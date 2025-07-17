import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { MonitoringPlanSummaryTemplateComponent } from '@shared/components/monitoring-plan/monitoring-plan-summary-template.component';
import { SharedModule } from '@shared/shared.module';
import { mockVerificationApplyPayload } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';

import { AerMonitoringPlanDeviation, OpinionStatement } from 'pmrv-api';

describe('MonitoringPlanSummaryTemplateComponent', () => {
  let component: MonitoringPlanSummaryTemplateComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-monitoring-plan-summary-template
        [planDeviation]="planDeviation"
        [headingSmall]="headingSmall"
        [opinionStatement]="opinionStatement"></app-monitoring-plan-summary-template>
    `,
  })
  class TestComponent {
    planDeviation: AerMonitoringPlanDeviation = {
      existChangesNotCoveredInApprovedVariations: true,
      details: 'There was a data gap for a portion of our natural gas consumption between 16th - 20th September 2021',
    };
    opinionStatement: OpinionStatement = mockVerificationApplyPayload.verificationReport.opinionStatement;
    headingSmall = true;
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
    component = fixture.debugElement.query(By.directive(MonitoringPlanSummaryTemplateComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the summary', () => {
    expect(element.querySelector('h3').textContent.trim()).toEqual(
      'Changes not covered in the approved monitoring plans',
    );
    expect(
      Array.from(element.querySelectorAll('dl>div')).map((el) => [
        el.querySelector('dt').textContent.trim(),
        el.querySelector('dd').textContent.trim(),
      ]),
    ).toEqual([
      [
        'Changes reported by the operator',
        'There was a data gap for a portion of our natural gas consumption between 16th - 20th September 2021',
      ],
      ['Changes reported by the verifier', 'Some changes by the verifier'],
    ]);
  });

  it('should render h2 when headingSmall is false', () => {
    fixture.componentInstance.headingSmall = false;
    fixture.detectChanges();
    expect(element.querySelector('h2').textContent.trim()).toEqual(
      'Changes not covered in the approved monitoring plans',
    );
  });
});
