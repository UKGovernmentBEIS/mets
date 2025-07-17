import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { ALR_TASK_FORM, AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { ALRApplicationVerificationSubmitRequestTaskPayload, ALRVerificationOpinionStatement } from 'pmrv-api';

import { alrOpinionStatementFormProvider } from './upload-opinion-statement-form.provider';

@Component({
  selector: 'app-alr-verification-upload-opinion-statement',
  standalone: true,
  imports: [AlrTaskSharedModule, SharedModule],
  templateUrl: './upload-opinion-statement.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [alrOpinionStatementFormProvider],
})
export class AlrUploadOpinionStatementComponent {
  isEditable = this.alrService.isEditable;
  payload: Signal<ALRApplicationVerificationSubmitRequestTaskPayload> = this.alrService.payload;
  opinionStatementFilesChanged: Signal<ALRVerificationOpinionStatement['opinionStatementFiles']> = toSignal(
    this.form.get('opinionStatementFiles').valueChanges,
  );

  constructor(
    @Inject(ALR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  getDownloadUrl() {
    return this.alrService.getBaseFileDownloadUrl();
  }

  getDocumentDownloadUrl(uuid: string): string | string[] {
    return ['../../..', 'file-download', uuid];
  }

  onContinue() {
    const nextRoute = 'summary';

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route });
    } else {
      const payload = this.payload();

      this.alrService
        .postVerificationTaskSave(
          {
            opinionStatement: {
              opinionStatementFiles: this.form.controls?.opinionStatementFiles?.value?.map((file) => file.uuid),
              supportingFiles: this.form.controls?.supportingFiles?.value?.map((file) => file.uuid),
              notes: this.form.value?.notes,
            },
          },
          false,
          'opinionStatement',
          {
            ...payload?.verificationAttachments,
            ...this.getVerificationAttachments(),
          },
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => {
          this.router.navigate([nextRoute], { relativeTo: this.route });
        });
    }
  }

  private getVerificationAttachments() {
    return [
      ...(this.form.controls?.opinionStatementFiles.value ?? []),
      ...(this.form.controls?.supportingFiles?.value ?? []),
    ]?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {});
  }
}
