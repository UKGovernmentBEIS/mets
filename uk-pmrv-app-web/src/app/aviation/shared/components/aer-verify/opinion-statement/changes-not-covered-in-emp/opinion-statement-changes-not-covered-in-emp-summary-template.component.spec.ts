import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { AviationAerMonitoringPlanChanges } from 'pmrv-api';

import OpinionStatementChangesNotCoveredInEMPSummaryTemplateComponent from './opinion-statement-changes-not-covered-in-emp-summary-template.component';

describe('OpinionStatementChangesNotCoveredInEMPSummaryTemplateComponent', () => {
  let component: OpinionStatementChangesNotCoveredInEMPSummaryTemplateComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let element: HTMLElement;

  @Component({
    template: `
      <app-opinion-statement-changes-not-covered-in-emp-summary-template
        [aerMonitoringPlanChanges]="aerMonitoringPlanChanges"
        [additionalChangesNotCovered]="additionalChangesNotCovered"
        [additionalChangesNotCoveredDetails]="additionalChangesNotCoveredDetails"
        [isEditable]="isEditable"
        [queryParams]="queryParams"></app-opinion-statement-changes-not-covered-in-emp-summary-template>
    `,
  })
  class TestComponent {
    aerMonitoringPlanChanges: AviationAerMonitoringPlanChanges = {
      notCoveredChangesExist: true,
      details: 'details',
    };

    additionalChangesNotCovered = false;
    additionalChangesNotCoveredDetails = null;

    isEditable = false;
    queryParams = {};
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionStatementChangesNotCoveredInEMPSummaryTemplateComponent, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;

    component = fixture.debugElement.query(
      By.directive(OpinionStatementChangesNotCoveredInEMPSummaryTemplateComponent),
    ).componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the changes not covered in emp without being editable', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [['Changes reported by the operator'], ['details']],
      [['Changes reported by the verifier'], ['None']],
    ]);
  });

  it('should render the changes not covered in emp and be editable', () => {
    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [['Changes reported by the operator'], ['details']],
      [['Changes reported by the verifier'], ['None', 'Change']],
    ]);
  });
});
