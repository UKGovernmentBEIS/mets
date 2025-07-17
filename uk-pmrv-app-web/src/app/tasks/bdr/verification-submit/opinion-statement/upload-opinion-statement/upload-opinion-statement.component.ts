import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import { opinionStatementFormProvider } from './upload-opinion-statement-form.provider';

@Component({
  selector: 'app-upload-opinion-statement',
  templateUrl: './upload-opinion-statement.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule],
  providers: [opinionStatementFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UploadOpinionStatementComponent {
  isEditable = this.bdrService.isEditable;

  readonly isFileUploaded$: Observable<boolean> = this.form.get('opinionStatementFiles').valueChanges.pipe(
    startWith(this.form.get('opinionStatementFiles').value),
    map((value) => value?.length > 0),
  );

  constructor(
    @Inject(BDR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrService: BdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    const nextRoute = 'summary';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route });
    } else {
      (this.bdrService.getPayload() as Observable<BDRApplicationVerificationSubmitRequestTaskPayload>)
        .pipe(
          first(),
          switchMap((payload) =>
            this.bdrService.postVerificationTaskSave(
              {
                opinionStatement: {
                  opinionStatementFiles: this.form.controls?.opinionStatementFiles?.value?.map((file) => file.uuid),
                  notes: this.form.value?.notes,
                },
              },
              false,
              'opinionStatement',
              {
                ...payload?.verificationAttachments,
                ...this.getVerificationAttachments(),
              },
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  getDownloadUrl() {
    return this.bdrService.getBaseFileDownloadUrl();
  }

  private getVerificationAttachments() {
    return this.form.controls?.opinionStatementFiles.value?.reduce(
      (acc, file) => ({ ...acc, [file.uuid]: file.file.name }),
      {},
    );
  }
}
