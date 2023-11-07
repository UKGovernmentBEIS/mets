import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { buildTaskData } from '@tasks/aer/submit/fallback/fallback';
import { uploadDocumentsFormProvider } from '@tasks/aer/submit/fallback/upload-documents/upload-documents-form.provider';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-upload-documents',
  templateUrl: './upload-documents.component.html',
  providers: [uploadDocumentsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UploadDocumentsComponent implements PendingRequest {
  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const nextRoute = '../summary';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      (this.aerService.getPayload() as Observable<AerApplicationSubmitRequestTaskPayload>)
        .pipe(
          first(),
          switchMap((payload) =>
            this.aerService.postTaskSave(
              buildTaskData(payload, {
                files: this.form.controls.files.value?.map((file) => file.uuid),
              }),
              {
                ...payload?.aerAttachments,
                ...this.getAerAttachments(),
              },
              false,
              'FALLBACK',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  getDownloadUrl() {
    return this.aerService.createBaseFileDownloadUrl();
  }

  private getAerAttachments() {
    return this.form.controls.files.value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {});
  }
}
