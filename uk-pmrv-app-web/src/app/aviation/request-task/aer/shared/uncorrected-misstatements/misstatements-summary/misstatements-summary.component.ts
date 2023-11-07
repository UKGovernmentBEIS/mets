import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { uncorrectedMisstatementsQuery } from '@aviation/request-task/aer/shared/uncorrected-misstatements/uncorrected-misstatements.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerUncorrectedMisstatements } from 'pmrv-api';

interface ViewModel {
  data: AviationAerUncorrectedMisstatements;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-misstatements-summary',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgFor,
    NgIf,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    UncorrectedItemGroupComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  templateUrl: './misstatements-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MisstatementsSummaryComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(uncorrectedMisstatementsQuery.selectUncorrectedMisstatements),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(uncorrectedMisstatementsQuery.selectStatusForTask('uncorrectedMisstatements')),
  ]).pipe(
    map(([type, uncorrectedMisstatements, isEditable, taskStatus]) => {
      return {
        data: uncorrectedMisstatements,
        pageHeader: 'Check Your Answers',
        isEditable,
        hideSubmit:
          !isEditable ||
          ['complete', 'cannot start yet'].includes(taskStatus) ||
          (uncorrectedMisstatements?.exist === true &&
            (!uncorrectedMisstatements?.uncorrectedMisstatements ||
              uncorrectedMisstatements?.uncorrectedMisstatements.length === 0)),
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
      .pipe(first(), uncorrectedMisstatementsQuery.selectUncorrectedMisstatements)
      .pipe(
        switchMap((uncorrectedMisstatements) => {
          return this.store.aerVerifyDelegate.saveAerVerify(
            { uncorrectedMisstatements: uncorrectedMisstatements },
            'complete',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.route }));
  }
}
