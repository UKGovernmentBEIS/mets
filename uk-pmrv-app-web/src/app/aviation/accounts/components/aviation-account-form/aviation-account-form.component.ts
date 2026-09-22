import { Component, Input } from '@angular/core';
import { ControlContainer } from '@angular/forms';

import { getFyroLabel } from '@aviation/accounts/utils/fyro.util';
import { existingControlContainer } from '@shared/providers/control-container.factory';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-aviation-account-form',
  standalone: false,
  templateUrl: './aviation-account-form.component.html',
  viewProviders: [existingControlContainer],
})
export class AviationAccountFormComponent {
  @Input() withEmissionTradingScheme = true;
  @Input() withLocation = false;
  @Input() editModeEnabled = false;

  constructor(private readonly controlContainer: ControlContainer) {}

  get fyroLabel(): string {
    return getFyroLabel(this.controlContainer.control?.get('emissionTradingScheme')?.value);
  }
}
