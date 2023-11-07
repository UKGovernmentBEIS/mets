import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable, tap } from 'rxjs';

import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { aerVerifyQuery } from '@aviation/request-task/aer/ukets/aer-verify/aer-verify.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerUkEtsVerificationReport } from 'pmrv-api';

import { VerifierDetailsFormProvider } from '../verifier-details-form.provider';
import AerVerifierDetailsGroupFormComponent from '../verifier-details-group-form/verifier-details-group-form.component';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  verificationReportData: AviationAerUkEtsVerificationReport;
  showDecision: boolean;
  hideSubmit: boolean;
}

@Component({
  selector: 'app-aer-verifier-details-summary',
  templateUrl: './verifier-details-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    AerVerifierDetailsGroupFormComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AerVerifierDetailsSummaryComponent {
  private verificationReport: AviationAerUkEtsVerificationReport;

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: VerifierDetailsFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerVerifyQuery.selectVerificationReport),
    this.store.pipe(aerVerifyQuery.selectStatusForTask('verificationReport')),
  ]).pipe(
    map(([type, isEditable, verificationReportData, taskStatus]) => {
      return {
        pageHeader: getSummaryHeaderForTaskType(type, 'verificationReport'),
        isEditable,
        verificationReportData,
        showDecision: showReviewDecisionComponent.includes(type),
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
      };
    }),
    tap((data) => {
      this.verificationReport = data.verificationReportData;
    }),
  );

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyStoreDelegate)
      .saveAerVerify({ verificationReport: this.verificationReport }, 'complete')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyStoreDelegate).setVerificationReport(this.verificationReport);
        this.router.navigate(['../../..'], { relativeTo: this.route, replaceUrl: true });
      });
  }
}
