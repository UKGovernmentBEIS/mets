import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';

import { map, Observable } from 'rxjs';

import { requestTaskQuery } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { BaseMonitoringApproachComponent } from '../base-monitoring-approach.component';
import { SimplifiedApproachViewModel } from '../monitoring-approach.interface';
import { SimplifiedApproachFormComponent } from '../simplified-approach-form/simplified-approach-form.component';

@Component({
  selector: 'app-simplified-approach',
  templateUrl: './simplified-approach.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, SimplifiedApproachFormComponent, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class SimplifiedApproachComponent extends BaseMonitoringApproachComponent implements OnInit, OnDestroy {
  form = this.formProvider.simplifiedApproachForm;

  vm$: Observable<SimplifiedApproachViewModel> = this.store.pipe(requestTaskQuery.selectIsEditable).pipe(
    map((isEditable) => {
      return {
        form: this.form,
        downloadUrl: `${this.store.empUkEtsDelegate.baseFileAttachmentDownloadUrl}/`,
        submitHidden: !isEditable,
      };
    }),
  );

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit() {
    this.saveEmpAndNavigate(this.formProvider.getFormValue(), 'in progress', '../summary', () => {
      this.form.value.supportingEvidenceFiles.forEach((doc) => {
        this.store.empUkEtsDelegate.addEmpAttachment({ [doc.uuid]: doc.file.name });
      });
    });
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }
}
