import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { existingControlContainer } from '@shared/providers/control-container.factory';

import { GovukComponentsModule } from 'govuk-components';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-procedure-form',
  standalone: true,
  imports: [GovukComponentsModule, ReactiveFormsModule],
  templateUrl: './procedure-form.component.html',
  viewProviders: [existingControlContainer],
})
export class ProcedureFormComponent {}
