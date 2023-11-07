import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, startWith } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { DoalService } from '../../core/doal.service';
import { DOAL_TASK_FORM } from '../../core/doal-task-form.token';
import { additionalDocumentsFormProvider } from './additional-documents.component-form.provider';

@Component({
  selector: 'app-additional-documents',
  templateUrl: './additional-documents.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [additionalDocumentsFormProvider],
})
export class AdditionalDocumentsComponent {
  readonly documentsExist$ = this.form.get('documents').valueChanges.pipe(
    startWith(this.form.get('documents').value),
    map((value) => value?.length > 0),
  );

  constructor(
    @Inject(DOAL_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['summary'], { relativeTo: this.route });
    } else {
      this.doalService
        .saveDoal(
          {
            additionalDocuments: {
              ...this.form.value,
              documents: this.form.value.documents?.map((file) => file.uuid),
            },
          },
          'additionalDocuments',
          false,
          {
            ...this.form.value.documents?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
          },
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route }));
    }
  }

  getBaseFileDownloadUrl() {
    return this.doalService.getBaseFileDownloadUrl();
  }
}
