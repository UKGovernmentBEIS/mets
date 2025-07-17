import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { getSummaryHeaderForTaskType } from '@aviation/request-task/util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { SharedModule } from '@shared/shared.module';

import { EmpAdditionalDocuments } from 'pmrv-api';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';

interface ViewModel {
  pageHeader: string;
  hasDocuments: boolean;
  files: { downloadUrl: string; fileName: string }[];
  additionalDocuments: EmpAdditionalDocuments;
}

@Component({
  selector: 'app-additional-documents-summary',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.pageHeader }}</app-page-heading>

      <h2 app-summary-header class="govuk-heading-m">
        <span [class.govuk-visually-hidden]="!vm.hasDocuments">Uploaded files</span>
      </h2>

      <app-documents-summary-template
        [data]="vm.additionalDocuments"
        [files]="vm.files"
        [isEditable]="false"></app-documents-summary-template>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AdditionalDocumentsSummaryComponent {
  private store = inject(RequestTaskStore);
  private form = inject<FormGroup>(TASK_FORM_PROVIDER);

  vm$: Observable<ViewModel> = combineLatest([this.store.pipe(requestTaskQuery.selectRequestTaskType)]).pipe(
    map(([type]) => ({
      pageHeader: getSummaryHeaderForTaskType(type, 'additionalDocuments'),
      hasDocuments: this.form.value.exist,
      files:
        this.form.value?.documents?.map((doc) => {
          return {
            fileName: doc.file.name,
            downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${doc.uuid}`,
          };
        }) ?? [],
      additionalDocuments: this.additionalDocuments,
    })),
  );

  private get additionalDocuments(): EmpAdditionalDocuments {
    const ret: EmpAdditionalDocuments = { exist: this.form.value.exist };

    if (ret.exist) {
      ret.documents = this.form.value.documents.map((doc: FileUpload) => doc.uuid);
    }

    return ret;
  }
}
