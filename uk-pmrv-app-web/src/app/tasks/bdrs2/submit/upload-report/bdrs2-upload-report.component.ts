import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared/bdrs2-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRS2ApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { bdrs2UploadReportFormProvider } from './bdrs2-upload-report-form.provider';

@Component({
  selector: 'app-bdrs2-upload-report',
  templateUrl: './bdrs2-upload-report.component.html',
  providers: [bdrs2UploadReportFormProvider],
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BDRS2UploadReportComponent implements PendingRequest {
  isEditable = this.bdrs2Service.isEditable;
  bdrs2Payload: Signal<BDRS2ApplicationSubmitRequestTaskPayload> = this.bdrs2Service.payload;
  bdrs2RequestMetadata: Signal<BDRS2ApplicationSubmitRequestTaskPayload> = this.bdrs2Service.requestMetadata;
  requestTaskType: Signal<BDRS2ApplicationSubmitRequestTaskPayload> = this.bdrs2Service.requestTaskType;
  returnLinkTitle = this.bdrs2Service.title();

  renameFile = (originalFile: File): File => {
    const suffix = originalFile.name.slice(((originalFile.name.lastIndexOf('.') - 1) >>> 0) + 1);
    const fileVersion = this.bdrs2Payload().bdrs2FileVersion;
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

  onContinue(): void {
    const payload = this.bdrs2Payload();
    const isYesCBAM = payload.bdrs2?.bdrs2guardQuestions?.requiresAdditionalSubInstallationSplitsForCbam === true;
    const nextRoute = isYesCBAM ? '../upload-mmp' : '../..';

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route });
    } else {
      this.bdrs2Service
        .postTaskSave(
          {
            bdrs2Files: {
              file: this.form.controls.bdrs2File.value?.uuid,
              supportingFiles: this.form.controls.files.value?.map((file) => file.uuid),
            },
          },
          {
            ...payload?.bdrs2Attachments,
            ...this.getBdrS2Attachments(),
          },
          false,
          'baseline',
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

  private getBdrS2Attachments() {
    const attachments =
      this.form.controls.files.value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {}) || {};

    const bdrs2File = this.form.controls.bdrs2File.value;
    if (bdrs2File) {
      attachments[bdrs2File.uuid] = bdrs2File.file.name;
    }

    return attachments;
  }
}
