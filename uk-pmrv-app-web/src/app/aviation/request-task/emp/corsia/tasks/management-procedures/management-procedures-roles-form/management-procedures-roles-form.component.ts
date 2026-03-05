import { NgFor, NgIf } from '@angular/common';
import { Component, Inject, Input } from '@angular/core';
import { FormArray } from '@angular/forms';

import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { ManagementProceduresCorsiaFormProvider } from '../management-procedures-form.provider';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-management-procedures-roles-form',
  templateUrl: './management-procedures-roles-form.component.html',
  standalone: true,
  imports: [GovukComponentsModule, NgIf, NgFor, SharedModule],
  providers: [DestroySubject],
  viewProviders: [existingControlContainer],
})
export class ManagementProceduresRolesFormComponent {
  @Input() heading: HTMLHeadingElement;

  form = this.formProvider.form;

  constructor(@Inject(TASK_FORM_PROVIDER) private formProvider: ManagementProceduresCorsiaFormProvider) {}

  addManagementProceduresRole() {
    this.formProvider.addManagementProceduresRole();
  }

  removeManagementProceduresRole(index: number) {
    this.formProvider.removeManagementProceduresRole(index);
  }

  get managementProceduresRolesCtrl(): FormArray | null {
    return (this.form.get('monitoringReportingRoles') as FormArray) ?? null;
  }
}
