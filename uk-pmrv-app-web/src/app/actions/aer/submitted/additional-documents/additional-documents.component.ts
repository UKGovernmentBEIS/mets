import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-additional-documents',
  template: `
    <app-action-task header="Additional documents and information" [breadcrumb]="true">
      <app-documents-summary-template
        [data]="additionalDocuments$ | async"
        [files]="additionalDocumentFiles$ | async"
      ></app-documents-summary-template>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalDocumentsComponent {
  additionalDocuments$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.aer.additionalDocuments));
  additionalDocumentFiles$ = this.additionalDocuments$.pipe(
    map((additionalDocuments) =>
      additionalDocuments.exist ? this.aerService.getDownloadUrlFiles(additionalDocuments.documents) : [],
    ),
  );

  constructor(private readonly aerService: AerService) {}
}
