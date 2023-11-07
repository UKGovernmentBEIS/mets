import { Component, Input } from '@angular/core';

import { existingControlContainer } from '@shared/providers/control-container.factory';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-aviation-account-form',
  templateUrl: './aviation-account-form.component.html',
  viewProviders: [existingControlContainer],
})
export class AviationAccountFormComponent {
  @Input() withEmissionTradingScheme = true;
  @Input() withRegistryId = false;
  @Input() withLocation = false;
}
