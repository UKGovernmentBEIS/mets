import { Component, Input } from '@angular/core';

import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { SimplifiedApproachFormModel } from '../monitoring-approach.interfaces';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-simplified-approach-form',
  imports: [GovukComponentsModule, SharedModule],
  templateUrl: './simplified-approach-form.component.html',
  viewProviders: [existingControlContainer],
})
export class SimplifiedApproachFormComponent {
  @Input() vm: SimplifiedApproachFormModel;
}
