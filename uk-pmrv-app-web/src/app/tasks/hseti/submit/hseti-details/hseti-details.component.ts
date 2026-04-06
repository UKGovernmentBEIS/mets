import { ChangeDetectionStrategy, Component, computed, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { HSE_TI_TASK_FORM, HseTiService } from '@tasks/hseti/core';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { HSETIApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { hseTiUploadReportFormProvider } from './hseti-details-form.provider';

@Component({
  selector: 'app-hseti-details',
  imports: [SharedModule, TaskSharedModule, HseTiTaskSharedModule],
  templateUrl: './hseti-details.component.html',
  providers: [hseTiUploadReportFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HSETIDetailsComponent implements PendingRequest {
  isEditable = this.hseTiService.isEditable;
  readonly allocationPeriod: Signal<string> = this.hseTiService.allocationPeriod;
  readonly title: Signal<string> = computed(() => `Upload the ${this.allocationPeriod()} HSE target increase file`);

  readonly hseTiPayload: Signal<HSETIApplicationSubmitRequestTaskPayload> = this.hseTiService.payload;
  readonly hseTiRequestMetadata: Signal<HSETIApplicationSubmitRequestTaskPayload> = this.hseTiService.requestMetadata;
  readonly requestTaskType: Signal<HSETIApplicationSubmitRequestTaskPayload> = this.hseTiService.requestTaskType;

  constructor(
    @Inject(HSE_TI_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly hseTiService: HseTiService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const nextRoute = 'summary';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      const payload = this.hseTiPayload();
      this.hseTiService
        .postTaskSave(
          {
            hsetiFile: this.form.controls.hseTiFile.value?.uuid,
            files: this.form.controls.files.value?.map((file) => file.uuid),
            notes: this.form.controls.notes.value,
          },
          {
            ...payload?.hsetiAttachments,
            ...this.getHseTiAttachments(),
          },
          false,
          'details',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  getDownloadUrl() {
    return this.hseTiService.getBaseFileDownloadUrl();
  }

  getDocumentDownloadUrl(uuid: string): string | string[] {
    return ['../../..', 'file-download', uuid];
  }

  private getHseTiAttachments() {
    const attachments =
      this.form.controls.files.value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {}) || {};

    const hseTiFile = this.form.controls.hseTiFile.value;
    if (hseTiFile) {
      attachments[hseTiFile.uuid] = hseTiFile.file.name;
    }

    return attachments;
  }
}
