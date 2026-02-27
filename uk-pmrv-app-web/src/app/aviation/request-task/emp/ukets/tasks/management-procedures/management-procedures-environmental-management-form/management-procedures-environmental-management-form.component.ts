import { Component } from '@angular/core';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-management-procedures-environmental-management-form',
  imports: [GovukComponentsModule, SharedModule],
  templateUrl: './management-procedures-environmental-management-form.component.html',
  providers: [DestroySubject],
  viewProviders: [existingControlContainer],
})
export class ManagementProceduresEnvironmentalManagementFormComponent {}
