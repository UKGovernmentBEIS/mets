import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

@Component({
  selector: 'app-alr-activity-summary-template',
  templateUrl: './activity-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivitySummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() alrFile: AttachedFile;
  @Input() files: AttachedFile[];
  @Input() hasBottomBorder = true;
  @Input() cssClass: string;
  @Input() changeLink = ['..'];

  constructor() {}
}
