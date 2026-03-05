import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { BasePage } from '@testing';

import { OperatorActivityLevelReport } from 'pmrv-api';

import { OperatorReportSummaryTemplateComponent } from './operator-report-summary-template.component';

describe('OperatorReportSummaryTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-operator-report-summary-template
        [operatorActivityLevelReport]="operatorActivityLevelReport"
        [document]="document"
        [editable]="editable"></app-operator-report-summary-template>
    `,
  })
  class TestComponent {
    editable = true;
    operatorActivityLevelReport: OperatorActivityLevelReport = {
      document: '2b587c89-1973-42ba-9682-b3ea5453b9dd',
      areActivityLevelsEstimated: true,
      comment: 'operatorActivityLevelReport',
    };
    document: AttachedFile = {
      downloadUrl: 'url',
      fileName: 'doc.pdf',
    };
  }

  class Page extends BasePage<TestComponent> {
    get values() {
      return this.queryAll<HTMLElement>(
        'app-operator-report-summary-template .govuk-summary-list .govuk-summary-list__value',
      );
    }

    get actions() {
      return this.queryAll<HTMLElement>(
        'app-operator-report-summary-template .govuk-summary-list .govuk-summary-list__actions',
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, OperatorReportSummaryTemplateComponent],
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

  it('should display operatorActivityLevelReport', () => {
    expect(page.values.map((el) => el.textContent.trim())).toEqual(['doc.pdf', 'Yes', 'operatorActivityLevelReport']);
  });

  it('should display change links', () => {
    expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(3);
  });
});
