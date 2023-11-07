import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { ProcedureFormStepComponent } from '@aviation/request-task/emp/shared/procedure-form-step';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';

@Component({
  selector: 'app-upload-template',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    ProcedureFormStepComponent,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    ReactiveFormsModule,
  ],
  templateUrl: './upload-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class UploadTemplateComponent {
  downloadUrl = `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/`;

  constructor(
    private store: RequestTaskStore,
    private router: Router,
    private route: ActivatedRoute,
    private pendingRequestService: PendingRequestService,
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: EmissionsReductionClaimFormProvider,
  ) {}

  form = new FormGroup(
    {
      cefFiles: this.formProvider.cefFilesCtlr,
      totalEmissions: this.formProvider.totalEmissionsCtlr,
    },
    { updateOn: 'change' },
  );

  onSubmit() {
    const emissionsReductionClaim = this.getFormData();

    this.store.aerDelegate
      .saveAer({ emissionsReductionClaim: emissionsReductionClaim }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.aerDelegate.setSaf(emissionsReductionClaim);
        this.form.value?.cefFiles?.forEach((doc) => {
          this.store.aerDelegate.addAerAttachment({ [doc.uuid]: doc.file.name });
        });
        this.router.navigate(['../declaration-of-no-double-claiming'], {
          relativeTo: this.route,
        });
      });
  }

  getFormData() {
    const formValue = this.formProvider.getFormValue();
    const files = this.form.value.cefFiles.map((x) => x.uuid);

    return {
      ...formValue,
      emissionsReductionClaimDetails: {
        ...formValue.emissionsReductionClaimDetails,
        cefFiles: files,
        totalEmissions: this.form.value.totalEmissions,
      },
    } as any;
  }
}
