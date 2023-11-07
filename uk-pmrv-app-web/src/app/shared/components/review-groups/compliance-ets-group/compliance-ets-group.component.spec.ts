import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { EtsComplianceRules } from 'pmrv-api';

import { ComplianceEtsGroupComponent } from './compliance-ets-group.component';

describe('ComplianceEtsGroupComponent', () => {
  let component: ComplianceEtsGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-compliance-ets-group
        [isEditable]="isEditable"
        [etsComplianceRules]="etsComplianceRules"
      ></app-compliance-ets-group>
    `,
  })
  class TestComponent {
    isEditable = false;
    etsComplianceRules = {
      competentAuthorityGuidanceMet: false,
      competentAuthorityGuidanceNotMetReason: 'No competentAuthorityGuidanceNotMetReason',
      controlActivitiesDocumented: false,
      controlActivitiesNotDocumentedReason: 'No controlActivitiesNotDocumentedReason',
      dataVerificationCompleted: false,
      dataVerificationNotCompletedReason: 'No dataVerificationNotCompletedReason',
      detailSourceDataNotVerifiedReason: 'No detailSourceDataNotVerifiedReason',
      detailSourceDataVerified: false,
      euRegulationMonitoringReportingMet: false,
      euRegulationMonitoringReportingNotMetReason: 'No euRegulationMonitoringReportingNotMetReason',
      methodsApplyingMissingDataNotUsedReason: 'No methodsApplyingMissingDataNotUsedReason',
      methodsApplyingMissingDataUsed: false,
      monitoringApproachAppliedCorrectly: false,
      monitoringApproachNotAppliedCorrectlyReason: 'No monitoringApproachNotAppliedCorrectlyReason',
      monitoringPlanRequirementsMet: false,
      monitoringPlanRequirementsNotMetReason: 'No monitoringPlanRequirementsNotMetReason',
      nonConformities: 'NOT_APPLICABLE',
      partOfSiteVerification: 'Yes partOfSiteVerification',
      plannedActualChangesNotReportedReason: 'No plannedActualChangesNotReportedReason',
      plannedActualChangesReported: false,
      proceduresMonitoringPlanDocumented: false,
      proceduresMonitoringPlanNotDocumentedReason: 'No proceduresMonitoringPlanNotDocumentedReason',
      uncertaintyAssessment: false,
      uncertaintyAssessmentNotUsedReason: 'No uncertaintyAssessmentNotUsedReason',
    } as EtsComplianceRules;
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
    component = fixture.debugElement.query(By.directive(ComplianceEtsGroupComponent)).componentInstance;
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
          'Monitoring Plan requirements met?',
          'EU Regulation on Monitoring and reporting met?',
          'Data verified in detail and back to source',
          'Control activities are documented, implemented, maintained and effective to mitigate the inherent risks?',
          'Procedures listed in monitoring plan are documented, implemented, maintained and effective to mitigate the inherent risks and control risks?',
          'Data verification completed as required?',
          'Correct application of monitoring methodology?',
          'Reporting of planned or actual changes?',
          'Verification of methods applied for missing data?',
          'Does the operator need to comply with uncertainty thresholds?',
          'Competent authority guidance on monitoring and reporting met?',
          'Previous year Non-Conformities corrected?',
        ],
        [
          'NoNo monitoringPlanRequirementsNotMetReason',
          'NoNo euRegulationMonitoringReportingNotMetReason',
          'NoNo detailSourceDataNotVerifiedReason',
          'NoNo controlActivitiesNotDocumentedReason',
          'NoNo proceduresMonitoringPlanNotDocumentedReason',
          'NoNo dataVerificationNotCompletedReason',
          'NoNo monitoringApproachNotAppliedCorrectlyReason',
          'NoNo plannedActualChangesNotReportedReason',
          'NoNo methodsApplyingMissingDataNotUsedReason',
          'NoNo uncertaintyAssessmentNotUsedReason',
          'NoNo competentAuthorityGuidanceNotMetReason',
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
          'Monitoring Plan requirements met?',
          'EU Regulation on Monitoring and reporting met?',
          'Data verified in detail and back to source',
          'Control activities are documented, implemented, maintained and effective to mitigate the inherent risks?',
          'Procedures listed in monitoring plan are documented, implemented, maintained and effective to mitigate the inherent risks and control risks?',
          'Data verification completed as required?',
          'Correct application of monitoring methodology?',
          'Reporting of planned or actual changes?',
          'Verification of methods applied for missing data?',
          'Does the operator need to comply with uncertainty thresholds?',
          'Competent authority guidance on monitoring and reporting met?',
          'Previous year Non-Conformities corrected?',
        ],
        [
          'NoNo monitoringPlanRequirementsNotMetReason',
          'Change',
          'NoNo euRegulationMonitoringReportingNotMetReason',
          'Change',
          'NoNo detailSourceDataNotVerifiedReason',
          'Change',
          'NoNo controlActivitiesNotDocumentedReason',
          'Change',
          'NoNo proceduresMonitoringPlanNotDocumentedReason',
          'Change',
          'NoNo dataVerificationNotCompletedReason',
          'Change',
          'NoNo monitoringApproachNotAppliedCorrectlyReason',
          'Change',
          'NoNo plannedActualChangesNotReportedReason',
          'Change',
          'NoNo methodsApplyingMissingDataNotUsedReason',
          'Change',
          'NoNo uncertaintyAssessmentNotUsedReason',
          'Change',
          'NoNo competentAuthorityGuidanceNotMetReason',
          'Change',
          'Not applicable',
          'Change',
        ],
      ],
    ]);
  });
});
