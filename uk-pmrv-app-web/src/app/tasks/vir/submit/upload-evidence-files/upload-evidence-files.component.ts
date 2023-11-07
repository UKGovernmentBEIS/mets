import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { VirService } from '@tasks/vir/core/vir.service';
import { VIR_TASK_FORM } from '@tasks/vir/core/vir-task-form.token';
import { uploadEvidenceFilesFormProvider } from '@tasks/vir/submit/upload-evidence-files/upload-evidence-files-form.provider';

import { OperatorImprovementResponse, VirApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-upload-evidence-files',
  templateUrl: './upload-evidence-files.component.html',
  providers: [uploadEvidenceFilesFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UploadEvidenceFilesComponent implements PendingRequest {
  verificationDataItem = this.route.snapshot.data.verificationDataItem as VerificationDataItem;
  reference = this.verificationDataItem.reference;
  heading = `Upload evidence and documents: ${this.reference}`;
  isEditable$ = this.virService.isEditable$;

  constructor(
    @Inject(VIR_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly virService: VirService,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    const nextRoute = `../../${this.reference}/summary`;
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      this.virService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.virService.postVirTaskSave(
              {
                operatorImprovementResponses: {
                  ...payload?.operatorImprovementResponses,
                  [this.reference]: this.getFormData(payload),
                },
                virSectionsCompleted: {
                  ...payload?.virSectionsCompleted,
                  [this.reference]: false,
                },
              },
              {
                ...payload?.virAttachments,
                ...this.getVirAttachments(),
              },
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  getDownloadUrl() {
    return this.virService.createBaseFileDownloadUrl();
  }

  private getFormData(payload: VirApplicationSubmitRequestTaskPayload): OperatorImprovementResponse {
    return {
      ...payload?.operatorImprovementResponses[this.reference],
      files: this.form.get('files').value?.map((file) => file.uuid),
    };
  }

  private getVirAttachments() {
    return this.form.get('files').value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {});
  }
}
