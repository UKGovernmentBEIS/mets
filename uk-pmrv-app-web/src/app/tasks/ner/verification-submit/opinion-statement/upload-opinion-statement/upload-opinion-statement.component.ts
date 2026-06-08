import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { NER_TASK_FORM, NerService } from '@tasks/ner/core';
import { NerTaskComponent } from '@tasks/ner/shared/components/ner-task/ner-task.component';

import { NERApplicationVerificationSubmitRequestTaskPayload, RequestTaskDTO } from 'pmrv-api';

import { nerOpinionStatementFormProvider } from './upload-opinion-statement-form.provider';

interface ViewModel {
  isEditable: boolean;
  requestTaskType: RequestTaskDTO['type'];
  downloadUrl: string;
}

@Component({
  selector: 'app-ner-upload-opinion-statement',
  imports: [NerTaskComponent, SharedModule],
  templateUrl: './upload-opinion-statement.component.html',
  providers: [nerOpinionStatementFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerUploadOpinionStatementComponent {
  private readonly nerService = inject(NerService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly pendingRequest = inject(PendingRequestService);
  private readonly isEditable = this.nerService.isEditable;
  private readonly requestTaskType = this.nerService.requestTaskType;
  private readonly payload = this.nerService.payload as Signal<NERApplicationVerificationSubmitRequestTaskPayload>;

  private getDownloadUrl() {
    return this.nerService.getBaseFileDownloadUrl();
  }

  private getVerificationAttachments() {
    const attachments =
      this.form.controls.supportingFiles.value?.reduce(
        (acc: any, file: { uuid: string; file: { name: string } }) => ({ ...acc, [file.uuid]: file.file.name }),
        {},
      ) || {};

    const opinionStatementFile = this.form.controls.opinionStatementFile.value;
    if (opinionStatementFile) {
      attachments[opinionStatementFile.uuid] = opinionStatementFile.file.name;
    }

    return attachments;
  }

  form = inject<UntypedFormGroup>(NER_TASK_FORM);

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const requestTaskType = this.requestTaskType();

    return {
      isEditable,
      requestTaskType,
      downloadUrl: this.getDownloadUrl(),
    };
  });

  getDocumentDownloadUrl(uuid: string): string | string[] {
    return ['../../..', 'file-download', uuid];
  }

  onContinue() {
    const nextRoute = 'summary';

    if (this.form.dirty) {
      const payload = this.payload();

      this.nerService
        .postVerificationTaskSave(
          {
            opinionStatement: {
              opinionStatementFile: this.form.controls?.opinionStatementFile?.value?.uuid,
              supportingFiles: this.form.controls?.supportingFiles?.value?.map((file: { uuid: string }) => file.uuid),
              notes: this.form.value?.notes,
            },
          },
          false,
          'OPINION_STATEMENT',
          {
            ...payload?.verificationAttachments,
            ...this.getVerificationAttachments(),
          },
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => {
          this.router.navigate([nextRoute], { relativeTo: this.route });
        });
    } else {
      this.router.navigate([nextRoute], { relativeTo: this.route });
    }
  }
}
