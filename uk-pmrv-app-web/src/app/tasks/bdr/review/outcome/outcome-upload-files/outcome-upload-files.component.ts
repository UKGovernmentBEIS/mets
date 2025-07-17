import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { uploadFilesFormProvider } from './outcome-upload-files-form.provider';

@Component({
  selector: 'app-outcome-upload-files',
  templateUrl: './outcome-upload-files.component.html',
  providers: [uploadFilesFormProvider],
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OutcomeUploadFilesComponent implements PendingRequest {
  isEditable = this.bdrService.isEditable;
  bdrPayload = this.bdrService.payload as Signal<BDRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  renameFile = (originalFile: File): File => {
    const suffix = originalFile.name.slice(((originalFile.name.lastIndexOf('.') - 1) >>> 0) + 1);
    const newName = this.bdrService.fileName(this.getFileNextVersion(), suffix);
    return new File([originalFile], newName, { type: originalFile.type, lastModified: originalFile.lastModified });
  };

  constructor(
    @Inject(BDR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrService: BdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    const nextRoute = '../';

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      const payload = this.bdrPayload();
      this.bdrService
        .postRegulatorTaskSave(
          {
            bdrFile: this.form.controls.bdrFile.value?.uuid,
            files: this.form.controls.files.value?.map((file) => file.uuid),
          },
          false,
          'outcome',
          {
            ...payload?.bdrAttachments,
            ...this.getBdrAttachments(),
          },
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  getDownloadUrl() {
    return this.bdrService.getBaseFileDownloadUrl();
  }

  getDocumentDownloadUrl(uuid: string): string | string[] {
    return ['../../..', 'file-download', uuid];
  }

  private getBdrAttachments() {
    const attachments =
      this.form.controls.files.value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {}) || {};

    const bdrFile = this.form.controls.bdrFile.value;
    if (bdrFile) {
      attachments[bdrFile.uuid] = bdrFile.file.name;
    }

    return attachments;
  }

  private getFileNextVersion(): number {
    const fileName = this.form.controls.bdrFile?.value?.file?.name;
    if (fileName) {
      const match = fileName.match(/-v(\d+)-/);
      return match ? parseInt(match[1]) + 1 : 1;
    }
    return 1;
  }
}
