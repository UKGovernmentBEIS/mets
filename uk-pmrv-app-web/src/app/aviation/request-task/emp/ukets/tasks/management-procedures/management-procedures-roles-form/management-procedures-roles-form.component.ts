import { NgFor, NgIf } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { FormArray } from '@angular/forms';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';

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

  formProvider = inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER);
  form = this.formProvider.form;

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
