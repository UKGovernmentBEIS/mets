import { Component } from '@angular/core';

import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-monitoring-approach-total-emissions-form',
  templateUrl: './monitoring-approach-total-emissions-form.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule],
  viewProviders: [existingControlContainer],
})
export class MonitoringApproachTotalEmissionsFormComponent {}
