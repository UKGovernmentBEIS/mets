import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

@Component({
  selector: 'app-waste-qdr-summary-template',
  imports: [SharedModule, RouterLink],
  templateUrl: './summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() quartelyTitle: string;
  @Input() qdrReport: AttachedFile;
  @Input() reportProvided: boolean;
  @Input() notes: string;
  @Input() reasonForUnprovided: string;
  @Input() supportingFiles: AttachedFile[];
  @Input() hasBottomBorder = true;
  @Input() cssClass: string;
  @Input() changeLink = ['..'];

  constructor() {}
}
