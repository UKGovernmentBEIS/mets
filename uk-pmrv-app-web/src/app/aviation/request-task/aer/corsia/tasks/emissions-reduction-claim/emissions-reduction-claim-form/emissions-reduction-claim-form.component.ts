import { Component } from '@angular/core';

import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-emissions-reduction-claim-form',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule],
  templateUrl: './emissions-reduction-claim-form.component.html',
  viewProviders: [existingControlContainer],
})
export class EmissionsReductionClaimFormComponent {}
