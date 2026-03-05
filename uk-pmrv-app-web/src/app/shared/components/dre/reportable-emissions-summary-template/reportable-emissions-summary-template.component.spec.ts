import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { Dre } from 'pmrv-api';

import { ReportableEmissionsSummaryTemplateComponent } from './reportable-emissions-summary-template.component';

describe('ReportableEmissionsSummaryTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-reportable-emissions-summary-template
        [approachEmissions]="dre.monitoringApproachReportingEmissions"
        [editable]="editable"></app-reportable-emissions-summary-template>
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
    get reportableEmissionsValues() {
      return this.queryAll<HTMLElement>('app-reportable-emissions-summary-template govuk-table tbody td:nth-child(2)');
    }
    get sustainableBiomassValues() {
      return this.queryAll<HTMLElement>('app-reportable-emissions-summary-template govuk-table tbody td:nth-child(3)');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, ReportableEmissionsSummaryTemplateComponent],
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display emissions values', () => {
    expect(page.reportableEmissionsValues.map((el) => el.textContent.trim())).toEqual(['1  t', '1  tCO2e']);
    expect(page.sustainableBiomassValues.map((el) => el.textContent.trim())).toEqual(['2 t', '2 tCO2e']);
  });
});
