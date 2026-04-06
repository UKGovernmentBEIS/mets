import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { NER } from 'pmrv-api';

@Component({
  selector: 'app-ner-details-summary-template',
  imports: [SharedModule, RouterLink],
  templateUrl: './details-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerDetailsSummaryTemplateComponent {
  @Input() isEditable: boolean;
  @Input() ner: NER;
  @Input() nerFile: AttachedFile;
  @Input() nerSupportingFiles: Array<AttachedFile>;
  @Input() mmpFile: AttachedFile;
  @Input() mmpSupportingFiles: Array<AttachedFile>;
}
