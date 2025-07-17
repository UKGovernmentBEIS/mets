import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReferenceItemFormProvider } from '@aviation/request-task/vir/submit/tasks/reference-item/reference-item-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';

import { OperatorImprovementResponse } from 'pmrv-api';

interface ViewModel {
  form: UntypedFormGroup;
  heading: string;
  downloadUrl: string;
  isEditable: boolean;
}

@Component({
  selector: 'app-upload-evidence-files',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule],
  template: `
    <app-wizard-step
      *ngIf="vm$ | async as vm"
      (formSubmit)="onSubmit()"
      [formGroup]="vm.form"
      submitText="Continue"
      [heading]="vm.heading"
      [hideSubmit]="vm.isEditable === false">
      <app-multiple-file-input
        [baseDownloadUrl]="vm.downloadUrl"
        label="Upload files"
        headerSize="s"
        listTitle="Uploaded attachments"
        formControlName="files"></app-multiple-file-input>
    </app-wizard-step>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class UploadEvidenceFilesComponent {
  form = new FormGroup(
    {
      files: this.formProvider.filesCtrl,
    },
    { updateOn: 'change' },
  );

  private verificationDataItem = this.route.snapshot.data.verificationDataItem as VerificationDataItem;

  vm$: Observable<ViewModel> = this.store.pipe(requestTaskQuery.selectIsEditable).pipe(
    map((isEditable) => {
      return {
        form: this.form,
        heading: `Upload evidence and documents: ${this.verificationDataItem.reference}`,
        downloadUrl: `${this.store.virDelegate.baseFileAttachmentDownloadUrl}/`,
        isEditable: isEditable,
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: ReferenceItemFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.store.virDelegate
      .saveVir(this.getFormData(), this.verificationDataItem.reference, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.form.get('files').value.forEach((doc) => {
          this.store.virDelegate.addVirAttachment({ [doc.uuid]: doc.file.name });
        });
        this.formProvider.setFormValue(this.getFormData());
        this.router.navigate(['../summary'], {
          relativeTo: this.route,
        });
      });
  }

  private getFormData(): OperatorImprovementResponse {
    return {
      ...this.formProvider.getFormValue(),
      files: this.form.get('files').value.map((doc) => doc.uuid),
    };
  }
}
