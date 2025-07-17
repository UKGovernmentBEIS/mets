import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { TasksModule } from '@tasks/tasks.module';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { DocumentPreviewService } from 'pmrv-api';

import { PreviewDocumentsComponent } from './preview-documents.component';

describe('PreviewDocumentsComponent', () => {
  let component: PreviewDocumentsComponent;
  let fixture: ComponentFixture<PreviewDocumentsComponent>;
  let page: Page;

  const fakeFile = (): File => {
    const blob = new Blob([''], { type: 'text/html' });
    return blob as File;
  };
  const documentPreviewService = {
    getDocumentPreview: jest.fn().mockReturnValue(of({ fakeFile })),
  };

  class Page extends BasePage<PreviewDocumentsComponent> {
    get fileNames() {
      return this.queryAll<HTMLElement>('.govuk-link').map((element) => element.textContent.trim());
    }
    get files() {
      return this.queryAll<HTMLElement>('.govuk-link');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(PreviewDocumentsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.taskId = 1;
    component.decisionNotification = {
      operators: [],
      externalContacts: [],
      signatory: 'f616b1a9-e30a-497f-9ce4-20529ef15c8e',
    };
    component.previewDocuments = [
      {
        filename: 'letter_preview.pdf',

        documentType: 'PERMIT_VARIATION_REGULATOR_LED_APPROVED',
      },
      {
        filename: 'permit_preview.pdf',
        documentType: 'PERMIT',
      },
    ];

    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, TasksModule, SharedModule],
      declarations: [PreviewDocumentsComponent],
      providers: [
        KeycloakService,
        { provide: DocumentPreviewService, useValue: documentPreviewService },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    createComponent();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show files for installation regulator led variation', () => {
    expect(page.fileNames).toEqual(['letter_preview.pdf', 'permit_preview.pdf']);
  });

  it('should download', () => {
    const documentPreviewServiceSpy = jest.spyOn(documentPreviewService, 'getDocumentPreview');
    page.files[0].click();
    fixture.detectChanges();

    expect(documentPreviewServiceSpy).toHaveBeenCalled();
  });
});
