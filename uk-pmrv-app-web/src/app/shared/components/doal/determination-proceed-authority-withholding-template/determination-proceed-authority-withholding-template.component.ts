import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { DoalProceedToAuthorityDetermination } from 'pmrv-api';

@Component({
  selector: 'app-determination-proceed-authority-withholding-template',
  templateUrl: './determination-proceed-authority-withholding-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeterminationProceedAuthorityWithholdingTemplateComponent {
  @Input() determination: DoalProceedToAuthorityDetermination;
  @Input() editable: boolean;
}
