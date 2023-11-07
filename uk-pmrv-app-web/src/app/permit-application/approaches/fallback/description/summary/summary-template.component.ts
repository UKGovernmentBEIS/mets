import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

@Component({
  selector: 'app-fallback-description-summary-template',
  templateUrl: './summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryTemplateComponent {
  @Input() showOriginal = false;
  @Input() hasBottomBorder = true;

  constructor(readonly store: PermitApplicationStore<PermitApplicationState>) {}
}
