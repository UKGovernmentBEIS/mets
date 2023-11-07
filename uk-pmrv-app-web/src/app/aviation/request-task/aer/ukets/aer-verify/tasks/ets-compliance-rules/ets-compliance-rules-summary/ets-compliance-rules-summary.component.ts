import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

import { aerVerifyQuery } from '../../../aer-verify.selector';
import { EtsComplianceRulesFormProvider } from '../ets-compliance-rules-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  etsComplianceRules: AviationAerEtsComplianceRules;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-ets-compliance-rules-summary',
  templateUrl: './ets-compliance-rules-summary.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, RouterLinkWithHref, AerVerificationReviewDecisionGroupComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class EtsComplianceRulesSummaryComponent {
  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: EtsComplianceRulesFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerVerifyQuery.selectStatusForTask('etsComplianceRules')),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => {
      return {
        pageHeader: getSummaryHeaderForTaskType(type, 'etsComplianceRules'),
        isEditable,
        etsComplianceRules: this.formProvider.getFormValue(),
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyStoreDelegate)
      .saveAerVerify({ etsComplianceRules: this.formProvider.getFormValue() }, 'complete')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyStoreDelegate).setEtsComplianceRules(
          this.formProvider.getFormValue(),
        );
        this.router.navigate(['../../..'], { relativeTo: this.route, replaceUrl: true });
      });
  }
}
