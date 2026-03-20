import { Component } from '@angular/core';

import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-application-timeframe-apply-form',
  imports: [GovukComponentsModule, SharedModule],
  templateUrl: './application-timeframe-apply-form.component.html',
  viewProviders: [existingControlContainer],
})
export class ApplicationTimeframeApplyFormComponent {}
