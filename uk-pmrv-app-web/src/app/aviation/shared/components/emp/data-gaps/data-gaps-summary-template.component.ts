import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { EmpDataGaps } from 'pmrv-api';

@Component({
  selector: 'app-data-gaps-summary-template',
  templateUrl: './data-gaps-summary-template.component.html',
  standalone: true,
  imports: [CommonModule, RouterModule, SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataGapsSummaryTemplateComponent {
  @Input() data: EmpDataGaps | null;
  @Input() isEditable = false;
}
