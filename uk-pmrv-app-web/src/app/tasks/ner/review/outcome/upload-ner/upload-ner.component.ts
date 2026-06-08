import { ChangeDetectionStrategy, Component, inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { NER_TASK_FORM, NerService } from '@tasks/ner/core';
import { NerTaskComponent } from '@tasks/ner/shared';

import { NERApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { nerReviewUploadNerFormProvider } from './upload-ner-form.provider';

@Component({
  selector: 'app-ner-review-upload-ner',
  imports: [NerTaskComponent, SharedModule],
  templateUrl: './upload-ner.component.html',
  providers: [nerReviewUploadNerFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerReviewUploadNerComponent {
  private readonly nerService = inject(NerService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly pendingRequest = inject(PendingRequestService);
  private readonly payload = this.nerService.payload as Signal<NERApplicationRegulatorReviewSubmitRequestTaskPayload>;

  form = inject<UntypedFormGroup>(NER_TASK_FORM);
  isEditable = this.nerService.isEditable;
  requestTaskType = this.nerService.requestTaskType;

  private getNerReviewAttachments() {
    const attachments =
      this.form.controls.supportingFiles.value?.reduce(
        (acc: any, file: any) => ({ ...acc, [file.uuid]: file.file.name }),
        {},
      ) || {};

    const nerFile = this.form.controls.nerFile.value;
    if (nerFile) {
      attachments[nerFile.uuid] = nerFile.file.name;
    }

    return attachments;
  }

  getDownloadUrl() {
    return this.nerService.getBaseFileDownloadUrl();
  }

  getDocumentDownloadUrl(uuid: string): string | string[] {
    return ['../../../..', 'file-download', uuid];
  }

  renameFile = (originalFile: File): File => {
    const suffix = originalFile.name.slice(((originalFile.name.lastIndexOf('.') - 1) >>> 0) + 1);
    const fileVersion = this.payload().nerFileVersion;
    const newName = this.nerService.fileName(fileVersion, suffix);

    return new File([originalFile], newName, { type: originalFile.type, lastModified: originalFile.lastModified });
  };

  onSubmit() {
    const { nerFile, supportingFiles } = this.form.controls;

    if (this.form.dirty) {
      this.nerService
        .postRegulatorTaskSave(
          {
            nerFile: nerFile.value?.uuid,
            supportingFiles: supportingFiles.value?.map((file: any) => file.uuid),
          },
          false,
          'OUTCOME',
          {
            ...this.payload()?.regulatorReviewAttachments,
            ...this.getNerReviewAttachments(),
          },
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['..', 'summary'], { relativeTo: this.route }));
    } else {
      this.router.navigate(['..', 'summary'], { relativeTo: this.route });
    }
  }
}
