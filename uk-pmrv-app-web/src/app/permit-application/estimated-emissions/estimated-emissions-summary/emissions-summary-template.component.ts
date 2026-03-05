import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-emissions-summary-template',
  templateUrl: './emissions-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsSummaryTemplateComponent {
  @Input() hasBorders = true;
  @Input() isPreview: boolean;
  @Input() showOriginal = false;

  constructor(readonly store: PermitApplicationStore<PermitApplicationState>) {}
}
