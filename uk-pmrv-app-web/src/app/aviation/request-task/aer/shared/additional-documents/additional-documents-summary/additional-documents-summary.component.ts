import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable, switchMap, take } from 'rxjs';

import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { EmpAdditionalDocuments } from 'pmrv-api';

import { aerQuery } from '../../aer.selectors';
import { additionalDocsQuery } from '../store/additional-documents.selectors';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  taskStatus: TaskItemStatus;
  hasDocuments: boolean;
  files: { downloadUrl: string; fileName: string }[];
  additionalDocuments: EmpAdditionalDocuments;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-additional-documents-summary',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, AerReviewDecisionGroupComponent],
  templateUrl: './additional-documents-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalDocumentsSummaryComponent {
  private store = inject(RequestTaskStore);
  private form = inject<FormGroup>(TASK_FORM_PROVIDER);
  private pendingRequestService = inject(PendingRequestService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectStatusForTask('additionalDocuments')),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => ({
      pageHeader: getSummaryHeaderForTaskType(type, 'additionalDocuments'),
      isEditable,
      taskStatus,
      hasDocuments: this.form.value.exist,
      showDecision: showReviewDecisionComponent.includes(type),
      files:
        this.form.value?.documents?.map((doc) => {
          return {
            fileName: doc.file.name,
            downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${doc.uuid}`,
          };
        }) ?? [],
      additionalDocuments: this.additionalDocuments,
      hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
    })),
  );

  onSubmit() {
    this.store
      .pipe(
        additionalDocsQuery.selectAdditionalDocuments,
        take(1),
        switchMap((additionalDocuments) => this.store.aerDelegate.saveAer({ additionalDocuments })),
        this.pendingRequestService.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.route }));
  }

  private get additionalDocuments(): EmpAdditionalDocuments {
    const ret: EmpAdditionalDocuments = { exist: this.form.value.exist };
    if (ret.exist) {
      ret.documents = this.form.value.documents.map((doc: FileUpload) => doc.uuid);
    }

    return ret;
  }
}
