import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AdditionalDocumentsFormModel, AdditionalDocumentsFormProvider } from '../additional-documents-form.provider';

interface ViewModel {
  form: FormGroup<AdditionalDocumentsFormModel>;
  submitHidden: boolean;
  downloadUrl: string;
}

@Component({
  selector: 'app-additional-documents-page',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  templateUrl: './additional-documents-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalDocumentsPageComponent {
  private store = inject(RequestTaskStore);
  private formProvider = inject<AdditionalDocumentsFormProvider>(TASK_FORM_PROVIDER);
  private pendingRequestService = inject(PendingRequestService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  vm$: Observable<ViewModel> = this.store.pipe(requestTaskQuery.selectIsEditable).pipe(
    map((isEditable) => {
      return {
        form: this.formProvider.form,
        downloadUrl: `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/`,
        submitHidden: !isEditable,
      };
    }),
  );

  onSubmit() {
    const value = this.formProvider.getFormValue();

    this.store.empDelegate
      .saveEmp(
        { additionalDocuments: { exist: !!value.exist, documents: value?.documents?.map((doc) => doc.uuid) } },
        'in progress',
      )
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        value?.documents?.forEach((doc) => {
          this.store.empDelegate.addEmpAttachment({ [doc.uuid]: doc.file.name });
        });

        this.router.navigate(['summary'], { relativeTo: this.route });
      });
  }
}
