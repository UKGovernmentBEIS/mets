import { Component } from '@angular/core';

import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-monitoring-plan-changes-form',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule],
  templateUrl: './monitoring-plan-changes-form.component.html',
  viewProviders: [existingControlContainer],
})
export class MonitoringPlanChangesFormComponent {}
