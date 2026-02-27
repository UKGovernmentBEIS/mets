import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { BDRS2_TASK_FORM, BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared/bdrs2-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRS2ApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { bdrs2UploadMmpFormProvider } from './bdrs2-upload-mmp-form.provider';

@Component({
  selector: 'app-bdrs2-upload-mmp',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
  standalone: true,
  templateUrl: './bdrs2-upload-mmp.component.html',
  providers: [bdrs2UploadMmpFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BDRS2UploadMmpComponent implements PendingRequest {
  isEditable = this.bdrs2Service.isEditable;
  bdrs2Payload: Signal<BDRS2ApplicationSubmitRequestTaskPayload> = this.bdrs2Service.payload;
  bdrs2RequestMetadata: Signal<BDRS2ApplicationSubmitRequestTaskPayload> = this.bdrs2Service.requestMetadata;
  requestTaskType: Signal<BDRS2ApplicationSubmitRequestTaskPayload> = this.bdrs2Service.requestTaskType;
  returnLinkTitle = this.bdrs2Service.title();

  constructor(
    @Inject(BDRS2_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const nextRoute = '../summary';

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route });
    } else {
      const payload = this.bdrs2Payload();
      this.bdrs2Service
        .postTaskSave(
          {
            mmpFiles: {
              file: this.form.controls.mmpFile.value?.uuid,
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

    const mmpFile = this.form.controls.mmpFile.value;
    if (mmpFile) {
      attachments[mmpFile.uuid] = mmpFile.file.name;
    }

    return attachments;
  }
}
