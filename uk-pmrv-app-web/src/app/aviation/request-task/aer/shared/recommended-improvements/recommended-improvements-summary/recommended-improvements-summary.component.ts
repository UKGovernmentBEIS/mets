import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { recommendedImprovementsQuery } from '@aviation/request-task/aer/shared/recommended-improvements/recommended-improvements.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { RecommendedImprovementsGroupComponent } from '@aviation/shared/components/aer-verify/recommended-improvements-group/recommended-improvements-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerRecommendedImprovements } from 'pmrv-api';

interface ViewModel {
  data: AviationAerRecommendedImprovements;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-recommended-improvements-summary',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgFor,
    NgIf,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    RecommendedImprovementsGroupComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  templateUrl: './recommended-improvements-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendedImprovementsSummaryComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(recommendedImprovementsQuery.selectRecommendedImprovements),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(recommendedImprovementsQuery.selectStatusForTask('recommendedImprovements')),
  ]).pipe(
    map(([type, recommendedImprovements, isEditable, taskStatus]) => {
      return {
        data: recommendedImprovements,
        pageHeader: getSummaryHeaderForTaskType(type, 'recommendedImprovements'),
        isEditable,
        hideSubmit:
          !isEditable ||
          ['complete', 'cannot start yet'].includes(taskStatus) ||
          (recommendedImprovements?.exist === true &&
            (!recommendedImprovements?.recommendedImprovements ||
              recommendedImprovements?.recommendedImprovements.length === 0)),
        showDecision: showReviewDecisionComponent.includes(type),
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
      .pipe(first(), recommendedImprovementsQuery.selectRecommendedImprovements)
      .pipe(
        switchMap((recommendedImprovements) => {
          return this.store.aerVerifyDelegate.saveAerVerify(
            { recommendedImprovements: recommendedImprovements },
            'complete',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.route }));
  }
}
