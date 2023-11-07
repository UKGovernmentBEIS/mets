import { NgFor, NgIf } from '@angular/common';
import { Component } from '@angular/core';

import { DeterminationReasonTypePipe } from '@aviation/shared/pipes/determination-reason-type.pipe';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-aviation-emissions-reasons-form',
  templateUrl: './aviation-emissions-reasons-form.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, NgIf, NgFor, DeterminationReasonTypePipe],
  viewProviders: [existingControlContainer],
})
export class AviationEmissionsReasonsFormComponent {}
