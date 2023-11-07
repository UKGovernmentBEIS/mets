import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { DoalProceedToAuthorityDetermination } from 'pmrv-api';

@Component({
  selector: 'app-doal-determination-proceed-authority-summary-template',
  templateUrl: './determination-proceed-authority-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeterminationProceedAuthoritySummaryTemplateComponent {
  @Input() determination: DoalProceedToAuthorityDetermination;
  @Input() editable: boolean;
}
