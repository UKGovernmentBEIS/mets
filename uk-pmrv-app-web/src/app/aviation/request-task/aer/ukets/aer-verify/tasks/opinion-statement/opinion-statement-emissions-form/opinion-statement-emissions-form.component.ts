import { NgIf } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { existingControlContainer } from '@shared/providers/control-container.factory';

import { GovukComponentsModule } from 'govuk-components';

import { OpinionStatementFormProvider } from '../opinion-statement-form.provider';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-opinion-statement-emissions-form',
  templateUrl: './opinion-statement-emissions-form.component.html',
  standalone: true,
  imports: [GovukComponentsModule, ReactiveFormsModule, NgIf],
  viewProviders: [existingControlContainer],
})
export default class OpinionStatementEmissionsFormComponent {
  @Input() totalEmissionsProvided: string;

  protected emissionsCorrectCtrl = inject<OpinionStatementFormProvider>(TASK_FORM_PROVIDER).emissionsCorrectCtrl;
}
