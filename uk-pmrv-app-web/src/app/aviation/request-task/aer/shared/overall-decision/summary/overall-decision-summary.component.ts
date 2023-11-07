import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { overallDecisionQuery } from '@aviation/request-task/aer/shared/overall-decision/overall-decision.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { OverallDecisionGroupComponent } from '@aviation/shared/components/aer-verify/overall-decision-group/overall-decision-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import {
  AviationAerNotVerifiedDecision,
  AviationAerVerifiedSatisfactoryDecision,
  AviationAerVerifiedSatisfactoryWithCommentsDecision,
} from 'pmrv-api';

interface ViewModel {
  data:
    | AviationAerVerifiedSatisfactoryDecision
    | AviationAerVerifiedSatisfactoryWithCommentsDecision
    | AviationAerNotVerifiedDecision;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
  isCorsia: boolean;
}

@Component({
  selector: 'app-overall-decision-summary',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgFor,
    NgIf,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    OverallDecisionGroupComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  templateUrl: './overall-decision-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionSummaryComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(overallDecisionQuery.selectOverallDecision),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(overallDecisionQuery.selectStatusForTask('overallDecision')),
    this.store.pipe(aerQuery.selectIsCorsia),
  ]).pipe(
    map(([type, overallDecision, isEditable, taskStatus, isCorsia]) => {
      return {
        data: overallDecision,
        pageHeader: 'Check Your Answers',
        isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
        isCorsia,
      };
    }),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    this.store
      .pipe(first(), overallDecisionQuery.selectOverallDecision)
      .pipe(
        switchMap((overallDecision) => {
          return this.store.aerVerifyDelegate.saveAerVerify({ overallDecision: overallDecision }, 'complete');
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.route }));
  }
}
