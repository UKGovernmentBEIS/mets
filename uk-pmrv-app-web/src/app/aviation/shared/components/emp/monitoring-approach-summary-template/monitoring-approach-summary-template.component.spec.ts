import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SimplifiedMonitoringApproach } from '@aviation/request-task/emp/ukets/tasks/monitoring-approach/monitoring-approach-types.interface';
import { MonitoringApproachSummaryTemplateComponent } from '@aviation/shared/components/emp/monitoring-approach-summary-template/monitoring-approach-summary-template.component';

import { EmpEmissionsMonitoringApproach } from 'pmrv-api';

type EmissionsMonitoringApproachFormValues = {
  monitoringApproachType: EmpEmissionsMonitoringApproach['monitoringApproachType'];
  simplifiedApproach?: SimplifiedMonitoringApproach;
  supportingEvidenceFiles?: Array<string>;
};

describe('MonitoringApproachSummaryTemplateComponent', () => {
  let component: MonitoringApproachSummaryTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-monitoring-approach-summary-template
        [data]="data"
        [files]="files"
        [isEditable]="isEditable"></app-monitoring-approach-summary-template>
    `,
  })
  class TestComponent {
    isEditable = false;
    data = {
      simplifiedApproach: {
        explanation: 'My explanation',
      },
      monitoringApproachType: 'EUROCONTROL_SUPPORT_FACILITY',
      supportingEvidenceFiles: ['4c7478d9-ae7d-420f-968e-4647c91c841c'],
    } as EmissionsMonitoringApproachFormValues;

    files = [
      {
        downloadUrl: 'link',
        fileName: 'test.png',
      },
    ];
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonitoringApproachSummaryTemplateComponent, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(MonitoringApproachSummaryTemplateComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the monitoring approach types', () => {
    expect(summaryListValues()).toEqual([
      ['Monitoring approach', ['Use unmodified Eurocontrol Support Facility data', '']],
      ['Simplified reporting eligibility', ['My explanation', '']],
      ['Supporting evidence', ['test.png', '']],
    ]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([
      ['Monitoring approach', ['Use unmodified Eurocontrol Support Facility data', '', 'Change']],
      ['Simplified reporting eligibility', ['My explanation', '', 'Change']],
      ['Supporting evidence', ['test.png', '', 'Change']],
    ]);

    hostComponent.data = {
      simplifiedApproach: {
        explanation: 'My explanation',
      },
      monitoringApproachType: 'EUROCONTROL_SMALL_EMITTERS',
    } as EmissionsMonitoringApproachFormValues;
    hostComponent.files = [];
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([
      ['Monitoring approach', ['Use your own flight data with the Eurocontrol Small Emitters Tool', '', 'Change']],
      ['Simplified reporting eligibility', ['My explanation', '', 'Change']],
    ]);

    hostComponent.data = {
      monitoringApproachType: 'FUEL_USE_MONITORING',
      simplifiedApproach: {
        explanation: '',
      },
    } as EmissionsMonitoringApproachFormValues;
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([['Monitoring approach', ['Use fuel use monitoring', '', 'Change']]]);
  });

  function summaryListValues() {
    return Array.from(element.querySelectorAll<HTMLDivElement>('.govuk-summary-list__row')).map((row) => [
      row.querySelector('dt').textContent.trim(),
      Array.from(row.querySelectorAll('dd')).map((el) => el.textContent.trim()),
    ]);
  }
});
