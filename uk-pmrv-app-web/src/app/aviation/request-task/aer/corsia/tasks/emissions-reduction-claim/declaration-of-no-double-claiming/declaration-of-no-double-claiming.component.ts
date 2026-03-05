import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { ProcedureFormStepComponent } from '@aviation/request-task/emp/shared/procedure-form-step';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import {
  EmissionsReductionClaimDetailsFormModel,
  EmissionsReductionClaimFormProvider,
} from '../emissions-reduction-claim-form.provider';

@Component({
  selector: 'app-declaration-of-no-double-claiming',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    ProcedureFormStepComponent,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    ReactiveFormsModule,
  ],
  templateUrl: './declaration-of-no-double-claiming.component.html',
  viewProviders: [existingControlContainer],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class DeclarationOfNoDoubleClaimingComponent {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);
  private formProvider = inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);

  downloadUrl = `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/`;

  form = new FormGroup<Partial<EmissionsReductionClaimDetailsFormModel>>(
    {
      noDoubleCountingDeclarationFiles: this.formProvider.emissionsReductionClaimDetailsCtrl.controls
        .noDoubleCountingDeclarationFiles as FormControl<FileUpload[]>,
    },
    { updateOn: 'change' },
  );

  onSubmit() {
    if (this.form.invalid) return;

    const value = this.form.value;
    const emissionsReductionClaim = this.getFormData();

    this.store.aerDelegate
      .saveAer({ emissionsReductionClaim: emissionsReductionClaim }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.aerDelegate.setSaf(emissionsReductionClaim);
        if (value.noDoubleCountingDeclarationFiles) {
          this.form.value?.noDoubleCountingDeclarationFiles?.forEach((doc) => {
            this.store.aerDelegate.addAerAttachment({ [doc.uuid]: doc.file.name });
          });
        }
        this.router.navigate(['../', 'summary'], {
          relativeTo: this.route,
        });
      });
  }

  getFormData() {
    const formValue = this.formProvider.getFormValue();
    const files = this.form.value.noDoubleCountingDeclarationFiles?.map((x) => x.uuid);

    return {
      ...formValue,
      emissionsReductionClaimDetails: {
        ...formValue.emissionsReductionClaimDetails,
        noDoubleCountingDeclarationFiles: files,
      },
    };
  }
}
