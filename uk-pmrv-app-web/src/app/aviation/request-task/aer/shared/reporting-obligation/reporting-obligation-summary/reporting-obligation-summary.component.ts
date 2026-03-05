import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { ReportingObligationSummaryTemplateComponent } from '@aviation/shared/components/aer/reporting-obligation-summary-template/reporting-obligation-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { AviationAerRequestMetadata } from 'pmrv-api';

import { ReportingObligation } from '../reporting-obligation.interface';
import { ReportingObligationFormProvider } from '../reporting-obligation-form.provider';

interface ViewModel {
  reportingData: ReportingObligation;
  files: AttachedFile[];
  year: number;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-reporting-obligation-summary',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    RouterLinkWithHref,
    ReportingObligationSummaryTemplateComponent,
    AerReviewDecisionGroupComponent,
  ],
  templateUrl: './reporting-obligation-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReportingObligationSummaryComponent {
  private store = inject(RequestTaskStore);
  private formProvider = inject<ReportingObligationFormProvider>(TASK_FORM_PROVIDER);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectStatusForTask('reportingObligation')),
  ]).pipe(
    map(([type, requestInfo, isEditable, taskStatus]) => {
      return {
        reportingData: this.formProvider.getFormValue(),
        files:
          this.formProvider.reportingObligationDetailsForm?.getRawValue()?.supportingDocuments?.map((doc) => {
            return {
              fileName: doc.file.name,
              downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${doc.uuid}`,
            };
          }) ?? [],
        year: (requestInfo.requestMetadata as AviationAerRequestMetadata).year,
        isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );

  onSubmit() {
    if (this.formProvider.form.valid) {
      this.formProvider.form.updateValueAndValidity();

      this.store.aerDelegate
        .saveAer({ reportingObligation: this.formProvider.getFormValue() }, 'complete')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.store.aerDelegate.setReportingObligation(this.formProvider.getFormValue());

          this.router.navigate(['../../..'], { relativeTo: this.route, replaceUrl: true });
        });
    }
  }
}
