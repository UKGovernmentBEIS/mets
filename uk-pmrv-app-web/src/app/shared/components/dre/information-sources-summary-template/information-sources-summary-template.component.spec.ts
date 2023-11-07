import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { Dre } from 'pmrv-api';

import { InformationSourcesSummaryTemplateComponent } from './information-sources-summary-template.component';

describe('InformationSourcesSummaryTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `<app-information-sources-summary-template
      [data]="dre.informationSources"
      [editable]="editable"
      [baseChangeLink]="'../information-sources'"
    ></app-information-sources-summary-template>`,
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
      monitoringApproachReportingEmissions: {},
      informationSources: ['fgf'],
      fee: {},
    };
  }

  class Page extends BasePage<TestComponent> {
    get informationSourcesValues() {
      return this.queryAll<HTMLElement>(
        'app-information-sources-summary-template .govuk-summary-list .govuk-summary-list__value',
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, InformationSourcesSummaryTemplateComponent],
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

  it('should display information sources', () => {
    expect(page.informationSourcesValues.map((el) => el.textContent.trim())).toEqual(['fgf']);
  });
});
