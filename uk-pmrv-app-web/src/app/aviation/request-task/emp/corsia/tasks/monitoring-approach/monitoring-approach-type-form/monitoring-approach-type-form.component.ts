import { Component } from '@angular/core';

import { MonitoringApproachTypeCorsiaPipe } from '@aviation/shared/pipes/monitoring-approach-type-corsia.pipe';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-monitoring-approach-type-form',
  templateUrl: './monitoring-approach-type-form.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, MonitoringApproachTypeCorsiaPipe],
  viewProviders: [existingControlContainer],
})
export class MonitoringApproachTypeFormComponent {}
