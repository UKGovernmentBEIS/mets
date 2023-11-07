import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { DoalClosedDetermination } from 'pmrv-api';

@Component({
  selector: 'app-doal-determination-close-summary-template',
  templateUrl: './determination-close-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeterminationCloseSummaryTemplateComponent {
  @Input() determination: DoalClosedDetermination;
  @Input() editable: boolean;
}
