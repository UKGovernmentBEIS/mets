import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { DataGapsMethodologiesGroupComponent } from '@aviation/shared/components/aer-verify/data-gaps-methodologies-group/data-gaps-methodologies-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import {
  AviationAerDataGapsMethodologies,
  AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload,
} from 'pmrv-api';

import { aerVerifyQuery } from '../../../aer-verify.selector';

interface ViewModel {
  data: AviationAerDataGapsMethodologies;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-methodologies-summary',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgFor,
    NgIf,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    DataGapsMethodologiesGroupComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  templateUrl: './methodologies-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MethodologiesSummaryComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectRequestTaskPayload),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerVerifyQuery.selectStatusForTask('dataGapsMethodologies')),
  ]).pipe(
    map(([type, payload, isEditable, taskStatus]) => {
      const methodologiesInfo = (payload as AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload)
        .verificationReport.dataGapsMethodologies;
      return {
        data: methodologiesInfo,
        pageHeader: getSummaryHeaderForTaskType(type, 'dataGapsMethodologies'),
        isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
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

  onSubmit() {
    let methodologiesValue: AviationAerDataGapsMethodologies;
    this.store
      .pipe(first(), aerVerifyQuery.selectVerificationReport)
      .pipe(
        switchMap((verificationReportData) => {
          methodologiesValue = verificationReportData.dataGapsMethodologies;
          return (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).saveAerVerify(
            { dataGapsMethodologies: methodologiesValue },
            'complete',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setDataGapsMethodologies({
          ...methodologiesValue,
        });
        this.router.navigate(['../../..'], { relativeTo: this.route });
      });
  }
}
