import { ChangeDetectionStrategy, Component, inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { NER_TASK_FORM, NerService } from '@tasks/ner/core';
import { NerTaskComponent } from '@tasks/ner/shared';

import { NerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { nerUploadNerFormProvider } from './upload-ner-form.provider';

@Component({
  selector: 'app-upload-ner',
  imports: [SharedModule, NerTaskComponent],
  templateUrl: './upload-ner.component.html',
  providers: [nerUploadNerFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerDetailsUploadNerComponent {
  private readonly nerService = inject(NerService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly pendingRequest = inject(PendingRequestService);
  private readonly payload = this.nerService.payload as Signal<NerApplicationSubmitRequestTaskPayload>;

  form = inject<UntypedFormGroup>(NER_TASK_FORM);
  isEditable = this.nerService.isEditable;
  requestTaskType = this.nerService.requestTaskType;

  getDownloadUrl() {
    return this.nerService.getBaseFileDownloadUrl();
  }

  getDocumentDownloadUrl(uuid: string): string | string[] {
    return ['../../..', 'file-download', uuid];
  }

  renameFile = (originalFile: File): File => {
    const suffix = originalFile.name.slice(((originalFile.name.lastIndexOf('.') - 1) >>> 0) + 1);
    const fileVersion = this.payload().nerFileVersion;
    const newName = this.nerService.fileName(fileVersion, suffix);

    return new File([originalFile], newName, { type: originalFile.type, lastModified: originalFile.lastModified });
  };

  onSubmit() {
    const nextWizardStep = ['./', 'upload-mmp'];
    const payload = this.payload();
    const { file, supportingFiles, notes } = this.form.controls;

    if (this.form.dirty) {
      this.nerService
        .postTaskSave(
          {
            nerFiles: {
              file: file.value?.uuid,
              supportingFiles: supportingFiles.value?.map((file: any) => file.uuid),
            },
            notes: notes.value,
          },
          {
            ...payload?.nerAttachments,
            ...this.getNerAttachments(),
          },
          false,
          'NER',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(nextWizardStep, { relativeTo: this.route }));
    } else {
      this.router.navigate(nextWizardStep, { relativeTo: this.route });
    }
  }

  private getNerAttachments() {
    const attachments =
      this.form.controls.supportingFiles.value?.reduce(
        (acc: any, file: any) => ({ ...acc, [file.uuid]: file.file.name }),
        {},
      ) || {};

    const report = this.form.controls.file.value;
    if (report) {
      attachments[report.uuid] = report.file.name;
    }

    return attachments;
  }
}
