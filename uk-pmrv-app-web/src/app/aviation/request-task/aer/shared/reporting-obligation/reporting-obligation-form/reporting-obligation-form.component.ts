import { NgFor, NgIf } from '@angular/common';
import { Component, inject, Input } from '@angular/core';

import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { ReportingObligationFormProvider } from '../reporting-obligation-form.provider';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-reporting-obligation-form',
  templateUrl: './reporting-obligation-form.component.html',
  imports: [GovukComponentsModule, NgIf, NgFor, SharedModule],
  standalone: true,
  viewProviders: [existingControlContainer],
})
export class ReportingObligationFormComponent {
  @Input() year: number;
  @Input() downloadUrl: string;

  private formProvider = inject<ReportingObligationFormProvider>(TASK_FORM_PROVIDER);

  reportingRequiredCtrl = this.formProvider.reportingRequiredCtrl;
}
