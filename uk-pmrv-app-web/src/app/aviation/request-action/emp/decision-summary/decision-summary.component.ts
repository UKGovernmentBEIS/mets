import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { empQuery } from '@aviation/request-action/emp/emp.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { variationRegulatorLedaApprovedRequestActionTypes } from '@aviation/request-action/util';
import { OverallDecisionSummaryTemplateComponent } from '@aviation/shared/components/emp/overall-decision-summary-template/overall-decision-summary-template.component';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import {
  EmpAcceptedVariationDecisionDetails,
  EmpIssuanceCorsiaApplicationApprovedRequestActionPayload,
  EmpIssuanceDetermination,
  EmpIssuanceUkEtsApplicationApprovedRequestActionPayload,
  EmpVariationDetermination,
  EmpVariationReviewDecision,
  FileInfoDTO,
  RequestActionDTO,
} from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  creationDate: string;
  determination: EmpIssuanceDetermination;
  usersInfo:
    | EmpIssuanceUkEtsApplicationApprovedRequestActionPayload['usersInfo']
    | EmpIssuanceCorsiaApplicationApprovedRequestActionPayload['usersInfo'];
  empDocument: FileInfoDTO;
  officialNotice: FileInfoDTO;
  downloadUrl: string;
  showApprovedSummary: boolean;
  variationScheduleItems: string[];
}

@Component({
  selector: 'app-decision-summary',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, RouterModule, OverallDecisionSummaryTemplateComponent],
  templateUrl: './decision-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DecisionSummaryComponent implements OnInit {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(empQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectCreationDate),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(
      ([payload, creationDate, requestActionType]) =>
        ({
          requestActionType,
          pageHeader: this.getDeterminationMap(requestActionType, payload?.determination?.type),
          creationDate,
          determination: variationRegulatorLedaApprovedRequestActionTypes.includes(requestActionType)
            ? {
                type: 'APPROVED',
              }
            : payload.determination,
          usersInfo: payload.usersInfo,
          empDocument: payload.empDocument,
          officialNotice: payload.officialNotice,
          downloadUrl: this.store.empDelegate.baseFileDocumentDownloadUrl + '/',
          showApprovedSummary: variationRegulatorLedaApprovedRequestActionTypes.includes(requestActionType)
            ? true
            : payload.determination.type === 'APPROVED',
          variationScheduleItems: this.getVariationDecisionDetails(
            payload.reviewGroupDecisions,
            payload?.empVariationDetailsReviewDecision,
          ),
        }) as ViewModel,
    ),
  );

  signatory$ = this.store.pipe(
    empQuery.selectRequestActionPayload,
    map((payload) => payload.decisionNotification.signatory),
  );
  operators$ = this.store.pipe(
    empQuery.selectRequestActionPayload,
    map((payload) =>
      Object.keys(payload.usersInfo).filter((userId) => userId !== payload.decisionNotification.signatory),
    ),
  );

  constructor(
    public store: RequestActionStore,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  getDeterminationMap(
    requestActionType: RequestActionDTO['type'],
    determinationType: EmpVariationDetermination['type'],
  ): string {
    if (variationRegulatorLedaApprovedRequestActionTypes.includes(requestActionType)) {
      return 'Approved';
    }

    switch (determinationType) {
      case 'APPROVED':
        return 'Approved';

      case 'REJECTED':
        return 'Rejected';

      case 'DEEMED_WITHDRAWN':
        return 'Deemed withdrawn';
    }
  }

  getVariationDecisionDetails(
    reviewGroupDecisions: { [key: string]: EmpVariationReviewDecision | EmpAcceptedVariationDecisionDetails },
    variationReviewDecision?: EmpVariationReviewDecision,
  ): string[] {
    const variationScheduleItems = reviewGroupDecisions
      ? Object.values(reviewGroupDecisions)
          .map(
            (decision) =>
              (decision as EmpAcceptedVariationDecisionDetails)?.variationScheduleItems ??
              ((decision as EmpVariationReviewDecision)?.details as EmpAcceptedVariationDecisionDetails)
                ?.variationScheduleItems ??
              [],
          )
          .reduce((acc, curVal) => acc.concat(curVal), [])
      : [];

    return variationReviewDecision &&
      (variationReviewDecision.details as EmpAcceptedVariationDecisionDetails)?.variationScheduleItems
      ? [
          ...(variationReviewDecision.details as EmpAcceptedVariationDecisionDetails).variationScheduleItems,
          ...variationScheduleItems,
        ]
      : variationScheduleItems;
  }
}
