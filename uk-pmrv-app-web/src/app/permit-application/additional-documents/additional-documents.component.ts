/* eslint-disable @angular-eslint/component-max-inline-declarations */
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { SectionComponent } from '../shared/section/section.component';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { additionalDocumentsFormFactory } from './additional-documents-form.provider';

@Component({
  selector: 'app-additional-documents',
  template: `
    <app-permit-task reviewGroupTitle="Additional information" reviewGroupUrl="additional-info" [breadcrumb]="true">
      <app-additional-documents-shared
        [form]="form"
        [isEditable]="store.isEditable$ | async"
        (formSubmit)="onSubmit()"
        [downloadUrl]="getDownloadUrl()"></app-additional-documents-shared>
      <app-list-return-link
        reviewGroupTitle="Additional information"
        reviewGroupUrl="additional-info"></app-list-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [additionalDocumentsFormFactory],
})
export class AdditionalDocumentsComponent extends SectionComponent {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    readonly router: Router,
    readonly route: ActivatedRoute,
  ) {
    super(store, router, route);
  }

  onSubmit(): void {
    this.store
      .postTask(
        'additionalDocuments',
        {
          ...this.form.value,
          documents: this.form.value.documents?.map((file) => file.uuid),
        },
        true,
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.store.setState({
          ...this.store.getState(),
          permitAttachments: {
            ...this.store.getState().permitAttachments,
            ...this.form.value.documents?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
          },
        });
        this.navigateSubmitSection('summary', 'additional-info');
      });
  }

  getDownloadUrl() {
    return this.store.createBaseFileAttachmentDownloadUrl();
  }
}
