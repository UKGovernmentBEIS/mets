import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { DoalService } from '@tasks/doal/core/doal.service';

@Component({
  selector: 'app-additional-documents-summary',
  template: `
    <app-doal-task [breadcrumb]="true">
      <app-page-heading>Upload additional documents</app-page-heading>
      <app-doal-additional-documents-summary-template
        [additionalDocuments]="additionalDocuments$ | async"
        [documents]="documentFiles$ | async"></app-doal-additional-documents-summary-template>
      <app-task-return-link [levelsUp]="2" [taskType]="taskType$ | async"></app-task-return-link>
    </app-doal-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalDocumentsSummaryComponent {
  taskType$ = this.doalService.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem?.requestTask?.type));
  additionalDocuments$ = this.doalService.payload$.pipe(map((payload) => payload.doal?.additionalDocuments));
  documentFiles$ = this.additionalDocuments$.pipe(
    map((additionalDocuments) => additionalDocuments?.documents),
    map((files) => this.doalService.getDownloadUrlFiles(files)),
  );

  constructor(private readonly doalService: DoalService) {}
}
