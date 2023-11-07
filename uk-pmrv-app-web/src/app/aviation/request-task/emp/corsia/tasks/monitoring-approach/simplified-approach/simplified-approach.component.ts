import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { SimplifiedApproachFormModel } from '../monitoring-approach.interfaces';
import { MonitoringApproachCorsiaFormProvider } from '../monitoring-approach-form.provider';
import { SimplifiedApproachFormComponent } from '../simplified-approach-form';

@Component({
  selector: 'app-simplified-approach',
  templateUrl: './simplified-approach.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, SimplifiedApproachFormComponent, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class SimplifiedApproachComponent {
  form = this.formProvider.simplifiedApproachForm;

  vm$: Observable<SimplifiedApproachFormModel> = this.store.pipe(requestTaskQuery.selectIsEditable).pipe(
    map((isEditable) => {
      return {
        form: this.form,
        downloadUrl: `${this.store.empCorsiaDelegate.baseFileAttachmentDownloadUrl}/`,
        submitHidden: !isEditable,
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: MonitoringApproachCorsiaFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    const file = this.form.get('supportingEvidenceFiles').value;

    this.store.empCorsiaDelegate
      .saveEmp({ emissionsMonitoringApproach: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empCorsiaDelegate.setEmissionsMonitoringApproach(this.formProvider.getFormValue());

        file?.forEach((doc) => {
          this.store.empCorsiaDelegate.addEmpAttachment({ [doc.uuid]: doc.file.name });
        });

        this.router.navigate(['../summary'], {
          relativeTo: this.route,
        });
      });
  }
}
