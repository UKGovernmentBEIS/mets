import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { map, Observable } from 'rxjs';

import { requestTaskQuery } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

import { GovukComponentsModule } from 'govuk-components';

import { BaseDoeCorsiaEmissionsComponent } from '../doe-corsia-base.component';

export interface SupportingDocumentsViewModel {
  form: FormGroup;
  submitHidden: boolean;
  downloadUrl: string;
}

@Component({
  selector: 'app-doe-estimated-emissions',
  templateUrl: './doe-estimated-emissions.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, NgIf, NgFor, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class EstimatedEmissionsComponent extends BaseDoeCorsiaEmissionsComponent {
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;

  form = this.formProvider.emissionsCtrl;

  vm$: Observable<SupportingDocumentsViewModel> = this.store.pipe(requestTaskQuery.selectIsEditable).pipe(
    map((isEditable) => {
      return {
        form: this.form,
        downloadUrl: `${this.store.doeDelegate.baseFileAttachmentDownloadUrl}/`,
        submitHidden: !isEditable,
      };
    }),
  );

  onSubmit() {
    this.saveDoeAndNavigate(this.formProvider.getFormValue(), 'in progress', '../emission-charges', false);
  }
}
