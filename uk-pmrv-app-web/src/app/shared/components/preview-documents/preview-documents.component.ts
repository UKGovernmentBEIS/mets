import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';

import { DecisionNotification, DocumentPreviewService } from 'pmrv-api';

@Component({
  selector: 'app-preview-documents',
  templateUrl: './preview-documents.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    div .govuk-link {
      cursor: pointer;
    }
  `,
})
export class PreviewDocumentsComponent {
  @Input() taskId: number;
  @Input() previewDocuments: DocumentFilenameAndDocumentType[];
  @Input() decisionNotification: DecisionNotification;
  @Input() linkFontSize = 'govuk-!-font-size-19';

  disabled$ = new BehaviorSubject(false);

  constructor(
    private documentPreviewService: DocumentPreviewService,
    readonly pendingRequest: PendingRequestService,
  ) {}

  getDocument(document: DocumentFilenameAndDocumentType, event) {
    event.preventDefault();
    if (!this.disabled$.getValue()) {
      this.disabled$.next(true);
      this.documentPreviewService
        .getDocumentPreview(this.taskId, {
          documentType: document.documentType,
          decisionNotification: this.decisionNotification,
        })
        .pipe(this.pendingRequest.trackRequest())
        .subscribe((blob: any) => {
          const file = new Blob([blob]);
          const fileURL = URL.createObjectURL(file);

          const a = window.document.createElement('a');
          window.document.body.appendChild(a);
          a.setAttribute('style', 'display: none');
          a.href = fileURL;
          a.download = document.filename;
          a.click();
          a.remove();
          this.disabled$.next(false);
        });
    }
  }
}
