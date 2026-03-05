import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { existingControlContainer } from '@shared/providers/control-container.factory';

import { GovukComponentsModule } from 'govuk-components';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-ets-compliance-rules-form',
  templateUrl: './ets-compliance-rules-form.component.html',
  standalone: true,
  imports: [GovukComponentsModule, ReactiveFormsModule],
  viewProviders: [existingControlContainer],
})
export class EtsComplianceRulesFormComponent {}
