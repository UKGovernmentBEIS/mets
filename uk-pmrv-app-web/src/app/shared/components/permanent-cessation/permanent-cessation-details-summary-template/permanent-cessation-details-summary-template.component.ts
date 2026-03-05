import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { PermanentCessation } from 'pmrv-api';

@Component({
  selector: 'app-permanent-cessation-details-summary-template',
  standalone: true,
  imports: [SharedModule, RouterLink],
  templateUrl: './permanent-cessation-details-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermanentCessationDetailsSummaryTemplateComponent {
  @Input() isEditable: boolean;
  @Input() data: PermanentCessation;
  @Input() files: AttachedFile[];
  @Input() cssClass: string;
  @Input() hasBottomBorder = true;
}
