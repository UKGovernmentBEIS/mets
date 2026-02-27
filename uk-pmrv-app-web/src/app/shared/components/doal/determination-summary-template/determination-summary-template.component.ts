import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { DoalDetermination } from 'pmrv-api';

@Component({
  selector: 'app-determination-summary-template',
  standalone: false,
  templateUrl: './determination-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeterminationSummaryTemplateComponent {
  @Input() determination: DoalDetermination;
  @Input() editable: boolean;
}
