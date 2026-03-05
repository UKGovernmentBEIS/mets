import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { ConfidentialitySummaryTemplateComponent } from '@aviation/shared/components/aer/confidentiality-summary-template/confidentiality-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaConfidentiality } from 'pmrv-api';

import { ConfidentialityFormProvider } from '../confidentiality-form.provider';

interface ViewModel {
  pageHeader: string;
  confidentialityData: AviationAerCorsiaConfidentiality;
  totalEmissionsFiles: { downloadUrl: string; fileName: string }[];
  aggregatedStatePairDataFiles: { downloadUrl: string; fileName: string }[];
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-confidentiality-summary',
  templateUrl: './confidentiality-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    RouterLinkWithHref,
    ConfidentialitySummaryTemplateComponent,
    AerReviewDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfidentialitySummaryComponent {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectStatusForTask('confidentiality')),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => {
      return {
        pageHeader: getSummaryHeaderForTaskType(type, 'confidentiality'),
        confidentialityData: this.form.valid ? this.form.value : null,
        isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
        hasDocuments: this.form.value.exist,
        totalEmissionsFiles:
          this.formProvider?.totalEmissionsDocumentsCtrl?.getRawValue()?.map((doc) => {
            return {
              fileName: (doc.file as File).name,
              downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${doc.uuid}`,
            };
          }) ?? [],
        aggregatedStatePairDataFiles:
          this.formProvider?.aggregatedStatePairDataDocumentsCtrl?.getRawValue()?.map((doc) => {
            return {
              fileName: doc.file.name,
              downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${doc.uuid}`,
            };
          }) ?? [],
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: ConfidentialityFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    if (this.form?.valid) {
      this.store.aerDelegate
        .saveAer({ confidentiality: this.formProvider.getFormValue() }, 'complete')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../../..'], { relativeTo: this.route });
        });
    }
  }
}
