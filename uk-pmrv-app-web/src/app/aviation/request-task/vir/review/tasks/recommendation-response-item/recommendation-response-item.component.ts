import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { RecommendationResponseItemFormProvider } from '@aviation/request-task/vir/review/tasks/recommendation-response-item/recommendation-response-item-form.provider';
import { virQuery } from '@aviation/request-task/vir/vir.selectors';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { AttachedFile } from '@shared/types/attached-file.type';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';

import { OperatorImprovementResponse } from 'pmrv-api';

interface ViewModel {
  form: UntypedFormGroup;
  heading: string;
  verificationDataItem: VerificationDataItem;
  operatorImprovementResponse: OperatorImprovementResponse;
  documentFiles: AttachedFile[];
  isEditable: boolean;
}

@Component({
  selector: 'app-recommendation-response-item',
  standalone: true,
  imports: [AsyncPipe, VirSharedModule, ReturnToLinkComponent, NgIf],
  template: `
    <app-recommendation-response-item-form
      *ngIf="vm$ | async as vm"
      [formGroup]="vm.form"
      [heading]="vm.heading"
      [verificationDataItem]="vm.verificationDataItem"
      [operatorImprovementResponse]="vm.operatorImprovementResponse"
      [attachedFiles]="vm.documentFiles"
      [isEditable]="vm.isEditable"
      [isAviation]="true"
      (formSubmit)="onSubmit()"></app-recommendation-response-item-form>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendationResponseItemComponent {
  private verificationDataItem = this.route.snapshot.data.verificationDataItem as VerificationDataItem;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(virQuery.selectOperatorImprovementResponses),
    this.store.pipe(virQuery.selectAttachments),
  ]).pipe(
    map(([isEditable, operatorImprovementResponses, attachments]) => {
      return {
        form: this.formProvider.form,
        heading: `Respond to ${this.verificationDataItem.reference}`,
        verificationDataItem: this.verificationDataItem,
        operatorImprovementResponse: operatorImprovementResponses[this.verificationDataItem.reference],
        documentFiles:
          operatorImprovementResponses[this.verificationDataItem.reference].files?.map((uuid) => {
            return {
              fileName: attachments[uuid],
              downloadUrl: `${this.store.virDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
            };
          }) ?? [],
        isEditable: isEditable,
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: RecommendationResponseItemFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.store.virDelegate
      .saveReviewVir(this.verificationDataItem.reference, 'in progress', this.formProvider.getFormValue())
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.router.navigate(['summary'], {
          relativeTo: this.route,
        });
      });
  }
}
