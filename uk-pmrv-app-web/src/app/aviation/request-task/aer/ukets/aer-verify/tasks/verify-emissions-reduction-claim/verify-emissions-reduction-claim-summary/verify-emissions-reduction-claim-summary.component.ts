import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { aerVerifyQuery } from '@aviation/request-task/aer/ukets/aer-verify/aer-verify.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerEmissionsReductionClaimVerification } from 'pmrv-api';

import { VerifyEmissionsReductionClaimFormProvider } from '../verify-emissions-reduction-claim-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  emissionsReductionClaimVerification: AviationAerEmissionsReductionClaimVerification;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-verify-emissions-reduction-claim-summary',
  templateUrl: './verify-emissions-reduction-claim-summary.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, AerVerificationReviewDecisionGroupComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class VerifyEmissionsReductionClaimSummaryComponent {
  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: VerifyEmissionsReductionClaimFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerVerifyQuery.selectStatusForTask('emissionsReductionClaimVerification')),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => {
      return {
        pageHeader: getSummaryHeaderForTaskType(type, 'emissionsReductionClaimVerification'),
        isEditable,
        emissionsReductionClaimVerification: this.formProvider.getFormValue(),
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate)
      .saveAerVerify({ emissionsReductionClaimVerification: this.formProvider.getFormValue() }, 'complete')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setEmissionsReductionClaimVerification(
          this.formProvider.getFormValue(),
        );
        this.router.navigate(['../../..'], { relativeTo: this.route, replaceUrl: true });
      });
  }
}
