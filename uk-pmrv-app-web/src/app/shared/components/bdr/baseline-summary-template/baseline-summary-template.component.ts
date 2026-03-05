import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { BDR } from 'pmrv-api';

@Component({
  selector: 'app-bdr-baseline-summary-template',
  templateUrl: './baseline-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() data: BDR;

  @Input() bdrFile: AttachedFile;
  @Input() files: AttachedFile[];
  @Input() mmpFiles: AttachedFile[];
  @Input() hasBottomBorder = true;
  @Input() cssClass: string;

  constructor() {}
}
