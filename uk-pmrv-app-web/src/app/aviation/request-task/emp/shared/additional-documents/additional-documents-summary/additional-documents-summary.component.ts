import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable, switchMap, take } from 'rxjs';

import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import { variationSubmitRegulatorLedRequestTaskTypes } from '@aviation/request-task/emp/shared/util/emp.util';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { SharedModule } from '@shared/shared.module';

import { EmpAdditionalDocuments } from 'pmrv-api';

import {
  getSummaryHeaderForTaskType,
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '../../../../util';
import { empQuery } from '../../emp.selectors';
import { EmpReviewDecisionGroupComponent } from '../../emp-review-decision-group/emp-review-decision-group.component';
import { EmpVariationReviewDecisionGroupComponent } from '../../emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import { AdditionalDocumentsFormProvider } from '../additional-documents-form.provider';
import { additionalDocsQuery } from '../store/additional-documents.selectors';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  hasDocuments: boolean;
  files: { downloadUrl: string; fileName: string }[];
  additionalDocuments: EmpAdditionalDocuments;
  hideSubmit: boolean;
  showDecision: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision: boolean;
  showDiff: boolean;
  originalAddDocs: EmpAdditionalDocuments;
  originalFiles: { downloadUrl: string; fileName: string }[];
}

@Component({
  selector: 'app-additional-documents-summary',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    EmpVariationReviewDecisionGroupComponent,
    EmpReviewDecisionGroupComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
  ],
  templateUrl: './additional-documents-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalDocumentsSummaryComponent {
  private store = inject(RequestTaskStore);
  private formProvider = inject<AdditionalDocumentsFormProvider>(TASK_FORM_PROVIDER);
  private pendingRequestService = inject(PendingRequestService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empQuery.selectStatusForTask('additionalDocuments')),
    this.store.pipe(empQuery.selectOriginalEmpContainer),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer]) => ({
      pageHeader: getSummaryHeaderForTaskType(type, 'additionalDocuments'),
      isEditable,
      hasDocuments: this.formProvider.getFormValue().exist,
      files:
        this.formProvider.getFormValue().documents?.map((doc) => {
          return {
            fileName: doc.file.name,
            downloadUrl: `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/${doc.uuid}`,
          };
        }) ?? [],
      additionalDocuments: this.additionalDocuments,
      hideSubmit:
        !isEditable ||
        ['complete', 'cannot start yet'].includes(taskStatus) ||
        variationSubmitRegulatorLedRequestTaskTypes.includes(type),
      showDecision: showReviewDecisionComponent.includes(type),
      showVariationDecision: showVariationReviewDecisionComponent.includes(type),
      showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
      showDiff: !!originalEmpContainer,
      originalAddDocs: originalEmpContainer?.emissionsMonitoringPlan?.additionalDocuments,
      originalFiles:
        originalEmpContainer?.emissionsMonitoringPlan?.additionalDocuments?.documents?.map((doc) => {
          return {
            fileName: originalEmpContainer?.empAttachments[doc],
            downloadUrl: `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/${doc}`,
          };
        }) ?? [],
    })),
  );

  onSubmit() {
    this.store
      .pipe(
        additionalDocsQuery.selectAdditionalDocuments,
        take(1),
        switchMap((additionalDocuments) => this.store.empDelegate.saveEmp({ additionalDocuments })),
        this.pendingRequestService.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../../../..'], { relativeTo: this.route }));
  }

  private get additionalDocuments(): EmpAdditionalDocuments {
    const ret: EmpAdditionalDocuments = { exist: this.formProvider.getFormValue().exist };

    if (ret.exist) {
      ret.documents = this.formProvider.getFormValue().documents.map((doc: FileUpload) => doc.uuid);
    }

    return ret;
  }
}
