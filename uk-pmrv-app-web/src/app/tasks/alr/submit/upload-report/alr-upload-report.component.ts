import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { alrUploadReportFormProvider } from './alr-upload-report-form.provider';

@Component({
  selector: 'app-alr-upload-report',
  templateUrl: './alr-upload-report.component.html',
  providers: [alrUploadReportFormProvider],
  standalone: true,
  imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ALRUploadReportComponent implements PendingRequest {
  isEditable = this.alrService.isEditable;
  alrPayload: Signal<ALRApplicationSubmitRequestTaskPayload> = this.alrService.payload;
  alrRequestMetadata: Signal<ALRApplicationSubmitRequestTaskPayload> = this.alrService.requestMetadata;
  requestTaskType: Signal<ALRApplicationSubmitRequestTaskPayload> = this.alrService.requestTaskType;

  renameFile = (originalFile: File): File => {
    const suffix = originalFile.name.slice(((originalFile.name.lastIndexOf('.') - 1) >>> 0) + 1);
    const newName = this.alrService.fileName(this.getFileNextVersion(), suffix);
    return new File([originalFile], newName, { type: originalFile.type, lastModified: originalFile.lastModified });
  };

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const nextRoute = 'summary';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      const payload = this.alrPayload();
      this.alrService
        .postTaskSave(
          {
            alrFile: this.form.controls.alrFile.value?.uuid,
            files: this.form.controls.files.value?.map((file) => file.uuid),
          },
          {
            ...payload?.alrAttachments,
            ...this.getAlrAttachments(),
          },
          false,
          'activity',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  getDownloadUrl() {
    return this.alrService.getBaseFileDownloadUrl();
  }

  getDocumentDownloadUrl(uuid: string): string | string[] {
    return ['../../..', 'file-download', uuid];
  }

  private getAlrAttachments() {
    const attachments =
      this.form.controls.files.value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {}) || {};

    const alrFile = this.form.controls.alrFile.value;
    if (alrFile) {
      attachments[alrFile.uuid] = alrFile.file.name;
    }

    return attachments;
  }

  private getFileNextVersion(): number {
    const fileName = this.form.controls.alrFile?.value?.file?.name;
    if (fileName) {
      const match = fileName.match(/-v(\d+)-/);
      return match ? parseInt(match[1]) + 1 : 1;
    }
    return 1;
  }
}
