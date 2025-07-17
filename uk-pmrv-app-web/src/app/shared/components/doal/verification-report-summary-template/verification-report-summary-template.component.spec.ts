import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { BasePage } from '@testing';

import { VerificationReportOfTheActivityLevelReport } from 'pmrv-api';

import { VerificationReportSummaryTemplateComponent } from './verification-report-summary-template.component';

describe('VerificationReportSummaryTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-verification-report-summary-template
        [verificationActivityLevelReport]="verificationActivityLevelReport"
        [document]="document"
        [editable]="editable"></app-verification-report-summary-template>
    `,
  })
  class TestComponent {
    editable = true;
    verificationActivityLevelReport: VerificationReportOfTheActivityLevelReport = {
      document: 'abf68262-f6d1-4137-b654-c3302079d023',
      comment: 'verificationReportOfTheActivityLevelReportComment',
    };
    document: AttachedFile = {
      downloadUrl: 'url',
      fileName: 'doc.pdf',
    };
  }

  class Page extends BasePage<TestComponent> {
    get values() {
      return this.queryAll<HTMLElement>(
        'app-verification-report-summary-template .govuk-summary-list .govuk-summary-list__value',
      );
    }

    get actions() {
      return this.queryAll<HTMLElement>(
        'app-verification-report-summary-template .govuk-summary-list .govuk-summary-list__actions',
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, VerificationReportSummaryTemplateComponent],
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

  it('should display verificationActivityLevelReport', () => {
    expect(page.values.map((el) => el.textContent.trim())).toEqual([
      'doc.pdf',
      'verificationReportOfTheActivityLevelReportComment',
    ]);
  });

  it('should display change links', () => {
    expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(2);
  });
});
