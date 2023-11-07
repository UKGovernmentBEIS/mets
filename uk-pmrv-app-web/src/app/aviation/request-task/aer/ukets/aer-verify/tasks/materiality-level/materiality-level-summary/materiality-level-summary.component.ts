import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { AerVerifyMaterialityLevelGroupComponent } from '@aviation/shared/components/aer-verify/materiality-level-group/materiality-level-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerMaterialityLevel } from 'pmrv-api';

import { aerVerifyQuery } from '../../../aer-verify.selector';
import { MaterialityLevelFormProvider } from '../materiality-level-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  materialityLevel: AviationAerMaterialityLevel;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-materiality-level-summary',
  templateUrl: './materiality-level-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    RouterLinkWithHref,
    AerVerifyMaterialityLevelGroupComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class MaterialityLevelSummaryComponent {
  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: MaterialityLevelFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerVerifyQuery.selectStatusForTask('materialityLevel')),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => {
      return {
        pageHeader: getSummaryHeaderForTaskType(type, 'materialityLevel'),
        isEditable,
        materialityLevel: this.formProvider.getFormValue(),
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyStoreDelegate)
      .saveAerVerify({ materialityLevel: this.formProvider.getFormValue() }, 'complete')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyStoreDelegate).setMaterialityLevel(this.formProvider.getFormValue());

        this.router.navigate(['../../..'], { relativeTo: this.route, replaceUrl: true });
      });
  }
}
