import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { BasePage } from '@testing';

import { DoalAdditionalDocuments } from 'pmrv-api';

import { AdditionalDocumentsSummaryTemplateComponent } from './additional-documents-summary-template.component';

describe('AdditionalDocumentsSummaryTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-doal-additional-documents-summary-template
        [additionalDocuments]="additionalDocuments"
        [documents]="documents"
        [editable]="editable"></app-doal-additional-documents-summary-template>
    `,
  })
  class TestComponent {
    editable = true;
    additionalDocuments: DoalAdditionalDocuments = {
      exist: true,
      documents: ['7e2036b4-c857-4caa-afef-97e690df3454', '2b587c89-1973-42ba-9682-b3ea5453b9dd'],
      comment: 'additionalDocumentsComment',
    };
    documents: AttachedFile[] = [
      {
        downloadUrl: 'url1',
        fileName: 'doc1.pdf',
      },
      {
        downloadUrl: 'url2',
        fileName: 'doc2.pdf',
      },
    ];
  }

  class Page extends BasePage<TestComponent> {
    get values() {
      return this.queryAll<HTMLElement>('.govuk-summary-list .govuk-summary-list__value');
    }

    get actions() {
      return this.queryAll<HTMLElement>('.govuk-summary-list .govuk-summary-list__actions');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, AdditionalDocumentsSummaryTemplateComponent],
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

  it('should display data', () => {
    expect(page.values.map((el) => el.textContent.trim())).toEqual([
      'Yes',
      'doc1.pdf  doc2.pdf',
      'additionalDocumentsComment',
    ]);
  });

  it('should display change links', () => {
    expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(3);
  });
});
