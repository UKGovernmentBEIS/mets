import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { uploadFileProvider } from './upload-file.provider';

@Component({
  selector: 'app-upload-file',
  templateUrl: './upload-file.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [uploadFileProvider],
})
export class UploadFileComponent {
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  readonly isFileUploaded$: Observable<boolean> = this.form.get('files').valueChanges.pipe(
    startWith(this.form.get('files').value),
    map((value) => value?.length > 0),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  getDownloadUrl() {
    return this.store.createBaseFileAttachmentDownloadUrl();
  }

  onContinue(): void {
    combineLatest([this.permitTask$, this.store])
      .pipe(
        first(),
        switchMap(([permitTask, state]) =>
          this.store.postTask(permitTask, {
            ...state.permit.uncertaintyAnalysis,
            attachments: this.form.value.files?.map((file) => file.uuid),
          }),
        ),
        switchMap(() => this.store),
        first(),
        tap((state) =>
          this.store.setState({
            ...state,
            permitAttachments: {
              ...state.permitAttachments,
              ...this.form.value.files?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
            },
          }),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        this.router.navigate(['../answers'], { relativeTo: this.route });
      });
  }
}
