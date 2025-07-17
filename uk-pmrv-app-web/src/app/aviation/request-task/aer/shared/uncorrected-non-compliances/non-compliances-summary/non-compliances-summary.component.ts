import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { uncorrectedNonCompliancesQuery } from '@aviation/request-task/aer/shared/uncorrected-non-compliances/uncorrected-non-compliances.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerUncorrectedNonCompliances } from 'pmrv-api';

interface ViewModel {
  data: AviationAerUncorrectedNonCompliances;
  pageHeader: string;
  detailsHeader: string;
  isCorsia: boolean;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-non-compliances-summary',
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
  templateUrl: './non-compliances-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonCompliancesSummaryComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(uncorrectedNonCompliancesQuery.selectUncorrectedNonCompliances),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(uncorrectedNonCompliancesQuery.selectStatusForTask('uncorrectedNonCompliances')),
    this.store.pipe(aerQuery.selectIsCorsia),
  ]).pipe(
    map(([type, uncorrectedNonCompliances, isEditable, taskStatus, isCorsia]) => {
      return {
        data: uncorrectedNonCompliances,
        pageHeader: getSummaryHeaderForTaskType(type, 'uncorrectedNonCompliances'),
        detailsHeader: isCorsia
          ? 'Non-compliances with the Air Navigation Order'
          : 'Non-compliances with the monitoring and reporting regulations',
        isCorsia,
        isEditable,
        hideSubmit:
          !isEditable ||
          ['complete', 'cannot start yet'].includes(taskStatus) ||
          (uncorrectedNonCompliances?.exist === true &&
            (!uncorrectedNonCompliances?.uncorrectedNonCompliances ||
              uncorrectedNonCompliances?.uncorrectedNonCompliances.length === 0)),
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
      .pipe(first(), uncorrectedNonCompliancesQuery.selectUncorrectedNonCompliances)
      .pipe(
        switchMap((uncorrectedNonCompliances) => {
          return this.store.aerVerifyDelegate.saveAerVerify(
            { uncorrectedNonCompliances: uncorrectedNonCompliances },
            'complete',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.route }));
  }
}
