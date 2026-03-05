import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AttachedFile } from '@shared/types/attached-file.type';

import { DoalAdditionalDocuments } from 'pmrv-api';

@Component({
  selector: 'app-doal-additional-documents-summary-template',
  templateUrl: './additional-documents-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalDocumentsSummaryTemplateComponent {
  @Input() additionalDocuments: DoalAdditionalDocuments;
  @Input() editable: boolean;
  @Input() documents: AttachedFile[];
}
