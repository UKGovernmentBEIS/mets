import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { VirService } from '@tasks/vir/core/vir.service';
import { VIR_TASK_FORM } from '@tasks/vir/core/vir-task-form.token';
import { uploadEvidenceQuestionFormProvider } from '@tasks/vir/submit/upload-evidence-question/upload-evidence-question-form.provider';

import { OperatorImprovementResponse, VirApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-upload-evidence-question',
  templateUrl: './upload-evidence-question.component.html',
  providers: [uploadEvidenceQuestionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UploadEvidenceQuestionComponent implements PendingRequest {
  verificationDataItem = this.route.snapshot.data.verificationDataItem as VerificationDataItem;
  isEditable$ = this.virService.isEditable$;
  reference = this.verificationDataItem.reference;

  constructor(
    @Inject(VIR_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly virService: VirService,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    let nextRoute = `../../${this.reference}/upload-evidence-files`;
    if (this.form.get('uploadEvidence').value === false) {
      nextRoute = `../../${this.reference}/summary`;
    }
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      this.virService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.virService.postVirTaskSave({
              operatorImprovementResponses: {
                ...payload?.operatorImprovementResponses,
                [this.reference]: this.getFormData(payload),
              },
              virSectionsCompleted: {
                ...payload?.virSectionsCompleted,
                [this.reference]: false,
              },
            }),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  private getFormData(payload: VirApplicationSubmitRequestTaskPayload): OperatorImprovementResponse {
    const uploadEvidenceValue = this.form.get('uploadEvidence').value;
    const existingFiles = payload?.operatorImprovementResponses[this.reference]?.files;
    return {
      ...payload?.operatorImprovementResponses[this.reference],
      uploadEvidence: uploadEvidenceValue,
      files: uploadEvidenceValue && existingFiles ? existingFiles : [],
    };
  }
}
