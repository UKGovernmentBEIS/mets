import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { Dre } from 'pmrv-api';

import { MonitoringApproachesSummaryTemplateComponent } from './monitoring-approaches-summary-template.component';

describe('MonitoringApproachesSummaryTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;
  let hostComponent: TestComponent;

  @Component({
    template: `
      <app-monitoring-approaches-summary-template
        [approachEmissions]="dre.monitoringApproachReportingEmissions"
        [editable]="editable"></app-monitoring-approaches-summary-template>
    `,
  })
  class TestComponent {
    editable = false;
    dre: Dre = {
      determinationReason: {
        type: 'VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER',
        operatorAskedToResubmit: true,
        regulatorComments: 'dfdf',
        supportingDocuments: ['2b587c89-1973-42ba-9682-b3ea5453b9dd'],
      },
      officialNoticeReason: '',
      monitoringApproachReportingEmissions: {
        FALLBACK: {
          type: 'FALLBACK',
          emissions: {
            reportableEmissions: 1,
            sustainableBiomass: 2,
          },
        } as any,
      },
      informationSources: [],
      fee: {},
    };
  }

  class Page extends BasePage<TestComponent> {
    get values() {
      return this.queryAll<HTMLElement>(
        'app-monitoring-approaches-summary-template .govuk-summary-list .govuk-summary-list__value',
      );
    }

    get actions() {
      return this.queryAll<HTMLElement>(
        'app-monitoring-approaches-summary-template .govuk-summary-list .govuk-summary-list__actions',
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, MonitoringApproachesSummaryTemplateComponent],
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display dre', () => {
    expect(page.values.map((el) => el.textContent.trim())).toEqual(['Fallback approach']);

    expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(0);
  });

  it('should display Change links', () => {
    hostComponent.editable = true;
    fixture.detectChanges();

    expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(1);
  });
});
