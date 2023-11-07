import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { requestTaskQuery } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { SupportingDocumentsViewModel } from '../aviation-emissions-form.provider';
import { AviationEmissionsFormComponent } from '../aviation-emissions-form/aviation-emissions-form.component';
import { BaseAviationEmissionsComponent } from '../base-aviation-emissions.component';

@Component({
  selector: 'app-aviation-emissions',
  templateUrl: './aviation-emissions.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, NgIf, NgFor, ReturnToLinkComponent, AviationEmissionsFormComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AviationEmissionsComponent extends BaseAviationEmissionsComponent {
  form = this.formProvider.calculationApproachCtrl;

  vm$: Observable<SupportingDocumentsViewModel> = this.store.pipe(requestTaskQuery.selectIsEditable).pipe(
    map((isEditable) => {
      return {
        form: this.form,
        downloadUrl: `${this.store.dreDelegate.baseFileAttachmentDownloadUrl}/`,
        submitHidden: !isEditable,
      };
    }),
  );

  onSubmit() {
    this.saveDreAndNavigate(this.formProvider.getFormValue(), 'in progress', '../aviation-emissions-charges', false);
  }
}
