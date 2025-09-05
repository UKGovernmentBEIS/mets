import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { ALRClosedDetermination } from 'pmrv-api';

@Component({
  selector: 'app-alr-determination-close-summary-template',
  templateUrl: './determination-close-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeterminationCloseSummaryTemplateComponent {
  @Input() determination: ALRClosedDetermination;
  @Input() editable: boolean;
  @Input() alrFile: AttachedFile;
  @Input() files: AttachedFile[];
  @Input() hasBottomBorder = true;
  @Input() cssClass: string;
}
