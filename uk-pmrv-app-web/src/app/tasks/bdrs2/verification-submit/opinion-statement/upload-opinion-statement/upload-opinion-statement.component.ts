import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core/bdrs2-task-form.token';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared/bdrs2-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRS2ApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import { opinionStatementFormProvider } from './upload-opinion-statement-form.provider';

@Component({
  selector: 'app-upload-opinion-statement',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
  standalone: true,
  templateUrl: './upload-opinion-statement.component.html',
  providers: [opinionStatementFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UploadOpinionStatementComponent {
  isEditable = this.bdrs2Service.isEditable;

  readonly isFileUploaded$: Observable<boolean> = this.form.get('opinionStatementFiles').valueChanges.pipe(
    startWith(this.form.get('opinionStatementFiles').value),
    map((value) => value?.length > 0),
  );

  constructor(
    @Inject(BDRS2_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    const nextRoute = 'summary';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route });
    } else {
      (this.bdrs2Service.getPayload() as Observable<BDRS2ApplicationVerificationSubmitRequestTaskPayload>)
        .pipe(
          first(),
          switchMap((payload) =>
            this.bdrs2Service.postVerificationTaskSave(
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
    return this.bdrs2Service.getBaseFileDownloadUrl();
  }

  private getVerificationAttachments() {
    return this.form.controls?.opinionStatementFiles.value?.reduce(
      (acc, file) => ({ ...acc, [file.uuid]: file.file.name }),
      {},
    );
  }
}
