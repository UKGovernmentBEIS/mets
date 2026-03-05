import { Component } from '@angular/core';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-management-procedures-environmental-management-form',
  templateUrl: './management-procedures-environmental-management-form.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule],
  providers: [DestroySubject],
  viewProviders: [existingControlContainer],
})
export class ManagementProceduresEnvironmentalManagementFormComponent {}
