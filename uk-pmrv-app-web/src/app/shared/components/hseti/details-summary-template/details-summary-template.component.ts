import { ChangeDetectionStrategy, Component, computed, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { UserState } from '@core/store';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { HSETI, HSETIRegulatorReviewDecision } from 'pmrv-api';

@Component({
  selector: 'app-hseti-details-summary-template',
  imports: [SharedModule, RouterLink],
  templateUrl: './details-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsSummaryTemplateComponent {
  @Input() isEditable = false;
  @Input() data: HSETI;
  @Input() regulatorData: HSETIRegulatorReviewDecision;
  @Input() hsetiFile: AttachedFile;
  @Input() files: AttachedFile[];
  @Input() hasBottomBorder = true;
  @Input() cssClass: string;
  @Input() roleType: UserState['roleType'];

  isRegulator = computed(() => this.roleType === 'REGULATOR' && !!this.regulatorData);

  constructor() {}
}
