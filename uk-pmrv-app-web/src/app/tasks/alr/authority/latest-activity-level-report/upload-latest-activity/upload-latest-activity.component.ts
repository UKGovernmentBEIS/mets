import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { ALRAuthorityResponseSubmitRequestTaskPayload } from 'pmrv-api';

import { alrAuthorityUploadLatestActivityFormProvider } from './upload-latest-activity-form.provider';

@Component({
  selector: 'app-alr-authority-upload-latest-activity',
  imports: [SharedModule, AlrTaskSharedModule],
  templateUrl: './upload-latest-activity.component.html',
  providers: [alrAuthorityUploadLatestActivityFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrAuthorityUploadLatestActivityComponent {
  isEditable = this.alrService.isEditable;

  private readonly payload = this.alrService.payload as Signal<ALRAuthorityResponseSubmitRequestTaskPayload>;

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  private getAlrAttachments() {
    const attachments =
      this.form.controls.files.value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {}) || {};

    const alrFile = this.form.controls.alrFile.value;
    if (alrFile) {
      attachments[alrFile.uuid] = alrFile.file.name;
    }

    return attachments;
  }

  getDownloadUrl() {
    return this.alrService.getBaseFileDownloadUrl();
  }

  getDocumentDownloadUrl(uuid: string): string | string[] {
    return ['../../../../..', 'file-download', uuid];
  }

  renameFile = (originalFile: File): File => {
    const suffix = originalFile.name.slice(((originalFile.name.lastIndexOf('.') - 1) >>> 0) + 1);
    const fileVersion = this.payload().alrFileVersion;
    const newName = this.alrService.fileName(fileVersion, suffix);

    return new File([originalFile], newName, { type: originalFile.type, lastModified: originalFile.lastModified });
  };

  onSubmit() {
    const payload = this.payload();
    const nextStep = ['../', 'summary'];

    if (!this.form.dirty) {
      this.router.navigate(nextStep, { relativeTo: this.route });
    } else {
      this.alrService
        .postAlrAuthority(
          {
            ...payload?.authorityReviewOutcome,
            alr: {
              alrFile: this.form.controls.alrFile.value?.uuid,
              files: this.form.controls.files.value?.map((file) => file.uuid),
            },
          },
          'upload',
          false,
          this.getAlrAttachments(),
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(nextStep, { relativeTo: this.route }));
    }
  }
}
