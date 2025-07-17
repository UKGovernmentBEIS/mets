import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AttachedFile } from '@shared/types/attached-file.type';

@Component({
  selector: 'app-opinion-statement-summary-template',
  templateUrl: './opinion-statement-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OpinionStatementSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() opinionStatementFilesText = 'Uploaded BDR verification opinion statement';
  @Input() opinionStatementFiles: AttachedFile[];
  @Input() supportingFiles: AttachedFile[];
  @Input() notes: string;
  @Input() hasBottomBorder = true;
  @Input() cssClass: string;
}
