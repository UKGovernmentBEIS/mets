import { ChangeDetectionStrategy, Component, computed, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { BDRS2_TASK_FORM, BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { uploadBdrs2FilesFormProvider } from './outcome-file-upload-form.provider';

@Component({
  selector: 'app-outcome-file-upload',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
  templateUrl: './outcome-file-upload.component.html',
  providers: [uploadBdrs2FilesFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OutcomeFileUploadComponent implements PendingRequest {
  isEditable = this.bdrs2Service.isEditable;
  bdrPayload = this.bdrs2Service.payload as Signal<BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload>;
  originalFileName: Signal<string> = computed(() => {
    const payload = this.bdrPayload();

    return payload.regulatorReviewAttachments?.[payload?.regulatorReviewOutcome?.file];
  });

  renameFile = (originalFile: File): File => {
    const suffix = originalFile.name.slice(((originalFile.name.lastIndexOf('.') - 1) >>> 0) + 1);

    const fileVersion = this.bdrPayload().bdrs2FileVersion;
    const newName = this.bdrs2Service.fileName(fileVersion, suffix);

    return new File([originalFile], newName, { type: originalFile.type, lastModified: originalFile.lastModified });
  };

  constructor(
    @Inject(BDRS2_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    const nextRoute = '../summary';

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      const payload = this.bdrPayload();
      this.bdrs2Service
        .postRegulatorTaskSave(
          {
            file: this.form.controls.file.value?.uuid,
            supportingFiles: this.form.controls.supportingFiles.value?.map((file) => file.uuid),
          },
          false,
          'outcome',
          {
            ...payload?.bdrs2Attachments,
            ...this.getBdrAttachments(),
          },
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  getDownloadUrl() {
    return this.bdrs2Service.getBaseFileDownloadUrl();
  }

  getDocumentDownloadUrl(uuid: string): string | string[] {
    return ['../../../..', 'file-download', uuid];
  }

  private getBdrAttachments() {
    const attachments =
      this.form.controls.supportingFiles.value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {}) ||
      {};

    const file = this.form.controls.file.value;
    if (file) {
      attachments[file.uuid] = file.file.name;
    }

    return attachments;
  }
}
