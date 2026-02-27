import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { BDRS2 } from 'pmrv-api';

@Component({
  selector: 'app-bdrs2-baseline-summary-template',
  imports: [SharedModule, RouterLink],
  standalone: true,
  templateUrl: './baseline-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BDRS2BaselineSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() data: BDRS2;

  @Input() bdrFile: AttachedFile;
  @Input() files: AttachedFile[];
  @Input() mmpFile: AttachedFile;
  @Input() mmpFiles: AttachedFile[];
  @Input() hasBottomBorder = true;
  @Input() cssClass: string;

  constructor() {}
}
