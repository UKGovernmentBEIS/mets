import { Component, inject, Input } from '@angular/core';

import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { ConfidentialityFormProvider } from '../confidentiality-form.provider';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-confidentiality-form',
  imports: [SharedModule],
  templateUrl: './confidentiality-form.component.html',
  viewProviders: [existingControlContainer],
})
export class ConfidentialityFormComponent {
  @Input() downloadUrl: string;

  private formProvider = inject<ConfidentialityFormProvider>(TASK_FORM_PROVIDER);

  totalEmissionsPublishedCtrl = this.formProvider.totalEmissionsPublishedCtrl;
  aggregatedStatePairDataPublishedCtrl = this.formProvider.aggregatedStatePairDataPublishedCtrl;
}
